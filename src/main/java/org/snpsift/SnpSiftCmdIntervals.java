package org.snpsift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.BedFileIterator;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Genome;
import org.snpeff.interval.Markers;
import org.snpeff.interval.Variant;
import org.snpeff.interval.tree.IntervalForest;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;

/**
 * Filter variants that hit intervals
 *
 * @author pablocingolani
 */
public class SnpSiftCmdIntervals extends SnpSift {

	String vcfFileName;
	LinkedList<String> bedFiles;
	IntervalForest intervalForest;
	Genome genome;
	boolean exclude;

	public SnpSiftCmdIntervals() {
		super();
	}

	public SnpSiftCmdIntervals(String[] args) {
		super(args);
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		super.init();
		verbose = false;
		genome = new Genome("genome");
		exclude = false;
		vcfFileName = "-"; // Default STDIN
	}

	/**
	 * Load all BED files
	 * @param bedFileNames
	 */
	void loadIntervals() {
		LinkedList<Variant> seqChangesAll = new LinkedList<>();

		// Read filter interval files
		for (String bedFileName : bedFiles) {
			if (verbose) Log.info("Reading filter interval file '" + bedFileName + "'");

			BedFileIterator bedFile = new BedFileIterator(bedFileName, genome);
			bedFile.setCreateChromos(true);

			List<Variant> seqChanges = bedFile.load();
			seqChangesAll.addAll(seqChanges);
		}

		if (verbose) Log.info("Total " + seqChangesAll.size() + " intervals added.");

		// Filter only variants that match these intervals
		if (verbose) Log.info("Building interval forest.");
		intervalForest = new IntervalForest();
		intervalForest.add(seqChangesAll);
		intervalForest.build();
		if (verbose) Log.info("Done.");
	}

	@Override
	public void parseArgs(String[] args) {
		if (args.length <= 0) usage(null);

		bedFiles = new LinkedList<>();
		for (int i = 0; i < args.length; i++) {
			// Argument starts with '-'?
			if (isOpt(args[i])) {
				if (args[i].equals("-h") || args[i].equalsIgnoreCase("-help")) usage(null);
				else if (args[i].equals("-x")) exclude = true;
				else if (args[i].equals("-i")) vcfFileName = args[++i];
			} else bedFiles.add(args[i]);
		}
	}

	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Load a file compare calls
	 *
	 * @param fileName
	 */
	public List<VcfEntry> run(boolean createList) {
		loadIntervals();
		if (verbose) Log.info("FileName: '" + vcfFileName + "'\n\t\t\tIntervals: " + bedFiles + "\n\t\t\tExclude : " + exclude);
		List<VcfEntry> results = new ArrayList<>();

		// Read all vcfEntries
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcfFile) {
			// Show header
			if (showHeader) {
				addHeaders(vcfFile);
				String headerStr = vcfFile.getVcfHeader().toString();
				if (!headerStr.isEmpty()) print(headerStr);
				showHeader = false;
			}

			// Does it hit any markers
			Markers queryResult = intervalForest.query(vcfEntry);

			// Show?
			if (queryResult.isEmpty()) {
				// It does not intercept any interval
				// Show if we are interested in excluding intervals
				if (exclude) {
					results.add(vcfEntry);
					print(vcfEntry);
				}
			} else if (!exclude) {
				results.add(vcfEntry);
				print(vcfEntry);
			}
		}

		return results;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar intervals [-x] file_1.bed file_2.bed ... file_N.bed");
		System.err.println("Options:");
		System.err.println("\t\t-i <file> :\tVCF file. Default STDIN");
		System.err.println("\t\t-x        :\tExclude VCF entries in intervals");
		System.exit(1);
	}
}
