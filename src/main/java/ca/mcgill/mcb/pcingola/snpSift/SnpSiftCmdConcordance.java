package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ca.mcgill.mcb.pcingola.collections.AutoHashMap;
import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.stats.CountByType;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Calculate genotyping concordance between two VCF files.
 *
 * Typical usage: Calculate concordance between sequencing experiment and genotypting experiment.
 *
 * @author pcingola
 */
public class SnpSiftCmdConcordance extends SnpSift {

	public static final String MISSING_GENOTYPE = "MISSING_GT";
	public static final String MISSING_ENTRY = "MISSING_ENTRY";
	public static final String SEP = "_";
	public static final String SEP_GT = "/";
	public static final int SHOW_EVERY = 10000;

	int countEntries;
	String vcfFileName1, vcfFileName2;
	String name1, name2;
	String chrPrev = "";
	String restrictSamplesFile;
	int[] idx2toidx1; // How sample 2 index maps to sample 1 index
	String[] sampleNameIdx2;
	CountByType errors = new CountByType();
	CountByType concordance = new CountByType();
	AutoHashMap<String, CountByType> concordanceBySample = new AutoHashMap<String, CountByType>(new CountByType());
	List<String> labels;
	FileIndexChrPos indexVcf;
	StringBuilder summary = new StringBuilder();
	HashSet<String> restrictSamples;
	protected VcfEntry latestVcfEntry = null;
	int latestVcfPos = 0;
	String latestVcfChr = "";
	boolean writeSummaryFile;
	boolean writeBySampleFile;
	boolean errorOnNonBiallelic;

	public SnpSiftCmdConcordance(String args[]) {
		super(args, "concordance");
	}

	/**
	 * Check that VCF entries match
	 * @return String indicating an error (empty string if OK)
	 */
	String check(VcfEntry ve1, VcfEntry ve2) {
		if (ve1 == null && ve2 == null) return "ERROR_BOTH_NULL";
		if (ve1 == null || ve2 == null) return ""; // OK, nothing to check

		//---
		// Sanity checks
		//---
		if (errorOnNonBiallelic) {
			if (ve1.getAlts().length > 1) {
				errors(ve1, ve2, "Multiple ALT in file '" + vcfFileName1 + "'");
				return "ERROR_NON_BIALLELIC_" + vcfFileName1 + ":" + ve1.getAlts();
			}

			if (ve2.getAlts().length > 1) {
				errors(ve1, ve2, "Multiple ALT in file '" + vcfFileName2 + "'S");
				return "ERROR_NON_BIALLELIC_" + vcfFileName2 + ":" + ve2.getAlts();
			}
		}

		// Only makes sense to check for bi-allelic ALTs
		if (!ve1.getAltsStr().equals(ve2.getAltsStr()) && ve1.getAlts().length == 1 && ve2.getAlts().length == 1) {
			errors(ve1, ve2, "ALT field does not match");
			return "ERROR_ALT_DOES_NOT_MATCH:" + ve1.getAltsStr() + "/" + ve2.getAltsStr();
		}

		if (!ve1.getRef().equals(ve2.getRef())) {
			errors(ve1, ve2, "REF fields does not match");
			return "ERROR_REF_DOES_NOT_MATCH:" + ve1.getRef() + "/" + ve2.getRef();
		}

		if (!ve1.getChromosomeName().equals(ve2.getChromosomeName())) {
			errors(ve1, ve2, "CHROMO field does not match");
			return "ERROR_CHROMO_DOES_NOT_MATCH";
		}

		if (ve1.getStart() != ve2.getStart()) {
			errors(ve1, ve2, "POS field does not match");
			return "ERROR_POS_DOES_NOT_MATCH";
		}

		return "";
	}

