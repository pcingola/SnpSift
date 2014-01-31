package ca.mcgill.mcb.pcingola.snpSift.matrix;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSift;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Add genotype information to INFO fields
 * 
 * @author pcingola
 */
public class SnpSiftCmdGt extends SnpSift {

	public static int SHOW_EVERY = 100;
	String vcfFile;
	boolean uncompress;
	boolean save; // Save output to string buffer instead of printing it to STDOUT (used for test cases)
	StringBuilder output = new StringBuilder();

	public SnpSiftCmdGt() {
		super(null, null);
	}

	public SnpSiftCmdGt(String[] args) {
		super(args, "gt");
	}

	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();
		newHeaders.add("##INFO=<ID=" + VcfEntry.VCF_INFO_HOMS + ",Number=.,Type=Integer,Description=\"List of sample indexes having homozygous ALT genotypes\">");
		newHeaders.add("##INFO=<ID=" + VcfEntry.VCF_INFO_HETS + ",Number=.,Type=Integer,Description=\"List of sample indexes having heterozygous ALT genotypes\">");
		newHeaders.add("##INFO=<ID=" + VcfEntry.VCF_INFO_NAS + ",Number=.,Type=Integer,Description=\"List of sample indexes having missing genotypes\">");
		newHeaders.add("##SnpSiftCmd=\"" + commandLineStr() + "\"");
		return newHeaders;
	}

	@Override
	public String getOutput() {
		return output.toString();
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
			} else if (vcfFile == null) vcfFile = args[i];
		}
	}

	void print(String out) {
		if (save) output.append(out + "\n");
		else System.out.println(out);
	}

	/**
	 * Process a VCF entry and return a string (tab separated values)
	 * @param vcfEntry
	 * @return
	 */
	@Override
	public void run() {
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		if (save) showHeader = false; // No need to show header

		int i = 1;
		for (VcfEntry ve : vcf) {
			if (vcf.isHeadeSection()) {
				handleVcfHeader(vcf);

				if (save) print(vcf.getVcfHeader().toString()); // Save header to output buffer
			}

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

	public void setSave(boolean save) {
		this.save = save;
	}

	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar gt [options] file.vcf > file.gt.vcf");
		System.err.println("Options: ");
		System.err.println("\t-u   : Uncompress (restore genotype fields).");
		System.exit(1);
	}

}
