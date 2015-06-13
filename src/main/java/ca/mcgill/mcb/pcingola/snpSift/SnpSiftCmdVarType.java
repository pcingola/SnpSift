package ca.mcgill.mcb.pcingola.snpSift;

import java.util.HashMap;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo.VcfInfoNumber;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Annotate a VCF file with variant type
 *
 * @author pablocingolani
 */
public class SnpSiftCmdVarType extends SnpSift {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	public static final String VARTYPE = "VARTYPE";

	String vcfFile;
	HashMap<String, String> db = new HashMap<String, String>();

	public SnpSiftCmdVarType(String args[]) {
		super(args, "varType");
	}

	/**
	 * Annotate one entry
	 */
	@Override
	public void annotate(VcfEntry vcfEntry) {
		// Entry type?
		if (vcfEntry.getVariantType() != null) vcfEntry.addInfo(vcfEntry.getVariantType().toString(), null);

		// Heterozygous?
		Boolean isHet = vcfEntry.calcHetero();
		if (isHet != null) vcfEntry.addInfo(isHet ? "HET" : "HOM", null);

		// Add vartype according to alleles
		StringBuilder sb = new StringBuilder();
		for (Variant sq : vcfEntry.variants()) {
			if (sb.length() > 0) sb.append(",");
			sb.append(sq.getVariantType());
		}

		if (sb.length() > 0) vcfEntry.addInfo(VARTYPE, sb.toString());
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> newHeaders = super.headers();

		newHeaders.add(new VcfHeaderInfo(VARTYPE, VcfInfoType.String, VcfInfoNumber.ALLELE.toString(), "Comma separated list of variant types. One per allele"));

		newHeaders.add(new VcfHeaderInfo("SNP", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is a SNP"));
		newHeaders.add(new VcfHeaderInfo("MNP", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is a MNP"));
		newHeaders.add(new VcfHeaderInfo("INS", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is a INS"));
		newHeaders.add(new VcfHeaderInfo("DEL", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is a DEL"));
		newHeaders.add(new VcfHeaderInfo("MIXED", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is a MIXED"));

		newHeaders.add(new VcfHeaderInfo("HOM", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is homozygous"));
		newHeaders.add(new VcfHeaderInfo("HET", VcfInfoType.Flag, VcfInfoNumber.ALLELE.toString(), "Variant is heterozygous"));

		return newHeaders;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		int argNum = 0;
		if (args.length == 0) usage(null);

		if (args.length >= argNum) vcfFile = args[argNum++];
		else usage("Missing 'file.vcf'");
	}

	/**
	 * Annotate entries
	 */
	@Override
	public void run() {
		if (verbose) Timer.showStdErr("Annotating variants type entries from: '" + vcfFile + "'");

		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		vcf.setDebug(debug);

		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (showHeader) {
				addHeaders(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
				showHeader = false;
			}

			annotate(vcfEntry);
			System.out.println(vcfEntry);
		}
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar varType file.vcf > newFile.vcf.");
		System.exit(1);
	}
}
