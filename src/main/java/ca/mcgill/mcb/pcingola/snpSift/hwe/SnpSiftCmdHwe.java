package ca.mcgill.mcb.pcingola.snpSift.hwe;

import java.util.List;

import akka.actor.Actor;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import ca.mcgill.mcb.pcingola.akka.Master;
import ca.mcgill.mcb.pcingola.akka.vcf.VcfWorkQueue;
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

	String vcfFileName;

	/**
	 * Main
	 * @param args
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
		addh.add("##INFO=<ID=HWE,Number=1,Type=Float,Description=\"Hardy–Weinberg 'p'.\">");
		addh.add("##INFO=<ID=HWEP,Number=1,Type=Float,Description=\"Hardy–Weinberg p-value using Fisher exact test.\">");
		addh.add("##INFO=<ID=HWEPCHI,Number=1,Type=Float,Description=\"Hardy–Weinberg p-value using Chi sqaure approximation.\">");
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
			if (args[argc].startsWith("-")) { // Argument starts with '-'?

				if (args[argc].equals("-h") || args[argc].equalsIgnoreCase("-help")) usage(null);
				else if (args[argc].equals("-v")) verbose = true;
				else if (args[argc].equals("-q")) verbose = false;
				else if (args[argc].equals("-t")) {
					numWorkers = Gpr.parseIntSafe(args[++argc]);
					if (numWorkers <= 0) usage("Number of threads should be a positive number.");
				} else usage("Unknown option '" + args[argc] + "'");
			} else vcfFileName = args[argc++];
		}

		// Sanity check
		if (vcfFileName == null) usage("Missing VCF file name");
	}

	/**
	 * Analyze the file (run multi-thread mode)
	 */
	@Override
	public void run() {
		// Run multi or single threaded versions
		if (numWorkers == 1) runSingle();
		else runMulti();
	}

	/**
	 * Analyze the file (run multi-thread mode)
	 */
	void runMulti() {
		final String addHeader[] = addHeader().toArray(new String[0]);

		Timer.showStdErr("Reading '" + vcfFileName + "'. Running multi-threaded mode (numThreads=" + numWorkers + ").");

		// Master factory
		int batchSize = Master.DEFAULT_BATCH_SIZE;
		Props props = new Props(new UntypedActorFactory() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() {
				MasterVcfHwe master = new MasterVcfHwe(numWorkers);
				master.setAddHeader(addHeader);
				return master;
			}
		});

		// Create and run workers
		VcfWorkQueue vcfWorkQueue = new VcfWorkQueue(vcfFileName, batchSize, SHOW_EVERY, props);
		vcfWorkQueue.run(true);

		Timer.showStdErr("Done.");
	}

	/**
	 * Analyze the file (run single thread mode, which is a lot easier to debug)
	 */
	void runSingle() {
		Timer.showStdErr("Reading '" + vcfFileName + "'. Running single threaded mode.");

		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		VcfHwe vcfHwe = new VcfHwe();

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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar hwe [-v] [-q] [-p numThreads] file.vcf.");
		System.err.println("\t-q       : Be quite");
		System.err.println("\t-v       : Be verbose");
		System.err.println("\t-t <num> : Number of threads. Default: " + numWorkers);
		System.exit(1);
	}
}
