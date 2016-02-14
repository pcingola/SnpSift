package org.snpsift;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;

/**
 * Convert VCf file to allele matrix
 *
 * Note: Only use SNPs
 *
 * Note: Only variants with two possible alleles. I.e. the matrix has three possible values in each cell:
 * 		- 0, for allele 0/0
 * 		- 1, for allele 0/1 or 1/0
 * 		- 2, for allele 1/1
 *
 * @author pcingola
 */
public class SnpSiftCmdAlleleMatrix extends SnpSift {

	public static String SEPARATOR = "";
	public static int SHOW_EVERY = 1000;

	public SnpSiftCmdAlleleMatrix() {
		super(null, null);
	}

	public SnpSiftCmdAlleleMatrix(String[] args) {
		super(args, "alleleMatrix");
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length <= 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Argument starts with '-'?
			if (isOpt(arg)) {
				// No options available for this command
			} else vcfInputFile = arg;
		}
	}

	/**
	 * Process a VCF entry and return a string (tab separated values)
	 */
	public int processStr(VcfEntry vcfEntry, StringBuilder sbcodes) {
		// Add all genotype codes
		String sep = "";
		int countNonRef = 0;
		for (VcfGenotype gen : vcfEntry.getVcfGenotypes()) {
			int score = gen.getGenotypeCode();

			String sc = ".";
			if (score >= 0) {
				sc = Integer.toString(score);
				if (score > 0) countNonRef++;
			}

			sbcodes.append(sep + sc);
			sep = SEPARATOR;
		}

		return countNonRef;
	}

	/**
	 * Process the whole VCF file
	 */
	@Override
	public void run() {
		int i = 1;
		VcfFileIterator vcf = openVcfInputFile();
		for (VcfEntry ve : vcf) {
			if (vcf.isHeadeSection()) {
				System.out.print("#CHROM\tPOS\tREF\tALT");
				for (String sample : vcf.getVcfHeader().getSampleNames())
					System.out.print("\t" + sample);
				System.out.println("");
			}

			StringBuilder sbcodes = new StringBuilder();
			processStr(ve, sbcodes);

			System.out.println(ve.getChromosomeName() //
					+ "\t" + (ve.getStart() + 1) //
					+ "\t" + ve.getRef() //
					+ "\t" + ve.getAltsStr() //
					+ "\t" + sbcodes.toString() //
			);
			if (verbose) Gpr.showMark(i++, SHOW_EVERY);
		}

		if (verbose) Timer.showStdErr("Done");
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar alleleMat file.vcf > allele.matrix.txt");
		System.exit(1);
	}

}
