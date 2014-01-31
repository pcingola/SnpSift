package ca.mcgill.mcb.pcingola.snpSift.epistasis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import akka.actor.Actor;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.ped.PedPedigree;
import ca.mcgill.mcb.pcingola.ped.TfamEntry;
import ca.mcgill.mcb.pcingola.snpSift.SnpSift;
import ca.mcgill.mcb.pcingola.snpSift.caseControl.SnpSiftCmdCaseControl;
import ca.mcgill.mcb.pcingola.snpSift.epistasis.akka.MasterEpistasis;
import ca.mcgill.mcb.pcingola.snpSift.epistasis.akka.WorkQueueEpistasis;
import ca.mcgill.mcb.pcingola.stats.CountByType;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Simple 'epistatic' case-control analysis
 * 
 * @author pcingola
 */
public class SnpSiftCmdEpistasis extends SnpSiftCmdCaseControl {

	public enum ModelCoevolution {
		ABS, MAX, LD
	};

	public enum ModelPvalue {
		Allelic, Dominant, Recessive, Codominant
	};

	public static final int SHOW_EVERY = 1000;

	boolean isMulti;
	boolean analyzeEntry[]; // Should we analyze this entry? Does it match a gene or a position that we want to analyze?
	int minAlleleCount;
	int minDistance;
	int showNonSignificant; // Non significant test can be shown every now and then (if this value is positive). This is useful to get the full statistics spectrum (e.g. QQ plots)
	long countTests;
	String rResultsFileName, rGtFileName;
	ModelCoevolution model;
	List<String> sampleIds;
	ArrayList<byte[]> genotypes;
	ArrayList<String> entryId;
	String chromo[];
	int pos[];
	HashSet<String> genes, chrPos;
	BufferedWriter rFile;

	public SnpSiftCmdEpistasis(String args[]) {
		super(args);
		command = "coevolution";
	}

	/**
	 * Should we analyze this entries
	 * @param i
	 * @param j
	 * @return
	 */
	boolean analyzeEntries(int i, int j) {
		return analyzeEntry[i] || analyzeEntry[j];
	}

	/**
	 * Should we analyze this entry?
	 * @param i
	 * @return
	 */
	boolean analyzeEntry(int i) {
		// Do we have a preferred list of genes or positions to focus on?
		if ((genes == null) && (chrPos == null)) return true; // No 'genes' or 'chrPos' set? Everything matches

		// Matching gene name to 'genes'.
		// Get genes from entryId
		String eid[] = entryId.get(i).split(" ");
		if (genes != null) {
			if (eid.length >= 2) {
				// Any match?
				String genesStr = eid[1];
				for (String g : genesStr.split(","))
					if (genes.contains(g)) return true;
			}
		}

		// Does position match 'chrPos'
		if (chrPos != null) {
			String chrPosStr = eid[0].split("_")[0];
			return chrPos.contains(chrPosStr);
		}

		return false;
	}

	void closeRfile() {
		try {
			if (rFile != null) rFile.close();
		} catch (Exception e) {
			throw new RuntimeException("Error closing R file.", e);
		}
	}

	/**
	 * Distance between two markers
	 * @param i1 : Marker 1 index
	 * @param i2 : Marker 2 index
	 * @return
	 */
	public long distance(int i1, int i2) {
		// Different chromosome? Distance is 'infinite'
		if (!chromo[i1].equals(chromo[i2])) return Long.MAX_VALUE;

		// Positions
		long pos1 = pos[i1];
		long pos2 = pos[i2];

		return (pos1 > pos2 ? pos1 - pos2 : pos2 - pos1);
	}

	/**
	 * Create an ID for this VCF entry
	 * @param ve
	 * @return
	 */
	String entryId(VcfEntry ve) {
		// Try to get gene names
		HashSet<String> done = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for (VcfEffect veff : ve.parseEffects()) {
			String gene = veff.getGene();
			if ((gene != null) && !done.contains(gene)) {
				sb.append((sb.length() > 0 ? "," : "") + gene);
				done.add(gene);
			}
		}

		// ID
		return ve.getChromosomeName() //
				+ ":" + (ve.getStart() + 1) //
				+ "_" + ve.getRef() //
				+ "/" + ve.getAltsStr() //
				+ " " + sb.toString() //
				+ " " + ve.getId() //
		;
	}

