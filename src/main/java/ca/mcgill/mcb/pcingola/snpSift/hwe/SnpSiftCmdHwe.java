package ca.mcgill.mcb.pcingola.snpSift.hwe;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSift;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Calculate Hardy-Weimberg equilibrium and goodness of fit for each entry in a VCF file
 *
 * @author pablocingolani
 */
public class SnpSiftCmdHwe extends SnpSift {

	public static final int SHOW_EVERY = 1000;

	/**
	 * Main
	 */
	public static void main(String[] args) {
		SnpSiftCmdHwe vcfhwe = new SnpSiftCmdHwe(args);
		vcfhwe.run();
	}

	public SnpSiftCmdHwe() {
		super(null, null);
	}

	public SnpSiftCmdHwe(String[] args) {
		super(args, "hwe");
	}

	@Override
	protected List<String> addHeader() {
		List<String> addh = super.addHeader();
		addh.add("##INFO=<ID=HWE,Number=1,Type=Float,Description=\"Hardy€“Weinberg 'p'.\">");
		addh.add("##INFO=<ID=HWEP,Number=1,Type=Float,Description=\"HardyWeinberg p-value using Fisher exact test.\">");
		addh.add("##INFO=<ID=HWEPCHI,Number=1,Type=Float,Description=\"HardyWeinberg p-value using Chi sqaure approximation.\">");
		return addh;
	}

	/**
	 * Parse command line arguments
	 * @param args
	 */
	@Override
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int argc = 0; argc < args.length; argc++) {
			String arg = args[argc];

			if (isOpt(arg)) { // Is it a command line option?

				if (arg.equals("-h") || args[argc].equalsIgnoreCase("-help")) usage(null);
				else if (arg.equals("-v")) verbose = true;
				else if (arg.equals("-q")) verbose = false;
				else if (arg.equals("-d")) debug = false;
				else usage("Unknown option '" + args[argc] + "'");
			} else vcfInputFile = args[argc++];
		}

		// Sanity check
		if (vcfInputFile == null) vcfInputFile = "-";
	}

	/**
	 * Analyze the file (run multi-thread mode)
	 */
	@Override
	public void run() {
		Timer.showStdErr("Reading '" + vcfInputFile + "'. Running single threaded mode.");

		VcfFileIterator vcfFile = new VcfFileIterator(vcfInputFile);
		vcfFile.setDebug(debug);

		VcfHwe vcfHwe = new VcfHwe();
		VcfHwe.debug = debug;

		// Read all vcfEntries
		int entryNum = 1;
		for (VcfEntry vcfEntry : vcfFile) {

			if (entryNum == 1) {
				addHeader();
				String headerStr = vcfFile.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
			}

			vcfHwe.hwe(vcfEntry, true);
			System.out.println(vcfEntry);

			// Show progress
			Gpr.showMark(entryNum++, SHOW_EVERY);
		}

		Timer.showStdErr("Done: " + entryNum + " entries processed.");
	}

	/**
	 * Show usage and exit
	 */
	@Override
	public void usage(String errMsg) {
		if (errMsg != null) System.err.println("Error: " + errMsg);
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar hwe [-v] [-q] [file.vcf]");
		System.err.println("\t-q       : Be quite");
		System.err.println("\t-v       : Be verbose");
		System.exit(1);
	}
}
