package org.snpsift;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.snpeff.fileIterator.BedFileIterator;
import org.snpeff.fileIterator.LineFileIterator;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Marker;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;

/**
 * Annotate using PhastCons score files
 *
 * @author pcingola
 */
public class SnpSiftCmdPhastCons extends SnpSift {

	// Wig fields
	public static final String START_FIELD = "start=";
	public static final String FIXED_STEP_FIELD = "fixedStep";

	// VCF INFO FILED
	public static final String VCF_INFO_PHASTCONS_FIELD = "PhastCons";

	public static final int SHOW_EVERY = 1000;

	boolean bed;
	boolean extract;
	int minBases;
	double minScore;
	String phastConsDir;
	String vcfFile;
	HashMap<String, Integer> chromoSize;
	short score[];

	public SnpSiftCmdPhastCons() {
		super();
	}

	public SnpSiftCmdPhastCons(String[] args) {
		super(args);
	}

	/**
	 * Annotate and show in BED format
	 * @param seqChange
	 */
	void annotateBed(Variant seqChange) {
		float score = score(seqChange);
		printBed(seqChange, score);
	}

	/**
	 * Annotate VcfEntry
	 * @param ve
	 */
	void annotateVcf(VcfEntry ve) {
		float score = score(ve);
		if (score > minScore) ve.addInfo(VCF_INFO_PHASTCONS_FIELD, String.format("%.3f", score));
	}

	/**
	 * Get chromosome size
	 * @param chromo
	 * @return
	 */
	int chromoSize(String chromo) {
		Integer len = chromoSize.get(Chromosome.simpleName(chromo));
		return len == null ? 0 : len;
	}

	/**
	 * Extract sub-intervals having at least 'minBases' and 'minScore' average conservation
	 * @param seqChange
	 */
	void extractBed(Variant seqChange) {
		for (int start = seqChange.getStart(); start <= (seqChange.getEnd() - minBases); start++) {
			// Find best interval starting at 'start'
			Variant sc = extractBed(seqChange, start);
			if (sc == null) continue; // Nothing found

			// Show interval
			printBed(sc, score(sc));

			start = sc.getEnd() + 1; // Move after interval's end
		}
	}

	/**
	 * Get the longest interval starting at 'start' that has at least 'minBases' and 'minScore'
	 *
	 * @param seqChange
	 * @param start
	 * @return
	 */
	Variant extractBed(Variant seqChange, int start) {
		int prevEnd = -1;
		int end = start + minBases - 1;

		Variant scPrev = new Variant(seqChange.getParent(), start, end, seqChange.getId());
		Variant sc = scPrev;
		float score = score(sc);
		if (score < minScore) return null;

		// Try to get a larger interval
		while (score >= minScore) {
			scPrev = sc;
			prevEnd = end;

			// Prepare for next iteration
			end++;
			sc = new Variant(seqChange.getParent(), start, end, seqChange.getId());
			score = score(sc);
		}

		if (prevEnd > 0) return scPrev;
		return null;
	}

	/**
	 * Find any file matching a regular expression
	 * @param regex
	 * @return
	 */
	String findPhastConsFile(String dirName, String regex) {
		try {
			File dir = new File(dirName);
			for (File f : dir.listFiles()) {
				String fname = f.getCanonicalPath();
				if (fname.matches(regex)) return fname;
			}
		} catch (IOException e) {
			// Do nothing
		}

		Timer.showStdErr("Cannot find any file in directory '" + dirName + "' matching regular expression '" + regex + "'");
		return null;
	}

	@Override
	public void init() {
		super.init();
		minScore = 0.0;
		bed = false;
	}

