package org.snpsift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.LineFileIterator;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Timer;
import org.snpeff.vcf.LineChrPos;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;

/**
 * Sort VCF file/s by chromosome & position
 *
 * @author pablocingolani
 */
public class SnpSiftCmdSort extends SnpSift {

	public static final int SHOW = 1000;
	public static final int SHOW_LINES = 100 * SHOW;

	ArrayList<String> fileNames;
	VcfHeader vcfHeader;
	List<LineChrPos> vcfLines;

	public SnpSiftCmdSort() {
		super();
	}

	public SnpSiftCmdSort(String args[]) {
		super(args);
	}

	/**
	 * Create a list of VcfEntry using raw lines
	 * Used for testing / debugging
	 */
	List<VcfEntry> createList() {
		List<VcfEntry> ves = new LinkedList<>();
		VcfFileIterator vcf = new VcfFileIterator();
		vcf.setVcfHeader(vcfHeader);
		int lineNum = 0;
		for (LineChrPos lp : vcfLines) {
			VcfEntry ve = new VcfEntry(vcf, lp.getLine(), lineNum++, true);
			ves.add(ve);
		}
		return ves;
	}

	/**
	 * Load VCF files
	 */
	public void loadVcfFiles() {
		vcfLines = new ArrayList<>();
		vcfHeader = null;

		// Iterate all files
		for (String file : fileNames) {
			if (verbose) Timer.showStdErr("Loading file '" + file + "'");
			processHeader(file);

			// Read the whole file
			// Note: We use a lightweight 'LineChrPos' instead of VcfEntry
			//       for memory and speed efficiency
			LineFileIterator lfi = new LineFileIterator(file);
			for (String line : lfi)
				if (!line.startsWith("#") && !line.isEmpty()) {
					LineChrPos lineChrPos = new LineChrPos(line);
					vcfLines.add(lineChrPos);
				}
		}
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		// Initialize
		fileNames = new ArrayList<>();

		// Parse args
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (isOpt(arg)) usage("Unknown option '" + arg + "'");
			else fileNames.add(arg);
		}

		// Use STDIN if no input file is specified
		if (fileNames.size() <= 0) fileNames.add("-");
	}

	/**
	 * Read header and add missing headers
	 */
	void processHeader(String file) {
		// Open VCF file
		VcfFileIterator vcf = new VcfFileIterator(file);
		vcf.next();

		// Process header
		if (vcfHeader == null) vcfHeader = vcf.getVcfHeader();
		else vcfHeader.add(vcf.getVcfHeader());

		vcf.close();
	}

	/**
	 * Run
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	public List<VcfEntry> run(boolean createList) {
		loadVcfFiles();
		sort();
		return (createList ? createList() : null);
	}

	void sort() {
		Collections.sort(vcfLines);

		if (quiet) return; // Don't show results?

		// Show header
		System.out.println(vcfHeader);

		// Show lines
		for (LineChrPos lp : vcfLines)
			System.out.println(lp.getLine());
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar sort file.vcf [file_2.vcf ... file_N.vcf]");
		System.err.println("Note : If more than one file is given, files are merged and then sorted.");
		System.err.println("Note2: Loads the file/s in memory. Not suitable for large VCF files.");
		System.exit(1);
	}
}