	/**
	 * Calculate concordance
	 */
	void concordance(VcfEntry ve1, VcfEntry ve2) {
		// Check that VCF entries match
		String err = check(ve1, ve2);

		if (debug) {
			String s1 = ve1 == null ? "null" : ve1.toStr();
			String s2 = ve2 == null ? "null" : ve2.toStr();
			Gpr.debug("Concordance: " + s1 + "\t" + s2 + "\tErr: " + err);
		}

		// Compare all genotypes from ve2 to the corresponding genotype in ve1
		CountByType count = new CountByType();
		int gtMax = idx2toidx1.length;
		for (int idx2 = 0; idx2 < gtMax; idx2++) {
			// Get sample index on vcf1
			int idx1 = idx2toidx1[idx2];

			// Does vcf1 also have this sample?
			if (idx1 >= 0) {
				CountByType countBySample = concordanceBySample.get(sampleNameIdx2[idx2]);
				String key = "";

				if (err.isEmpty()) {
					// OK, we can calculate concordance
					String gen1Str = genotypeKey(ve1, idx1, name1);
					String gen2Str = genotypeKey(ve2, idx2, name2);
					key = gen1Str + SEP_GT + gen2Str;
				} else {
					key = "ERROR";
				}
				if (debug) Gpr.debug("Sample " + sampleNameIdx2[idx2] + "\tkey:" + key);

				concordanceCount(key, count, countBySample);
			} else if (debug) Gpr.debug("Unmatched sample '" + sampleNameIdx2[idx2] + "' (number " + idx2 + ") in file " + name2);
		}

		// Show counts for this match
		System.out.print(showCounts(count, (ve1 != null ? ve1 : ve2), null, err));
	}

	/**
	 * Update counters
	 */
	void concordanceCount(String label, CountByType count, CountByType countBySample) {
		concordance.inc(label);
		count.inc(label);
		countBySample.inc(label);
	}

	/**
	 * Create a list of labels to show
	 */
	List<String> createLabels() {
		ArrayList<String> labels = new ArrayList<String>();

		for (int gtCode1 = -2; gtCode1 <= 2; gtCode1++)
			for (int gtCode2 = -2; gtCode2 <= 2; gtCode2++) {
				String label = genotypKey(gtCode1, name1) + SEP_GT + genotypKey(gtCode2, name2);
				labels.add(label);
			}

		// Last one is 'error'
		labels.add("ERROR");

		return labels;
	}

	/**
	 * Log and show errors
	 */
	void errors(VcfEntry ve1, VcfEntry ve2, String message) {
		errors.inc(message);
		if (verbose) System.err.println("ERROR: " + message + "\n\tVCF entry " + name1 + "\t" + ve1 + "\n\tVCF entry " + name2 + "\t" + ve2);
	}

	/**
	 * Find an entry in 'vcfFile' that matches chromosome and position from 'vcfEntry'
	 */
	VcfEntry find(VcfFileIterator vcfFile, VcfEntry vcfEntry) throws IOException {
		// Do we have to seek to chromosome position (in vcfFile)?
		String chr = vcfEntry.getChromosomeName();
		if (!chr.equals(chrPrev)) {
			if (!jumpToChromo(vcfFile, chr)) return null;
		}

		chrPrev = chr;

		//---
		// Compare 'latestVcfEntry'
		//---
		if (latestVcfEntry != null) {
			// Sanity check
			if (!latestVcfEntry.getChromosomeName().equals(chr)) {
				if (debug) Gpr.debug("Find: Different chromosomes :\t" + latestVcfEntry.toStr() + "\t" + vcfEntry.toStr());
				return null;
			}
			if (vcfEntry.getStart() < latestVcfEntry.getStart()) {
				if (debug) Gpr.debug("Find: Not there yet         :\t" + latestVcfEntry.toStr() + "\t" + vcfEntry.toStr());
				return null; // Not there yet
			}
			if (vcfEntry.getStart() == latestVcfEntry.getStart()) {
				if (debug) Gpr.debug("Find: Match!                :\t" + latestVcfEntry.toStr() + "\t" + vcfEntry.toStr());
				VcfEntry ve = latestVcfEntry;
				latestVcfEntry = null;
				return ve; // Match!
			}

			// Latest entry is not used, we should do the accounting before skipping to next entry
			concordance(latestVcfEntry, null);
		}

		//---
		// Read more entries from vcfFile
		//---
		for (VcfEntry ve : vcfFile) {
			countEntries++;

			// Sanity check: Is VCF sorted?
			if (latestVcfChr.equals(ve.getChromosomeName()) && latestVcfPos > ve.getStart()) fatalError("VCF file '" + vcfFileName2 + "' is not properly sorted. Position " + latestVcfChr + ":" + (latestVcfPos + 1) + " is after position " + latestVcfChr + ":" + (ve.getStart() + 1));

			latestVcfEntry = ve;
			latestVcfChr = ve.getChromosomeName();
			latestVcfPos = ve.getStart();

			// Does this entry match?
			if (!ve.getChromosomeName().equals(chr)) return null;
			if (vcfEntry.getStart() < latestVcfEntry.getStart()) return null; // Not there yet
			if (vcfEntry.getStart() == latestVcfEntry.getStart()) {
				latestVcfEntry = null;
				return ve; // Match!
			}

			concordance(latestVcfEntry, null);
		}

		if (debug) Gpr.debug("Find: No more entried in VCF_1    :\t\t" + vcfEntry.toStr());
		return null;
	}

