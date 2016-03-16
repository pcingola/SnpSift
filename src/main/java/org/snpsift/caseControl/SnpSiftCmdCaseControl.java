package org.snpsift.caseControl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.ped.PedPedigree;
import org.snpeff.ped.TfamEntry;
import org.snpeff.probablility.CochranArmitageTest;
import org.snpeff.probablility.FisherExactTest;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpsift.SnpSift;

/**
 * Count number of cases and controls
 *
 * @author pablocingolani
 */
public class SnpSiftCmdCaseControl extends SnpSift {

	public static final int SHOW_EVERY = 100;

	public static final String VCF_INFO_CASE = "Cases";
	public static final String VCF_INFO_CONTROL = "Controls";
	public static final String VCF_INFO_CC_GENO = "CC_GENO";
	public static final String VCF_INFO_CC_ALL = "CC_ALL";
	public static final String VCF_INFO_CC_DOM = "CC_DOM";
	public static final String VCF_INFO_CC_REC = "CC_REC";
	public static final String VCF_INFO_CC_TREND = "CC_TREND";

	protected Boolean caseControl[];
	protected String tfamFile;
	protected String groups;
	protected PedPedigree pedigree;
	protected double pvalueThreshold;
	protected boolean useChiSquare;
	String name;
	String posMin = "";
	double pValueMin = 1.0;

	public SnpSiftCmdCaseControl(String args[]) {
		super(args, "casecontrol");
	}

	/**
	 * Annotate VCF entry
	 */
	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		int casesHom = 0, casesHet = 0, cases = 0;
		int ctrlHom = 0, ctrlHet = 0, ctrl = 0;
		int nCase[] = new int[3];
		int nControl[] = new int[3];

		// Count genotypes
		int idx = 0;
		if (debug) Gpr.debug(vcfEntry.toStringNoGt());
		for (VcfGenotype gt : vcfEntry) {

			int code = gt.getGenotypeCode();
			if ((caseControl[idx] != null)) {

				int codeMissing = gt.getGenotypeCodeIgnoreMissing();

				if (code >= 0) {
					// Count a/a, a/A and A/A
					if (caseControl[idx]) nCase[code]++;
					else nControl[code]++;
				}

				if (gt.isVariant()) {
					if (caseControl[idx]) {
						// Case sample
						if (gt.isMissing()) ; // Missing? => Do not count
						else if (gt.isHomozygous()) casesHom++;
						else casesHet++;

						cases += codeMissing;
					} else {
						//Control sample
						if (gt.isMissing()) ; // Missing? => Do not count
						else if (gt.isHomozygous()) ctrlHom++;
						else ctrlHet++;

						ctrl += codeMissing;
					}
				}
			}

			// Show detailed information
			if (debug) {
				String cc = "Ignore";
				if (caseControl[idx] != null) cc = caseControl[idx] ? "Case" : "Control";
				System.err.printf("\tSample: %3d\tType: %-8s\tGT code: %+1d\tnCases: [%3d, %3d, %3d]\tnControls: [%3d, %3d, %3d]\tGT: %s\n", idx, cc, code, nCase[0], nCase[1], nCase[2], nControl[0], nControl[1], nControl[2], gt);
			}

			idx++;
		}

		// Add info fields
		vcfEntry.addInfo(VCF_INFO_CASE + name, casesHom + "," + casesHet + "," + cases);
		vcfEntry.addInfo(VCF_INFO_CONTROL + name, ctrlHom + "," + ctrlHet + "," + ctrl);

		// Annotate pValues
		vcfEntry.addInfo(VCF_INFO_CC_TREND + name, pValueStr(vcfEntry, pTrend(nControl, nCase)));
		vcfEntry.addInfo(VCF_INFO_CC_GENO + name, pValueStr(vcfEntry, pGenotypic(nControl, nCase)));
		swapMinorAllele(nControl, nCase); // Swap if minor allele is reference
		vcfEntry.addInfo(VCF_INFO_CC_ALL + name, "" + pValueStr(vcfEntry, pAllelic(nControl, nCase, pvalueThreshold)));
		vcfEntry.addInfo(VCF_INFO_CC_DOM + name, "" + pValueStr(vcfEntry, pDominant(nControl, nCase, pvalueThreshold)));
		vcfEntry.addInfo(VCF_INFO_CC_REC + name, "" + pValueStr(vcfEntry, pRecessive(nControl, nCase, pvalueThreshold)));