	int epistasisCode(int code1, int code2) {
		switch (model) {
		case ABS:
			/**
			 * Codes conversion table
			 * 
			 * 		code 1:    0/0      0/1      1/1
			 *                   0        1        2
			 *     code 2   +------------------------+
			 *     0/0   0  |    0        1        2 |
			 *     0/1   1  |    1        0        1 |
			 *     1/1   2  |    2        1        0 |
			 *              +------------------------+
			 */
			return Math.abs(code1 - code2);

		case MAX:
			/**
			 * Codes conversion table
			 * 
			 * 		code 1:    0/0      0/1      1/1
			 *                   0        1        2
			 *     code 2   +------------------------+
			 *     0/0   0  |    0        1        2 |
			 *     0/1   1  |    0        0        1 |
			 *     1/1   2  |    0        0        0 |
			 *              +------------------------+
			 */
			return Math.max(code1 - code2, 0);

		default:
			throw new RuntimeException("Unimplemented model '" + model + "'");
		}
	}

	/**
	 * Obtain genotype scores
	 * @param ve
	 * @return
	 */
	byte[] genotypesScores(VcfEntry ve) {
		byte gs[] = ve.getGenotypesScores();

		// Do we need to swap alleles (REF <-> ALT)
		// Note: We swap alleles in order to have 'ALT' always being the minor allele
		int count = 0, alleles = 0;
		for (int i = 0; i < gs.length; i++) {
			if (gs[i] >= 0) {
				count += 2;
				alleles += gs[i];
			}
		}

		// ALT is more common than REF? Swap alleles 
		if (alleles > count / 2) {
			if (debug) Gpr.debug("Swapping alleles for entry: " + ve.toStr());
			for (int i = 0; i < gs.length; i++)
				if (gs[i] > 0) gs[i] = (byte) (2 - gs[i]);
		}

		return gs;
	}

	public ArrayList<String> getEntryId() {
		return entryId;
	}

	public ArrayList<byte[]> getGenotypes() {
		return genotypes;
	}

	/**
	 * Count how many tests have been performed
	 * @return
	 */
	synchronized long incCountTests() {
		return ++countTests;
	}

	@Override
	public void init() {
		minAlleleCount = 10;
		pvalueThreshold = 1e-4;
		model = ModelCoevolution.ABS;
		rResultsFileName = null; // Don't write results unless specified in the command line
		rGtFileName = null; // Don't write genotypes unless specified in the command line
		showNonSignificant = 0; // By default do not show any non-significant results
		countTests = 0;
		minDistance = 1000000; // Minimum distance between two loci for LD analysis
	}

	/**
	 * Initialize genesMatch
	 */
	void initMatchGenes() {
		analyzeEntry = new boolean[genotypes.size()];
		for (int i = 0; i < analyzeEntry.length; i++)
			analyzeEntry[i] = analyzeEntry(i);
	}

	/**
	 * Load data
	 */
	public void load() {
		loadTfam();
		loadVcf();
		initMatchGenes();
	}

	/**
	 * Load TFAM file
	 */
	void loadTfam() {
		// Load TFAM
		if (verbose) Timer.showStdErr("Reading TFAM file '" + tfamFile + "'");
		pedigree = new PedPedigree();
		pedigree.loadTfam(tfamFile);
	}

