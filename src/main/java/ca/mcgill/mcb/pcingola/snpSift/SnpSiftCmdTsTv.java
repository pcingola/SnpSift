package ca.mcgill.mcb.pcingola.snpSift;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.stats.TsTvStats;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Calculate Ts/Tv rations per sample (transitions vs transversions)
 *
 * @author pablocingolani
 */
public class SnpSiftCmdTsTv extends SnpSift {

	public static final int SHOW_EVERY = 1000;
	public static final int SHOW_EVERY_NL = 100 * SHOW_EVERY;

	TsTvStats tsTvStats;
	String vcfFileName;

	public SnpSiftCmdTsTv(String[] args) {
		super(args, "tstv");
	}

	/**
	 * Show an error (if not 'quiet' mode)
	 * @param message
	 */
	@Override
	public void error(Throwable e, String message) {
		e.printStackTrace();
		System.err.println(message);
	}

	/**
	 * Parse command line arguments
	 * @param args
	 */
	@Override
	public void parse(String[] args) {
		if (args.length < 1) usage(null);
		int argc = 0;
		vcfFileName = args[argc++]; // VCF file
	}

	/**
	 * Analyze the file
	 */
	@Override
	public void run() {
		Timer.showStdErr("Analysing '" + vcfFileName + "'");

		tsTvStats = new TsTvStats(); // Create stats object

		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setCreateChromos(true); // Create chromosomes when needed
		vcfFile.setDebug(debug);

		// Read all vcfEntries
		int entryNum = 1;
		for (VcfEntry vcfEntry : vcfFile) {
			try {
				entryNum++;
				tsTvStats.sample(vcfEntry);

				// Show progress
				if (entryNum % SHOW_EVERY == 0) {
					if (entryNum % SHOW_EVERY_NL == 0) System.err.println('.');
					else System.err.print('.');
				}

			} catch (Throwable t) {
				error(t, "Error while processing VCF entry (line " + vcfFile.getLineNum() + ") :\n\t" + vcfEntry + "\n" + t);
			}

		}

		System.err.println("");
		System.out.println(tsTvStats);
		Timer.showStdErr("Done");
	}

	/**
	 * Show usage and exit
	 */
	@Override
	public void usage(String errMsg) {
		if (errMsg != null) System.err.println("Error: " + errMsg);
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar tstv file1.vcf\nWARNING: Only SNPs are used for Ts/Tv calculations.");
		System.exit(1);
	}
}