		return true;
	}

	/**
	 * Lines to be added to VCF header
	 */
	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> addh = super.headers();
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CASE + name + ",Number=3,Type=Integer,Description=\"Number of variants in cases: Hom, Het, Count\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CONTROL + name + ",Number=3,Type=Integer,Description=\"Number of variants in controls: Hom, Het, Count\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CC_DOM + name + ",Number=1,Type=Float,Description=\"p-value using dominant model (Fisher exact test)\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CC_REC + name + ",Number=1,Type=Float,Description=\"p-value using recessive model (Fisher exact test)\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CC_ALL + name + ",Number=1,Type=Float,Description=\"p-value using allele count model (Fisher exact test)\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CC_GENO + name + ",Number=1,Type=Float,Description=\"p-value using genotypic model (ChiSquare)\">"));
		addh.add(new VcfHeaderEntry("##INFO=<ID=" + VCF_INFO_CC_TREND + name + ",Number=1,Type=Float,Description=\"p-value using trend model (CochranArmitage)\">"));
		return addh;
	}

	@Override
	public void init() {
		pvalueThreshold = 1.0;
		useChiSquare = false;
	}

	/**
	 * Is this a valid 'groups' string?
	 */
	boolean isGroupString(String groupsStr) {
		return groupsStr.replace('+', ' ').replace('-', ' ').replace('0', ' ').trim().isEmpty();
	}

	/**
	 * Allelic model: Count number of SNPs
	 */
	protected double pAllelic(int nControl[], int nCase[], double pvalueTh) {
		int k = 2 * nCase[2] + nCase[1];
		int N = 2 * (nControl[0] + nControl[1] + nControl[2] + nCase[0] + nCase[1] + nCase[2]);
		int D = 2 * (nCase[0] + nCase[1] + nCase[2]); // All cases
		int n = 2 * nControl[2] + nControl[1] + 2 * nCase[2] + nCase[1]; // A/A + A/a

		// Use ChiSquare approximation?
		if (useChiSquare) return FisherExactTest.get().chiSquareApproximation(k, N, D, n);

		// Use Fisher exact test
		double pdown = FisherExactTest.get().pValueDown(k, N, D, n, pvalueTh);
		double pup = FisherExactTest.get().pValueUp(k, N, D, n, pvalueTh);

		return Math.min(pup, pdown);
	}

	@Override
	public void parseArgs(String[] args) {
		if (args.length <= 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (isOpt(arg)) {
				if (arg.equals("-tfam")) tfamFile = args[++i];
				else if (arg.equals("-name")) name = args[++i];
				else if (arg.equals("-chi2")) useChiSquare = true;
				else if ((groups == null) && (tfamFile == null) && isGroupString(arg)) groups = arg; // Sometimes this starts with a '-' and is confused with a command line option
			} else if ((groups == null) && (tfamFile == null) && isGroupString(arg)) groups = arg;
			else if (vcfInputFile == null) vcfInputFile = arg;
			else usage("Unkown parameter '" + arg + "'");
		}

		// Sanity check
		if ((groups == null) && (tfamFile == null)) usage("You must provide either a 'group' string or a TFAM file");
		if (name == null) name = "";
	}

	/**
	 * Parse group string
	 */
	void parseCaseControlString() {
		char chars[] = groups.toCharArray();
		caseControl = new Boolean[chars.length];
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '+') caseControl[i] = true;
			else if (chars[i] == '-') caseControl[i] = false;
			else if (chars[i] == '0') caseControl[i] = null;
			else usage("Unknown character '" + chars[i] + "' (sample " + (i + 1) + ") in groups string");
		}
	}

	/**
	 * Parse from TFAM file
	 */
	void parseCaseControlTfam() {
		pedigree = new PedPedigree(tfamFile); // Here we just load the file
	}

	/**
	 * Dominant model: Either a/A or A/A causes the disease
	 */
	protected double pDominant(int nControl[], int nCase[], double pvalueTh) {
		int k = nCase[2] + nCase[1]; // Cases a/a + A/a
		int N = nControl[0] + nControl[1] + nControl[2] + nCase[0] + nCase[1] + nCase[2];
		int D = nCase[0] + nCase[1] + nCase[2]; // All cases
		int n = nControl[2] + nControl[1] + nCase[2] + nCase[1]; // a/a + A/a

		// Use ChiSquare approximation?
		if (useChiSquare) return FisherExactTest.get().chiSquareApproximation(k, N, D, n);

		// Use Fisher exact test
		double pdown = FisherExactTest.get().pValueDown(k, N, D, n, pvalueTh);
		double pup = FisherExactTest.get().pValueUp(k, N, D, n, pvalueTh);

		return Math.min(pup, pdown);
	}

	/**
	 * Genotypic model (Chi Square)
	 */
	protected double pGenotypic(int nControl[], int nCase[]) {
		int rows = 2;
		int cols = 3;

		int n[][] = new int[rows][cols];
		for (int j = 0; j < cols; j++) {
			n[0][j] = nCase[j];
			n[1][j] = nControl[j];
		}

		// Totals by row & column
		int total = 0;
		int totalRow[] = new int[2];
		int totalCol[] = new int[3];

		for (int i = 0; i < rows; i++)
			totalRow[i] = 0;

		for (int j = 0; j < cols; j++)
			totalCol[j] = 0;

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				totalRow[i] += n[i][j];
				totalCol[j] += n[i][j];
				total += n[i][j];
			}

		// Calculate ChiSquare
		double chi2 = 0.0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				double eij = (totalCol[j] * totalRow[i]) / ((double) total); // Expected value for this row & column
				double diff = (n[i][j] - eij);
				chi2 += diff * diff / eij;
			}

		// Null hypothesis of no association
		// Degrees of freedom: 2
		double pvalue = 1 - new ChiSquaredDistribution(2).cumulativeProbability(chi2);
		return pvalue;
	}

	/**
	 * Recessive model: Only A/A causes the disease
	 */
	protected double pRecessive(int nControl[], int nCase[], double pvalueTh) {
		int k = nCase[2]; // Cases a/a
		int N = nControl[0] + nControl[1] + nControl[2] + nCase[0] + nCase[1] + nCase[2];
		int D = nCase[0] + nCase[1] + nCase[2]; // All cases
		int n = nControl[2] + nCase[2]; // a/a

		// Use ChiSquare approximation?
		if (useChiSquare) return FisherExactTest.get().chiSquareApproximation(k, N, D, n);

		// Use Fisher exact test
		double pdown = FisherExactTest.get().pValueDown(k, N, D, n, pvalueTh);
		double pup = FisherExactTest.get().pValueUp(k, N, D, n, pvalueTh);

		return Math.min(pup, pdown);
	}

	@Override
	protected String processVcfHeader(VcfFileIterator vcf) {
		if (!vcf.isHeadeSection()) return "";

		String header = super.processVcfHeader(vcf); // Add lines and print header

		// Parse pedigree from TFAM?
		if (pedigree != null) {
			List<String> sampleIds = vcf.getVcfHeader().getSampleNames();
			caseControl = new Boolean[sampleIds.size()];

			int idx = 0, errors = 0;
			for (String sid : sampleIds) {
				TfamEntry tfam = pedigree.get(sid); // Find TFAM entry for this sample
				if (tfam == null) {
					System.err.println("WARNING: Sample ID '" + sid + "' has no entry in pedigree form TFAM file '" + tfamFile + "'");
					errors++;
					caseControl[idx] = null;
				} else {
					// Assign case, control or missing
					if (tfam.isMissing()) caseControl[idx] = null;
					else caseControl[idx] = tfam.isCase();
				}
				idx++;
			}

			// Abort?
			if (errors > (sampleIds.size() / 2)) throw new RuntimeException("VCF samples are missing in TFAM file. Too many errors, aboting!");
		}

		// Sanity check
		if (caseControl.length != vcf.getVcfHeader().getSampleNames().size()) throw new RuntimeException("Number of case control entries specified does not match number of samples in VCF file");

		// Show details
		if (debug) {
			System.err.println("\tSample\tCase");
			int idx = 0;
			for (String sid : vcf.getVcfHeader().getSampleNames())
				System.err.println("\t" + sid + "\t" + caseControl[idx++]);
		}

		// Show overview
		if (verbose) {
			int countCase = 0, countCtrl = 0, countIgnored = 0;
			for (Boolean cc : caseControl) {
				if (cc == null) countIgnored++;
				else if (cc) countCase++;
				else countCtrl++;
			}
			Timer.showStdErr("Total : " + caseControl.length + " entries. Cases: " + countCase + ", controls: " + countCtrl + ", ignored: " + countIgnored);
		}

		return header;
	}

	/**
	 * Trend model
	 */
	protected double pTrend(int nControl[], int nCase[]) {
		// Null hypothesis of no association
		double pvalue = CochranArmitageTest.get().p(nControl, nCase, CochranArmitageTest.WEIGHT_TREND);
		// We use a two tail test, so we multiple by 2
		return Math.min(2.0 * pvalue, 1.0);
	}

	/**
	 * Show p-value as a string and record minimum p-value
	 */
	String pValueStr(VcfEntry vcfEntry, double p) {
		if (verbose && (p > 0) && (p < 1.0) && (p <= pValueMin)) //
			Timer.showStdErr("Minimum p-value so far: " //
					+ pValueMin //
					+ "\tchr: " + vcfEntry.getChromosomeName() //
					+ "\tpos: " + (vcfEntry.getStart() + 1) //
					+ (!vcfEntry.getId().isEmpty() ? "\tid: " + vcfEntry.getId() : "") //
		);

		if ((p > 0) && (p < pValueMin)) {
			pValueMin = p;
			posMin = vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1);
		}
		return String.format("%.3e", p);
	}

	/**
	 * Load a file compare calls
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run
	 * @param createList : Is true , create a list of VcfEntries (used in test cases)
	 * @return A list of VcfEntry is createList is true
	 */
	public List<VcfEntry> run(boolean createList) {
		showHeader = !createList;
		ArrayList<VcfEntry> list = new ArrayList<VcfEntry>();

		if (tfamFile != null) parseCaseControlTfam();
		else parseCaseControlString();

		// Read all vcfEntries
		VcfFileIterator vcf = openVcfInputFile();
		vcf.setDebug(debug);

		int i = 1;
		for (VcfEntry vcfEntry : vcf) {
			processVcfHeader(vcf); // Handle header stuff
			annotate(vcfEntry); // Annotate

			// Show
			if (createList) list.add(vcfEntry);
			else System.out.println(vcfEntry);

			if (verbose) Gpr.showMark(i++, SHOW_EVERY);
		}

		if (verbose) {
			Timer.showStdErr("Done.\n\tMinimum pValue: " + pValueMin + "\tVcf entry: " + posMin);
		}
		return list;
	}

	/**
	 * Swap counts if REF is minor allele (instead of ALT)
	 */
	protected void swapMinorAllele(int nControl[], int nCase[]) {
		int refCount = 2 * nControl[0] + nControl[1] + 2 * nCase[0] + nCase[1];
		int altCount = 2 * nControl[2] + nControl[1] + 2 * nCase[2] + nCase[1];

		if (refCount < altCount) {
			if (debug) Gpr.debug("Swapping genotype counts");
			int tmp = nControl[0];
			nControl[0] = nControl[2];
			nControl[2] = tmp;

			tmp = nCase[0];
			nCase[0] = nCase[2];
			nCase[2] = tmp;
		}
	}

	/**
	 * Show usage message
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar caseControl [-v] [-name nameString] { -tfam file.tfam | <CaseControlString> } file.vcf");
		System.err.println("Where:");
		System.err.println("\t<CaseControlString> : A string of {'+', '-', '0'}, one per sample, to identify two groups (case='+', control='-', neutral='0')");
		System.err.println("\t -chi2              : Use ChiSquare approximarion instead of Fisher exact test.");
		System.err.println("\t -name nameStr      : A name to be added after to 'Cases' or 'Controls' tags");
		System.err.println("\t -tfam file.tfam    : A TFAM file having case/control informations (phenotype colmun)");
		System.err.println("\tfile.vcf            : A VCF file (variants and genotype data)");
		System.exit(1);
	}
}