	/**
	 * Load a phastcons file for this chromosome
	 * @param chromo
	 * @return
	 */
	boolean loadChromo(String chromo, Marker marker) {
		chromo = Chromosome.simpleName(chromo);
		score = null;

		// Find a file that matches a phastCons name
		String wigFile = findPhastConsFile(phastConsDir, ".*/chr" + chromo + "\\..*wigFix.*");
		if ((wigFile == null) || !Gpr.exists(wigFile)) {
			if (wigFile != null) Timer.showStdErr("Cannot open PhastCons file '" + wigFile + "' for chromosome '" + chromo + "'\n\tEntry:\t" + marker);
			return false;
		}

		if (verbose) Timer.showStdErr("Loading phastCons data for chromosome '" + chromo + "', file '" + wigFile + "'");

		// Initialize
		int chrSize = chromoSize(chromo) + 1;
		score = new short[chrSize];
		for (int i = 0; i < score.length; i++)
			score[i] = 0;

		//---
		// Read file
		//---
		LineFileIterator lfi = new LineFileIterator(wigFile);
		int index = 0, countHeaders = 1;
		for (String line : lfi) {
			if (line.startsWith(FIXED_STEP_FIELD)) {
				String fields[] = line.split("\\s+");
				for (String f : fields) {
					if (f.startsWith(START_FIELD)) {
						String value = f.substring(START_FIELD.length());
						index = Gpr.parseIntSafe(value) - 1; // Wig files coordinates are 1-based. Reference http://genome.ucsc.edu/goldenPath/help/wiggle.html
						if (verbose) Gpr.showMark(countHeaders++, SHOW_EVERY);
					}
				}
			} else if (index >= score.length) {
				// Out of chromosome?
				Timer.showStdErr("PhastCons index out of chromosome boundaries." //
						+ "\n\tIndex             : " + index //
						+ "\n\tChromosome length : " + score.length //
				);
				break;
			} else {
				score[index] = (short) (Gpr.parseFloatSafe(line) * 1000);
				index++;
			}
		}

		// Show message
		if (verbose) {
			int countNonZero = 0;
			for (int i = 0; i < score.length; i++)
				if (score[i] != 0) countNonZero++;

			double perc = (100.0 * countNonZero) / score.length;
			System.err.println("");
			Timer.showStdErr(String.format("Total non-zero scores: %d / %d [%.2f%%]", countNonZero, score.length, perc));
		}

		return index > 0;
	}

	/**
	 * Load chromosome length file.
	 * This is a fasta index file created by "samtools faidx" command.
	 *
	 * @return
	 */
	void loadFaidx() {
		String file = phastConsDir + "/genome.fai";
		if (!Gpr.exists(file)) { throw new RuntimeException("Cannot find fasta index file '" + file + "'\n\tYou can create one by running 'samtools faidx' command and copying the resulting index file to " + file + "\n\n"); }

		// Read and parse file
		chromoSize = new HashMap<>();
		String txt = Gpr.readFile(file);
		for (String line : txt.split("\n")) {
			String fields[] = line.split("\\s+");
			String chrName = Chromosome.simpleName(fields[0]);
			int len = Gpr.parseIntSafe(fields[1]);

			chromoSize.put(chrName, len);
		}
	}

	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		for (int argNum = 0; argNum < args.length; argNum++) {
			String arg = args[argNum];

			if (isOpt(arg)) {
				// Command line options
				if (arg.equals("-bed")) bed = true;
				else if (arg.equalsIgnoreCase("-minScore")) {
					if (argNum >= args.length) usage("Missing 'minScore' number");
					minScore = Gpr.parseDoubleSafe(args[++argNum]);
				} else if (arg.equalsIgnoreCase("-extract")) {
					if (argNum >= args.length) usage("Missing 'extract' number");
					extract = true;
					minBases = Gpr.parseIntSafe(args[++argNum]);
				} else usage("Unknown command line option '" + arg + "'");
			} else {
				if (phastConsDir == null) phastConsDir = args[argNum];
				else if (vcfFile == null) vcfFile = args[argNum];
			}
		}

