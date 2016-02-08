package org.snpsift.snpSift;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.stats.AlleleCountStats;
import org.snpeff.stats.HomHetStats;
import org.snpeff.stats.TsTvStats;
import org.snpeff.stats.VariantTypeStats;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;

/**
 * Calculate Ts/Tv rations per sample (transitions vs transversions)
 *
 * @author pablocingolani
 */
public class SnpSiftCmdTsTv extends SnpSift {

	public static final int SHOW_EVERY = 1;
	public static final int SHOW_EVERY_NL = 100 * SHOW_EVERY;

	TsTvStats tsTvStats;
	HomHetStats homHetStats;
	AlleleCountStats alleleCountStats;
	VariantTypeStats variantTypeStats;
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

		// Create stats objects
		tsTvStats = new TsTvStats();
		homHetStats = new HomHetStats();
		alleleCountStats = new AlleleCountStats();
		variantTypeStats = new VariantTypeStats();

		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		// Read all vcfEntries
		int entryNum = 1;
		for (VcfEntry vcfEntry : vcfFile) {
			try {
				entryNum++;

				// Perform all stats
				tsTvStats.sample(vcfEntry);
				homHetStats.sample(vcfEntry);
				alleleCountStats.sample(vcfEntry);
				variantTypeStats.sample(vcfEntry);

				// Show progress
				Gpr.showMark(entryNum, 1);

			} catch (Throwable t) {
				error(t, "Error while processing VCF entry (line " + vcfFile.getLineNum() + ") :\n\t" + vcfEntry + "\n" + t);
			}

		}

		// Show results
		System.out.println("\nTS/TV stats:");
		System.out.println(tsTvStats);

		System.out.println("\nHom/Het stats:");
		System.out.println(homHetStats);

		System.out.println("\nVariant type stats:");
		System.out.println(variantTypeStats);

		System.out.println("\nAllele count stats:");
		System.out.println(alleleCountStats);

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
