package org.snpsift;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;

/**
 *
 * Check VCF files (run some simple checks)
 *
 * @author pcingola
 */
public class SnpSiftCmdVcfCheck extends SnpSift {

	public SnpSiftCmdVcfCheck() {
		super(null, null);
	}

	public SnpSiftCmdVcfCheck(String[] args) {
		super(args, "alleleMatrix");
	}

	/**
	 * Check this VCF file (just iterate over it using 'debug' mode
	 */
	void check(String vcfFile) {
		if (verbose) Timer.showStdErr("Processing file '" + vcfFile + "'");

		// Create an input file iterator
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);

		// Check every entry just by iterating over the whole file in 'debug' mode.
		vcf.setDebug(true);
		int count = 1;
		for (VcfEntry ve : vcf) {
			Gpr.showMark(count++, 1000);
		};

		if (verbose) Timer.showStdErr("Finished file '" + vcfFile + "'");
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length < 1) usage(null);
	}

	/**
	 * Process the whole VCF file
	 */
	@Override
	public boolean run() {
		for (String vcfFile : args)
			check(vcfFile);
		return true;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar vcfCheck file_1.vcf [file_2.vcf ... file_N.vcf]");
		System.exit(1);
	}

}