	String genotypeKey(VcfEntry ve, int gtIndx, String name) {
		if (ve == null) return MISSING_ENTRY + SEP + name;

		VcfGenotype gt = ve.getVcfGenotype(gtIndx);
		if (gt.isMissing()) return MISSING_GENOTYPE + SEP + name;

		return genotypKey(gt.getGenotypeCode(), name);
	}

	String genotypKey(int gtCode, String name) {
		if (gtCode == -2) return MISSING_ENTRY + SEP + name;
		if (gtCode == -1) return MISSING_GENOTYPE + SEP + name;
		if (gtCode == 0) return "REF";
		return "ALT" + SEP + gtCode;
	}

	public CountByType getConcordance() {
		return concordance;
	}

	public AutoHashMap<String, CountByType> getConcordanceBySample() {
		return concordanceBySample;
	}

	public int getCountEntries() {
		return countEntries;
	}

	@Override
	public void init() {
		writeSummaryFile = writeBySampleFile = true;
		errorOnNonBiallelic = false;
	}

	/**
	 * Jump to next chromosome
	 */
	boolean jumpToChromo(VcfFileIterator vcfFile, String chr) throws IOException {
		if (debug) Gpr.debug("Find: Looking for chromosome '" + chr + "'");

		// Make sure we account for all 'missing' entries
		readUntilChromosomeEnd(vcfFile, chrPrev);

		// Get to the beginning of the new chromosome
		long start = indexVcf.getStart(chr);

		// No such chromosome?
		if (start < 0) {
			warn("Chromosome '" + chr + "' not found in database.");
			return false;
		}

		// Seek
		vcfFile.seek(start);
		latestVcfEntry = null;
		if (verbose) Timer.showStdErr("Chromosome: '" + chr + "'");

		return true;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (args[i].equals("-s")) restrictSamplesFile = args[++i];
			else if (vcfFileName1 == null) vcfFileName1 = arg;
			else if (vcfFileName2 == null) vcfFileName2 = arg;
		}