	/**
	 * Load VCF file
	 */
	void loadVcf() {
		// Initialize
		genotypes = new ArrayList<byte[]>();
		entryId = new ArrayList<String>();
		ArrayList<String> chromoList = new ArrayList<String>();
		ArrayList<Integer> posList = new ArrayList<Integer>();
		double minp = 1.0;

		// Open 'R' file
		openRfile(rGtFileName, "Writing genotypes to file");

		// Read file
		if (verbose) Timer.showStdErr("Reading input file '" + vcfFileName + "'");
		VcfFileIterator vcf = new VcfFileIterator(vcfFileName);
		for (VcfEntry ve : vcf) {
			if (vcf.isHeadeSection()) {
				parseVcfHeader(vcf);
			}

			// Use if at least 'MIN_MAC' minor allele counts			
			int mac = ve.mac();
			if (ve.isVariant() && (mac >= minAlleleCount)) {
				byte genoScores[] = genotypesScores(ve);

				genotypes.add(genoScores);
				entryId.add(entryId(ve));
				chromoList.add(ve.getChromosomeName());
				posList.add(ve.getStart());

				// Show line
				if (debug) {
					// Calculate p-values
					double pvalue = pValueFisher(genoScores);
					double logp = -Math.log10(pvalue);
					minp = Math.min(pvalue, minp);

					System.out.println(String.format("%s:%d\t%d\t%.2f\t%.4e\t%.4e\t", ve.getChromosomeName(), ve.getStart(), ve.mac(), logp, pvalue, minp));
				}

				// Write genotypes to "R" file
				writeRGt(genoScores);
			}
		}

		if (verbose) Timer.showStdErr("Finished loading VCF. Loaded: " + genotypes.size() + " lines." + (minp < 1 ? "\n\tMin p-value: " + minp : ""));
		closeRfile();

		// Create chromo and pos arrays
		chromo = chromoList.toArray(new String[0]);
		pos = new int[posList.size()];
		int i = 0;
		for (int p : posList)
			pos[i++] = p;
	}

	/**
	 * Minor allele count
	 * @param scores
	 * @return
	 */
	int mac(byte scores[]) {
		int count = 0;
		for (int i = 0; i < scores.length; i++)
			if (scores[i] > 0) count += scores[i];
		return count;
	}

	/**
	 * Find the minimum non-zero pvalue
	 * Note: If a p-value is exactly zero, there is something probably wrong with the statistic.
	 * @param pvalues
	 * @return
	 */
	double minPvalue(double pvalues[]) {
		double pvalue = 1.0;
		for (int i = 0; i < pvalues.length; i++) {
			double p = pvalues[i];
			if (p <= 0.0) p = 1.0;
			pvalue = Math.min(pvalue, p);
		}
		return pvalue;

	}

