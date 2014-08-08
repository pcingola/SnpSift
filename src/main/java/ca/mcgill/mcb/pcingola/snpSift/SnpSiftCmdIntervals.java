package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.BedFileIterator;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.interval.tree.IntervalForest;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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

	public SnpSiftCmdIntervals(String[] args) {
		super(args, "int");
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
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
		LinkedList<Variant> seqChangesAll = new LinkedList<Variant>();

		// Read filter interval files
		for (String bedFileName : bedFiles) {
			if (verbose) Timer.showStdErr("Reading filter interval file '" + bedFileName + "'");

			BedFileIterator bedFile = new BedFileIterator(bedFileName, genome);
			bedFile.setCreateChromos(true);

			List<Variant> seqChanges = bedFile.load();
			seqChangesAll.addAll(seqChanges);
		}

		if (verbose) Timer.showStdErr("Total " + seqChangesAll.size() + " intervals added.");

		if (verbose) Timer.showStdErr("Building interval forest.");
		intervalForest = new IntervalForest(); // Filter only seqChanges that match these intervals
		intervalForest.add(seqChangesAll);
		intervalForest.build();
		if (verbose) Timer.showStdErr("Done.");
	}

	@Override
	public void parse(String[] args) {
		if (args.length <= 0) usage(null);

		bedFiles = new LinkedList<String>();
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
	public void run() {
		run(false);
	}

	/**
	 * Load a file compare calls
	 *
	 * @param fileName
	 */
	public List<VcfEntry> run(boolean createList) {
		loadIntervals();
		if (verbose) Timer.showStdErr("FileName: '" + vcfFileName + "'\n\t\t\tIntervals: " + bedFiles + "\n\t\t\tExclude : " + exclude);
		List<VcfEntry> results = new ArrayList<VcfEntry>();

		// Read all vcfEntries
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcfFile) {
			// Show header
			if (showHeader) {
				addHeader(vcfFile);
				String headerStr = vcfFile.getVcfHeader().toString();
				if (!headerStr.isEmpty()) print(headerStr);
				showHeader = false;
			}

			// Does it hit any markers
			Markers queryResult = intervalForest.query(vcfEntry);

			// Show?
			if (queryResult.isEmpty()) {
				// It does not intercept any interval. Show if we are interested in excluding intervals
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