		// Sanity check
		if (vcfFileName2 == null) usage("Missing vcf file");
	}

	/**
	 * Read headers and parse sample names. Create map from
	 * sample index in vcf2 to sample index in vcf1
	 */
	void parseVcfSampleNames(VcfFileIterator vcf1, VcfFileIterator vcf2) {
		// Read both headers
		vcf1.readHeader();
		vcf2.readHeader();

		// Sanity check
		if (vcf1.getSampleNames() == null) fatalError("Unable to parse sample names from file '" + vcfFileName1 + "'. Missing header line?");
		if (vcf2.getSampleNames() == null) fatalError("Unable to parse sample names from file '" + vcfFileName2 + "'. Missing header line?");

		// Map sample names to sample number
		HashMap<String, Integer> vcf1Name2Idx = new HashMap<String, Integer>();
		int idx = 0;
		for (String sampleName : vcf1.getSampleNames())
			vcf1Name2Idx.put(sampleName, idx++);

		// Map sample names to sample number
		HashMap<String, Integer> vcf2Name2Idx = new HashMap<String, Integer>();
		idx2toidx1 = new int[vcf2.getSampleNames().size()];
		sampleNameIdx2 = new String[vcf2.getSampleNames().size()];
		idx = 0;
		int shared = 0;
		for (String sampleName : vcf2.getSampleNames()) {
			vcf2Name2Idx.put(sampleName, idx);

			if ((restrictSamples == null) || restrictSamples.contains(sampleName)) {
				if (vcf1Name2Idx.containsKey(sampleName)) {
					shared++;
					idx2toidx1[idx] = vcf1Name2Idx.get(sampleName); // Assign to index mapping array
					sampleNameIdx2[idx] = sampleName;
					concordanceBySample.getOrCreate(sampleName); // Initialize autoHash
					if (debug) System.err.println("\tMap\tSample " + sampleName + "\t" + name2 + "[" + idx + "]\t->\t" + name1 + "[" + idx2toidx1[idx] + "]");
				} else idx2toidx1[idx] = -1;
			} else idx2toidx1[idx] = -1;

			idx++;
		}

		// Show basic stats
		summary("Number of samples:");
		summary("\t" + vcf1.getSampleNames().size() + "\tFile " + vcfFileName1);
		summary("\t" + vcf2.getSampleNames().size() + "\tFile " + vcfFileName2);
		if (restrictSamples != null) summary("\t" + restrictSamples.size() + "\tFile " + restrictSamplesFile);
		summary("\t" + shared + "\tBoth files");
	}

	/**
	 * Iterate on a VCF until next we finish reading entries corresponding to chromosome 'chr'
	 * This is done to complete the 'missing' counts
	 */
	void readUntilChromosomeEnd(VcfFileIterator vcf, String chr) {

		// Don't forget to count this 'latestVcfEntry'
		if (latestVcfEntry != null) {
			if (latestVcfEntry.getChromosomeName().equals(chr)) concordance(latestVcfEntry, null);
			else return; // We are already on a different chromosome
		}

		// Finish iterating on VCF1, just to complete the 'missing' counts
		for (VcfEntry ve : vcf) {
			if (!ve.getChromosomeName().equals(chr)) break; // Jumped to another chromo? Then we are done
			concordance(ve, null);

			// Show progress
			if (verbose && (countEntries >= SHOW_EVERY)) {
				countEntries = 0;
				Timer.showStdErr("\t" + (latestVcfEntry != null ? latestVcfEntry.getChromosomeName() + ":" + (latestVcfEntry.getStart() + 1) : "") + "\t" + ve.getChromosomeName() + ":" + (ve.getStart() + 1));
			}
			countEntries++;
		}
	}

	@Override
	public void run() {
		// Read samples file
		if (restrictSamplesFile != null) {
			restrictSamples = new HashSet<String>();
			for (String s : Gpr.readFile(restrictSamplesFile).split("\n"))
				restrictSamples.add(s.trim());
		}

		// Assign labels based on file names
		name1 = vcfFile2name(vcfFileName1);
		name2 = vcfFile2name(vcfFileName2);

		//---
		// Index vcf1:  it is assumed to be the smaller of the two
		//---
		if (verbose) Timer.showStdErr("Indexing file '" + vcfFileName1 + "'");
		indexVcf = new FileIndexChrPos(vcfFileName1);
		indexVcf.setVerbose(verbose);
		indexVcf.setDebug(debug);
		indexVcf.open();
		indexVcf.index();
		indexVcf.close();

		//---
		// Open files
		//---
		VcfFileIterator vcf1, vcf2;
		try {
			if (verbose) Timer.showStdErr("Open VCF file '" + vcfFileName1 + "'");
			vcf1 = new VcfFileIterator(new SeekableBufferedReader(vcfFileName1));
			vcf1.setDebug(debug);

			if (verbose) Timer.showStdErr("Open VCF file '" + vcfFileName2 + "'");
			vcf2 = new VcfFileIterator(vcfFileName2);
			vcf2.setDebug(debug);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//---
		// Get sample names and mapping
		//---
		parseVcfSampleNames(vcf1, vcf2);

		//---
		// Create labels and show title
		//---
		StringBuilder title = new StringBuilder();
		StringBuilder titleBySample = new StringBuilder();
		title.append("chr\tpos\tref\talt");
		titleBySample.append("sample\t\t\t");
		labels = createLabels();
		for (String label : labels) {
			title.append("\t" + label);
			titleBySample.append("\t" + label);
		}
		System.out.println(title);

		//---
		// Iterate on larger file
		//---
		try {
			// Iterate over all entries on VCF2
			int latestPos = 0;
			String latestChr = "";
			for (VcfEntry ve2 : vcf2) {
				// Sanity check: Is VCF sorted?
				if (latestChr.equals(ve2.getChromosomeName()) && latestPos > ve2.getStart()) fatalError("VCF file '" + vcfFileName2 + "' is not properly sorted. Position " + latestChr + ":" + (latestPos + 1) + " is after position " + latestChr + ":" + (ve2.getStart() + 1));

				VcfEntry ve1 = find(vcf1, ve2);
				concordance(ve1, ve2);

				// Show progress
				if (verbose && (countEntries >= SHOW_EVERY)) {
					countEntries = 0;
					Timer.showStdErr("\t" + (latestVcfEntry != null ? latestVcfEntry.getChromosomeName() + ":" + (latestVcfEntry.getStart() + 1) : "") + "\t" + ve2.getChromosomeName() + ":" + (ve2.getStart() + 1));
				}

				countEntries++;
				latestChr = ve2.getChromosomeName();
				latestPos = ve2.getStart();
			}
			readUntilChromosomeEnd(vcf1, chrPrev);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Show results
		showResults(titleBySample.toString());
	}

	public void setWriteBySampleFile(boolean writeBySampleFile) {
		this.writeBySampleFile = writeBySampleFile;
	}

	public void setWriteSummaryFile(boolean writeSummaryFile) {
		this.writeSummaryFile = writeSummaryFile;
	}

	/**
	 * Show a counter
	 */
	String showCounts(CountByType count, VcfEntry ve, String rowTitle, String error) {
		StringBuilder sb = new StringBuilder();

		if (ve != null) sb.append(ve.getChromosomeName() + "\t" + (ve.getStart() + 1) + "\t" + ve.getRef() + "\t" + ve.getAltsStr());
		else if (rowTitle != null) sb.append(rowTitle + "\t\t\t");
		else sb.append("#Total\t.\t.\t.");

		for (String label : labels)
			sb.append("\t" + count.get(label));

		// Show error
		sb.append("\t" + error);

		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Show results
	 */
	void showResults(String titleBySample) {
		// Show totals
		System.out.print(showCounts(concordance, null, null, ""));

		// Write 'by sample' file
		if (writeBySampleFile) {
			String bySampleFile = "concordance_" + name1 + "_" + name2 + ".by_sample.txt"; // Write to file
			Timer.showStdErr("Writing concordance by sample to file '" + bySampleFile + "'");

			StringBuilder bySample = new StringBuilder();
			bySample.append(titleBySample + "\n"); // Add title
			ArrayList<String> sampleNames = new ArrayList<String>(); // Sort samples by name
			sampleNames.addAll(concordanceBySample.keySet());
			Collections.sort(sampleNames);
			for (String sample : sampleNames)
				bySample.append(showCounts(concordanceBySample.get(sample), null, sample, "")); // Add all samples

			Gpr.toFile(bySampleFile, bySample); // Write file
		}

		// Write summary file
		if (writeSummaryFile) {
			String summaryFile = "concordance_" + name1 + "_" + name2 + ".summary.txt"; // Write to file
			Timer.showStdErr("Writing summary file '" + summaryFile + "'");

			if (!errors.isEmpty()) { // Add errors (if any)
				summary("\n# Errors:");
				for (String l : errors.keySet())
					summary("\t" + l + "\t" + errors.get(l));
			}

			Gpr.toFile(summaryFile, summary);
		}
	}

	/**
	 * Add to summary (and show in verbose mode)
	 */
	void summary(String message) {
		summary.append(message + "\n");
		if (verbose) System.err.println(message);
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [options] reference.vcf sequencing.vcf\n");
		System.err.println("Options:\n");
		System.err.println("\t -s <file>  : Only use sample IDs in file (format: one sample ID per line).");
		System.exit(1);
	}

	String vcfFile2name(String vcf) {
		if (vcf.isEmpty() || vcf.equals("-")) return "stdin";
		return Gpr.removeExt(Gpr.baseName(vcf));
	}

}