		// Sanity check
		if (phastConsDir == null) usage("Missing 'phastConsDir' parameter.");
		if (vcfFile == null) usage("Missing 'inputFile' parameter.");
		if (extract && (minBases <= 0)) usage("Number of bases to extract should be greater than zero.");
	}

	/**
	 * Print an interval in BED format (and a score)
	 * @param seqChange
	 * @param score
	 */
	void printBed(Variant seqChange, float score) {
		System.out.print(seqChange.getChromosomeName() //
				+ "\t" + (seqChange.getStart()) //
				+ "\t" + (seqChange.getEnd() + 1) // End base is not included in BED format
				+ "\t" + seqChange.getId() //
		);

		if (score > minScore) System.out.print(String.format("\t%.3f", score));
		System.out.println("");
	}

	/**
	 * Run
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run annotations
	 *
	 * @param createList : If true, create a list of VcfEntries (used for test cases)
	 *
	 * @return
	 */
	public List<VcfEntry> run(boolean createList) {
		// Load chromosome lengths
		loadFaidx();

		// Run on BED file?
		if (bed) {
			runBed();
			return new ArrayList<>();
		}

		// Run VCF
		return runVcf(createList);
	}

	/**
	 * Run on intervals (BED file)
	 */
	void runBed() {
		BedFileIterator bedFile = new BedFileIterator(vcfFile);
		String chrPrev = "";
		for (Variant sc : bedFile) {
			// Do we need to load a database?
			if (!chrPrev.equals(sc.getChromosomeName())) {
				chrPrev = sc.getChromosomeName();
				loadChromo(chrPrev, sc);
			}

			// Annotate entry
			if (extract) extractBed(sc);
			else annotateBed(sc);
		}
	}

	/**
	 * Run on VCF file
	 * @param createList
	 * @return
	 */
	List<VcfEntry> runVcf(boolean createList) {
		// Iterate over file
		ArrayList<VcfEntry> list = new ArrayList<>();
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		vcf.setDebug(debug);
		String chrPrev = "";
		for (VcfEntry ve : vcf) {
			if (vcf.isHeadeSection()) {
				// Add header line
				vcf.getVcfHeader().addLine("##INFO=<ID=" + VCF_INFO_PHASTCONS_FIELD + ",Number=1,Type=Float,Description=\"PhastCons conservation score\">");

				// Show header
				if (!createList) System.out.println(vcf.getVcfHeader());
			}

			// Do we need to load a database?
			if (!chrPrev.equals(ve.getChromosomeName())) {
				chrPrev = ve.getChromosomeName();
				loadChromo(chrPrev, ve);
			}

			// Annotate entry
			annotateVcf(ve);

			// Show or add to list
			if (createList) list.add(ve);
			else System.out.println(ve);
		}

		return list;
	}

	/**
	 * Score for this entry
	 * @param marker
	 * @return
	 */
	float score(Marker marker) {
		int pos = marker.getEnd();
		if ((score == null) || (pos >= score.length)) return Float.MIN_VALUE;

		// Is this a SNP? i.e. only one base
		if (marker.size() == 1) return score[marker.getStart()] / 1000.0f;

		// More then one base length?
		// Return the average score of all those bases
		int sum = 0;
		for (int p = marker.getStart(); p <= marker.getEnd(); p++)
			sum += score[p];

		return sum / (1000.0f * marker.size());
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar [options] path/to/phastCons/dir inputFile");
		System.err.println("Arguments:");
		System.err.println("\tinputFile       : VCF or BED file.");
		System.err.println("Options:");
		System.err.println("\t-bed            : Input is a BED file.");
		System.err.println("\t-extract <num>  : Extract sub intervals of at least 'num' bases, having a conservarion score of at least 'minScore'. Only when input is a BED file.");
		System.err.println("\t-minScore <num> : Only annotate is score is greater to 'num'. Default: " + minScore);
		System.exit(1);
	}

}
