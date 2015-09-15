package ca.mcgill.mcb.pcingola.snpSift;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo.VcfInfoNumber;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

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
	public void parse(String[] args) {
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
	public void run() {
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
