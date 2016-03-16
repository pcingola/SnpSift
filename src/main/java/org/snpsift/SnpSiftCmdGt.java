package org.snpsift;

import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;

/**
 * Add genotype information to INFO fields
 *
 * @author pcingola
 */
public class SnpSiftCmdGt extends SnpSift {

	public static int SHOW_EVERY = 100;
	boolean uncompress;

	public SnpSiftCmdGt() {
		super(null, null);
	}

	public SnpSiftCmdGt(String[] args) {
		super(args, "gt");
	}

	@Override
	public String getOutput() {
		return output.toString();
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> newHeaders = super.headers();
		newHeaders.add(new VcfHeaderInfo(VcfEntry.VCF_INFO_HOMS, VcfInfoType.Integer, VcfInfoNumber.UNLIMITED.toString(), "List of sample indexes having homozygous ALT genotypes"));
		newHeaders.add(new VcfHeaderInfo(VcfEntry.VCF_INFO_HETS, VcfInfoType.Integer, VcfInfoNumber.UNLIMITED.toString(), "List of sample indexes having heterozygous ALT genotypes"));
		newHeaders.add(new VcfHeaderInfo(VcfEntry.VCF_INFO_NAS, VcfInfoType.Integer, VcfInfoNumber.UNLIMITED.toString(), "List of sample indexes having missing genotypes"));
		return newHeaders;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (isOpt(arg)) {
				if (arg.equals("-u")) uncompress = true;
				else usage("Unknown option '" + arg + "'");
			} else if (vcfInputFile == null) vcfInputFile = arg;
		}
	}

	/**
	 * Process a VCF entry and return a string (tab separated values)
	 */
	@Override
	public boolean run() {
		int i = 1;
		VcfFileIterator vcf = openVcfInputFile();
		for (VcfEntry ve : vcf) {
			processVcfHeader(vcf);

			if (uncompress) {
				// Uncompress
				print(ve.uncompressGenotypes().toString());
			} else {
				// Compress
				if (ve.compressGenotypes()) print(ve.toStringNoGt());
				else print(ve.toString());
			}

			if (verbose) Gpr.showMark(i++, SHOW_EVERY);
		}
		return true;
	}

	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar gt [options] [file.vcf] > file.gt.vcf");
		System.err.println("Options: ");
		System.err.println("\t-u   : Uncompress (restore genotype fields).");
		System.err.println("\tDefault 'file.vcf' is STDIN.");
		System.exit(1);
	}

}