	/**
	 * Open a file and write data to be parsed by an R program
	 * @param fileName
	 * @param message
	 */
	void openRfile(String fileName, String message) {
		try {
			if (fileName != null) {
				if (verbose) Timer.showStdErr(message + " '" + fileName + "'");
				rFile = new BufferedWriter(new FileWriter(fileName));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error opening file '" + fileName + "'", e);
		}
	}

	@Override
	public void parse(String[] args) {
		if (args.length <= 0) usage(null);

		for (int argc = 0; argc < args.length; argc++) {
			String arg = args[argc];

			if (isOpt(arg)) {
				// Command line options
				if (arg.equalsIgnoreCase("-minAc")) minAlleleCount = Gpr.parseIntSafe(args[++argc]);
				else if (arg.equalsIgnoreCase("-show")) showNonSignificant = Gpr.parseIntSafe(args[++argc]);
				else if (arg.equalsIgnoreCase("-maxP")) pvalueThreshold = Gpr.parseDoubleSafe(args[++argc]);
				else if (arg.equalsIgnoreCase("-model")) model = ModelCoevolution.valueOf(args[++argc].toUpperCase());
				else if (arg.equalsIgnoreCase("-out")) {
					rResultsFileName = args[++argc];
					// Create a name for genotype file
					if (rGtFileName == null) rGtFileName = Gpr.dirName(rResultsFileName) + "/" + Gpr.baseName(rResultsFileName, Gpr.extName(rResultsFileName)) + "gt.txt";
				} else if (arg.equalsIgnoreCase("-outGt")) {
					rGtFileName = args[++argc];
				} else if (arg.equalsIgnoreCase("-genes")) {
					String genesStr = args[++argc];
					genes = new HashSet<String>();
					for (String g : genesStr.split(","))
						genes.add(g);
				} else if (arg.equalsIgnoreCase("-pos")) {
					String genesStr = args[++argc];
					chrPos = new HashSet<String>();
					for (String cp : genesStr.split(","))
						chrPos.add(cp);
				}
			} else if (tfamFile == null) tfamFile = arg;
			else if (vcfFileName == null) vcfFileName = arg;
			else usage("Unkown parameter '" + arg + "'");
		}

		// Sanity check
		if (vcfFileName == null) usage("Missing paramter 'file.vcf'");
		if (tfamFile == null) usage("Missing parameter 'file.tfam'");
		if (showNonSignificant < 0) usage("Parameter '-show' must have a non-negative number");
		if ((pvalueThreshold <= 0) || (pvalueThreshold > 1)) usage("Parameter '-maxP' must have a number between 0 and 1. Current value: " + pvalueThreshold);
	}

	/**
	 * Parse VCF header to get sample IDs
	 * @param vcf
	 */
	List<String> parseVcfHeader(VcfFileIterator vcf) {
		// Get sample names
		sampleIds = vcf.getSampleNames();

		// Initialize sampleNum2group mapping
		CountByType countByGroup = new CountByType();
		caseControl = new Boolean[sampleIds.size()];
		int sampleNum = 0, missing = 0;
		for (String id : sampleIds) {
			TfamEntry tfam = pedigree.get(id);
			String groupId = "";

			if (tfam == null) {
				missing++;
				System.err.println("WARNING: VCF sample '" + id + "' not found in TFAM file.");
			} else {
				// Assign group
				caseControl[sampleNum] = tfam.isMissing() ? null : tfam.isCase();
				groupId = tfam.getFamilyId();
			}

			countByGroup.inc(groupId);
			sampleNum++;
		}

		if (missing == sampleIds.size()) throw new RuntimeException("All samples are missing in TFAM file!");

		// Show counts by group
		if (verbose) {
			System.err.println("Counts by group:\nGroup\tCount\n" + countByGroup);
			if (missing > 0) System.err.println("Samples from VCF missing in TFAM: " + missing);
			System.err.println("Total samples in VCF: " + sampleIds.size());
		}

		return sampleIds;
	}

	/**
	 * Power calculation
	 */
	void powerCalculation() {
		int nCase[] = new int[3];
		int nControl[] = new int[3];
		int cases = 0, controls = 0;

		// Count cases & controls
		for (int idx = 0; idx < caseControl.length; idx++) {
			if ((caseControl[idx] != null)) {
				// Count a/a, a/A and A/A
				if (caseControl[idx]) cases++;
				else controls++;
			}
		}

		Timer.showStdErr("Power calculation:" //
				+ "\n\tCases    : " + cases //
				+ "\n\tControls : " + controls //
		);

		int numEntries = genotypes.size();
		double pvalueSignificant = 0.05 / (numEntries * numEntries);

		// Calculate pvalues (best case scenario)
		String powerStr = "";
		for (int numCases = cases; numCases > 0; numCases--) {
			nCase[0] = 2 * (cases - numCases);
			nCase[1] = numCases;
			nCase[2] = 0;

			nControl[0] = 2 * controls;
			nControl[1] = 0;
			nControl[2] = 0;

			double pvalues[] = pvaluesFisher(nCase, nControl, pvalueThreshold);
			double pvalue = minPvalue(pvalues);
			if (debug && pvalue <= pvalueThreshold) System.out.println("NumCases : " + numCases + "\tp-value: " + pvalue);
			if (pvalue <= pvalueSignificant) powerStr = String.format("" //
					+ "\n\tSignificance threshold : %e" //
					+ "\n\tNumber of cases        : %d" //
					+ "\n\tp-value                : %e" //
			, pvalueSignificant, numCases, pvalue);
		}

		Timer.showStdErr("Done:" + powerStr);
	}

	/**
	 * Calculate pValue
	 * @param genoScores
	 * @return
	 */
	double pValueFisher(byte genoScores[]) {
		int casesHom = 0, casesHet = 0, cases = 0;
		int ctrlHom = 0, ctrlHet = 0, ctrl = 0;
		int nCase[] = new int[3];
		int nControl[] = new int[3];

		// Count genotypes
		for (int idx = 0; idx < genoScores.length; idx++) {
			if ((caseControl[idx] != null)) {
				int code = genoScores[idx];

				if (code >= 0) {
					int codeMissing = Math.max(0, code);

					// Count a/a, a/A and A/A
					if (caseControl[idx]) nCase[code]++;
					else nControl[code]++;

					if (caseControl[idx]) {
						// Case sample
						if (code < 0) ; // Missing? => Do not count
						else if (code == 2) casesHom++;
						else casesHet++;

						cases += codeMissing;
					} else {
						//Control sample
						if (code < 0) ; // Missing? => Do not count
						else if (code == 2) ctrlHom++;
						else ctrlHet++;

						ctrl += codeMissing;
					}
				}
			}
		}

		// Add info fields
		if (debug) Gpr.debug("\n\tCases: " + casesHom + "," + casesHet + "," + cases + "\n\tControls: " + ctrlHom + "," + ctrlHet + "," + ctrl);

		return minPvalue(pvaluesFisher(nControl, nCase, pvalueThreshold));
	}

	/**
	 * Compare p-values between two genotypes using Fisher model
	 */
	public double[] pValueFisher(int i1, int i2) {
		byte scores1[] = genotypes.get(i1);
		byte scores2[] = genotypes.get(i2);
		byte codes[] = new byte[scores1.length];
		int casesHom = 0, casesHet = 0, cases = 0;
		int ctrlHom = 0, ctrlHet = 0, ctrl = 0;
		int nCase[] = new int[3];
		int nControl[] = new int[3];
		int checkSum1 = 0, checkSum2 = 0;

		// Count genotypes
		for (int idx = 0; idx < scores1.length; idx++) {
			codes[idx] = -1;

			int code1 = scores1[idx];
			int code2 = scores2[idx];

			checkSum1 += code1;
			checkSum2 += code2;

			if ((caseControl[idx] != null)) {
				if ((code1 >= 0) && (code2 >= 0)) {
					// Summarize two codes into one
					int code = epistasisCode(code1, code2);
					codes[idx] = (byte) code;

					// Count a/a, a/A and A/A
					if (caseControl[idx]) nCase[code]++;
					else nControl[code]++;

					if (caseControl[idx]) {
						// Case sample
						if (code < 0) ; // Missing? => Do not count
						else if (code == 2) casesHom++;
						else casesHet++;

						cases += code;
					} else {
						//Control sample
						if (code < 0) ; // Missing? => Do not count
						else if (code == 2) ctrlHom++;
						else ctrlHet++;

						ctrl += code;
					}
				}
			}
		}

		//---
		// Calculate pValues
		//---

		// We only show results if p-value is less than maxP 
		// If showNonSignificant is set, we show 
		// results once every 'showNonSignificant'
		long count = incCountTests();
		boolean show = false;
		double pvalueTh = pvalueThreshold;
		if ((showNonSignificant > 0) && (count % showNonSignificant == 0)) {
			show = true;
			// Are we forcing to show results? Then we should calculate p-value even if it is high
			pvalueTh = 1.0;
		}

		double pvalues[] = pvaluesFisher(nCase, nControl, pvalueTh);
		double pvalue = minPvalue(pvalues);

		// Sanity check
		if (pvalue == 0.0) Gpr.debug("p-value is zero!" + "\n\t" + entryId.get(i1) + "\t" + entryId.get(i2) + "\n\tCases: " + casesHom + "," + casesHet + "," + cases + "\n\tControls: " + ctrlHom + "," + ctrlHet + "," + ctrl);
		if (debug) Gpr.debug(entryId.get(i1) + "\t" + entryId.get(i2) + "\n\tCases: " + casesHom + "," + casesHet + "," + cases + "\n\tControls: " + ctrlHom + "," + ctrlHet + "," + ctrl);

		// If p-value is less than 'pvalueThreshold' we should show the result (it is 'significant')
		show |= (pvalue <= pvalueThreshold);

		if (show) {
			writeRResultsFisher(i1, i2, pvalues, checkSum1, checkSum2);
			return pvalues;
		}
		return null;
	}

	/**
	 * Calculate using LD model
	 * References : "Test for interaction between two unlinked loci", Zhao et. al. 2006
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public double pValueLd(int i1, int i2) {
		// The method assumes that loci are not in LD, so we have to make sure 
		// that they are far away from each other. Otherwise we invalidate 
		// the main hypothesis
		long dist = distance(i1, i2);
		if (dist < minDistance) return Double.NaN;

		byte scores1[] = genotypes.get(i1);
		byte scores2[] = genotypes.get(i2);

		int count_D1 = 0, counta_D1 = 0;
		int count_D2 = 0, counta_D2 = 0;
		int count_11 = 0, counta_11 = 0;
		int n_G = 0, n_A = 0;

		int checkSum1 = 0, checkSum2 = 0;
		for (int idx = 0; idx < scores1.length; idx++) {
			int code1 = scores1[idx];
			int code2 = scores2[idx];

			checkSum1 += code1;
			checkSum2 += code2;

			// We make sure case/control status AND both genotypes are available
			if ((caseControl[idx] != null) && (code1 >= 0) && (code2 >= 0)) {
				if (caseControl[idx]) {
					// Affected population
					n_A++;
					if (code1 == 0) counta_D1++;
					if (code2 == 0) counta_D2++;
					if ((code1 == 0) && (code2 == 0)) counta_11++;
				} else {
					// General population
					n_G++;
					if (code1 == 0) count_D1++;
					if (code2 == 0) count_D2++;
					if ((code1 == 0) && (code2 == 0)) count_11++;
				}
			}
		}

		// General population
		double dng = n_G;
		double p_D1 = count_D1 / dng;
		double p_D2 = count_D2 / dng;
		double p_11 = count_11 / dng;
		double delta_N = p_11 - p_D1 * p_D2;
		double V_N = (p_D1 * (1.0 - p_D1) * p_D2 * (1.0 - p_D2) + (1.0 - 2.0 * p_D1) * (1.0 - 2.0 * p_D2) * delta_N - delta_N * delta_N) / (2.0 * n_G);

		// Affected population
		double dna = n_A;
		double pa_D1 = counta_D1 / dna;
		double pa_D2 = counta_D2 / dna;
		double pa_11 = counta_11 / dna;
		double delta_A = pa_11 - pa_D1 * pa_D2;
		double V_A = (pa_D1 * (1.0 - pa_D1) * pa_D2 * (1.0 - pa_D2) + (1.0 - 2.0 * pa_D1) * (1.0 - 2.0 * pa_D2) * delta_A - delta_A * delta_A) / (2.0 * n_A);

		// Test statistic (see paper, equation 5)
		double T = ((delta_A - delta_N) * (delta_A - delta_N)) / (V_A + V_N);

		//---
		// Calculate pValues
		//---
		boolean show = false;
		long count = incCountTests();
		if ((showNonSignificant > 0) && (count % showNonSignificant == 0)) show = true; // Are we forcing to show results? Then we should calculate p-value even if it is high

		// According to the paper, T is distributed as a Chi-Squared with 1 degree of freedom
		double pvalue = 1.0 - flanagan.analysis.Stat.chiSquareCDF(T, 1);
		if (debug) {
			System.err.println("Markers: " + entryId.get(i1) + "\t" + entryId.get(i2) //  
					+ "\n\tp-value     : " + pvalue //
					+ "\n\tT statistic : " + T //
					+ "\n" //
					+ "\n\tN_G         : " + n_G //
					+ "\n\tcount_D1    : " + count_D1 //
					+ "\n\tcount_D2    : " + count_D2 //
					+ "\n\tcount_11    : " + count_11 //
					+ "\n\tp_D1        : " + p_D1 //
					+ "\n\tp_D2        : " + p_D2 //
					+ "\n\tp_11        : " + p_11 //
					+ "\n\tdelta_N     : " + delta_N //
					+ "\n\tV_N         : " + V_N //
					+ "\n" //
					+ "\n\tN_A         : " + n_A //
					+ "\n\tcounta_D1   : " + counta_D1 //
					+ "\n\tcounta_D2   : " + counta_D2 //
					+ "\n\tcounta_11   : " + counta_11 //
					+ "\n\tpa_D1       : " + pa_D1 //
					+ "\n\tpa_D2       : " + pa_D2 //
					+ "\n\tpa_11       : " + pa_11 //
					+ "\n\tdelta_A     : " + delta_A //
					+ "\n\tV_A         : " + V_A //
			);
		}

		// Sanity check
		if (pvalue == 0.0) Gpr.debug("p-value is zero!" + "\n\t" + entryId.get(i1) + "\t" + entryId.get(i2));
		if (debug) Gpr.debug(entryId.get(i1) + "\t" + entryId.get(i2));

		// If p-value is less than 'pvalueThreshold' we should show the result (it is 'significant')
		show |= (pvalue <= pvalueThreshold);

		if (show) {
			writeRResultsLd(i1, i2, pvalue, checkSum1, checkSum2);
			return pvalue;
		}
		return Double.NaN;
	}

	/**
	 * Calculate the best p-value
	 * 
	 * Note: It looks like we could remove the recessive model 
	 *       test (QQ plots show it's not doing much).
	 *       Furthermore, using only Allellic model might be 
	 *       enough...
	 * 
	 * @param nCase
	 * @param nControl
	 * @return
	 */
	double[] pvaluesFisher(int nCase[], int nControl[], double pvalueTh) {
		// pValues
		double pTrend = pTrend(nControl, nCase);
		double pAllelic = pAllelic(nControl, nCase, pvalueTh);
		double pDominant = pDominant(nControl, nCase, pvalueTh);
		swapMinorAllele(nControl, nCase); // Swap if minor allele is reference. Should not be necesary, since genotypesScores() already swaps them
		double pRecessive = pRecessive(nControl, nCase, pvalueTh);

		// Return an array of pvalues
		double pvalues[] = { pAllelic, pDominant, pRecessive, pTrend };
		return pvalues;
	}

	/**
	 * Run 
	 */
	@Override
	public void run() {
		load(); // Load data

		// Run
		if (debug) powerCalculation();
		runEpistasis();
	}

	void runEpistasis() {
		// Open 'R' file
		openRfile(rResultsFileName, "Writing results to file");
		writeRResultsTitle();

		if (numWorkers <= 1) runEpistasisSingle();
		else runEpistasisMulti();

		closeRfile();
	}

	/**
	 * Analyze one entry compared to all other entries
	 * @param i
	 * @return
	 */
	public String runEpistasis(int i) {
		int numEntries = genotypes.size();
		int count = 0;
		StringBuilder sb = new StringBuilder();

		// Iteration start
		// Note: 'Abs' model yields the same result for [i, j] than [j, i] whereas 'max' model doesn't
		int minj = i + 1;
		if (model == ModelCoevolution.MAX) minj = 0;

		// Iterate on all entries
		for (int j = minj; j < numEntries; j++) {
			if (i != j) {
				// Does this entry match 'genes'?
				if (analyzeEntries(i, j)) {

					boolean show = false;
					switch (model) {
					case LD:
						double pvalue = pValueLd(i, j);
						if (!Double.isNaN(pvalue)) {
							show = true;
							String out = String.format("Result\t%.4e\t%.4e\t%.4e\t%.4e\t[%d, %d]\t%s\t%s\t%d\t%d", pvalue, 1.0, 1.0, 1.0, i, j, entryId.get(i), entryId.get(j), mac(genotypes.get(i)), mac(genotypes.get(j)));
							if (!isMulti) System.out.println(out);
							else sb.append((sb.length() > 0 ? "\n" : "") + out);
						}
						break;

					case MAX:
					case ABS:
						double pvalues[] = null;
						pvalues = pValueFisher(i, j);
						if (pvalues != null) {
							show = true;
							String out = String.format("Result\t%.4e\t%.4e\t%.4e\t%.4e\t[%d, %d]\t%s\t%s\t%d\t%d", pvalues[0], pvalues[1], pvalues[2], pvalues[3], i, j, entryId.get(i), entryId.get(j), mac(genotypes.get(i)), mac(genotypes.get(j)));
							if (!isMulti) System.out.println(out);
							else sb.append((sb.length() > 0 ? "\n" : "") + out);
						}
						break;

					default:
						throw new RuntimeException("Unimplemented model " + model);
					}
					count++;

					if (!show && !isMulti) Gpr.showMark(count, SHOW_EVERY);
				}
			}
		}

		if (verbose && !isMulti) Timer.showStdErr(entryId.get(i) + "\t" + i + "/" + numEntries);

		return sb.toString();
	}

	/**
	 * Run co-evolutionary algorithm
	 */
	void runEpistasisMulti() {
		if (verbose) Timer.showStdErr("Starting analysis on " + numWorkers + " cpus.");

		isMulti = true;

		final SnpSiftCmdEpistasis snpSiftCmdEpistasis = this;
		Props props = new Props(new UntypedActorFactory() {

			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() {
				MasterEpistasis master = new MasterEpistasis(numWorkers, snpSiftCmdEpistasis);
				return master;
			}
		});

		int batchSize = 1;
		WorkQueueEpistasis workQueueEpistasis = new WorkQueueEpistasis(batchSize, 1, props);
		workQueueEpistasis.run(true);

		if (verbose) Timer.showStdErr("Done. Total tests performed: " + countTests);
	}

	/**
	 * Run co-evolutionary algorithm
	 */
	void runEpistasisSingle() {
		if (verbose) Timer.showStdErr("Starting analysis.");

		for (int i = 0; i < genotypes.size(); i++)
			runEpistasis(i);

		if (verbose) Timer.showStdErr("Done. Total tests performed: " + countTests);
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar coevolution file.tfam file.vcf");
		System.err.println("Where:");
		System.err.println("\t-genes <list>   : Comma separated list of genes to analyze. Default: None");
		System.err.println("\t-maxP  <num>    : Maximum p-value to report. Default: " + pvalueThreshold);
		System.err.println("\t-minAc <num>    : Filter using minimum number of alleles. Default: " + minAlleleCount);
		System.err.println("\t-model <model>  : Model to use {ABS, MAX, LD}. Default: " + model);
		System.err.println("\t-out    <file>  : Write results to 'file'. Default: " + rResultsFileName);
		System.err.println("\t-outGt  <file>  : Write genotypes to 'file'. Default: " + rGtFileName);
		System.err.println("\t-pos <list>     : Comma separated list of genomic positions to analyze (chr:pos). Default: None");
		System.err.println("\t-show  <num>    : Show non-significant p-values every 'num' iterations. Default: " + showNonSignificant);
		System.err.println("\tfile.tfam       : A TFAM file having case/control informations (phenotype colmun)");
		System.err.println("\tfile.vcf        : A VCF file (variants and genotype data)");
		System.exit(1);
	}

	/**
	 * Make sure only one process is writing at the same time
	 * @param str
	 */
	synchronized void writeR(String str) {
		try {
			if (rFile != null) rFile.write(str);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write genotypes to an R file 
	 * @param idx
	 * @param codes
	 */
	void writeRGt(byte codes[]) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < codes.length; i++)
			sb.append((sb.length() > 0 ? "\t" : "") + codes[i]);
		sb.append('\n');

		// Write it to file
		writeR(sb.toString());
	}

	/**
	 * Show in a way that R can read (logistic regression)
	 * @param i1
	 * @param i2
	 * @param codes
	 */
	void writeRResultsFisher(int i1, int i2, double pvalues[], int checkSum1, int checkSum2) {
		StringBuilder sb = new StringBuilder();

		// Add indexes (one-based)
		sb.append(i1 + 1);
		sb.append('\t');
		sb.append(i2 + 1);
		sb.append('\t');

		// Add variant data
		sb.append(entryId.get(i1).replace(' ', '\t'));
		sb.append('\t');
		sb.append(entryId.get(i2).replace(' ', '\t'));

		// Add p-values
		for (int i = 0; i < pvalues.length; i++)
			sb.append("\t" + pvalues[i]);

		// Add checksums
		sb.append("\t" + checkSum1 + "\t" + checkSum2);

		sb.append('\n');

		// Write it to file
		writeR(sb.toString());
	}

	/**
	 * Show in a way that R can read (logistic regression)
	 * @param i1
	 * @param i2
	 * @param codes
	 */
	void writeRResultsLd(int i1, int i2, double pvalue, int checkSum1, int checkSum2) {
		StringBuilder sb = new StringBuilder();

		// Add indexes (one-based)
		sb.append(i1 + 1);
		sb.append('\t');
		sb.append(i2 + 1);
		sb.append('\t');

		// Add variant data
		sb.append(entryId.get(i1).replace(' ', '\t'));
		sb.append('\t');
		sb.append(entryId.get(i2).replace(' ', '\t'));

		// Add p-values
		sb.append("\t" + pvalue);
		sb.append("\t1.0\t1.0\t1.0"); // For compatibility in the R program

		// Add checksums
		sb.append("\t" + checkSum1 + "\t" + checkSum2);

		sb.append('\n');

		// Write it to file
		writeR(sb.toString());
	}

	/**
	 * Show title in R file
	 */
	void writeRResultsTitle() {
		writeR("idx1\tidx2\tpos1\tgene1\tid1\tpos2\tgene2\tid2\tpAllelic\tpDominant\tpRecessive\tpCodominant\tchecksum1\tchecksum2\n");
	}
}
