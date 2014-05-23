package ca.mcgill.mcb.pcingola.snpSift;

import java.util.HashMap;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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

	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();
		newHeaders.add("##INFO=<ID=" + VARTYPE + ",Number=A,Type=Flag,Description=\"Variant types {SNP,MNP,INS,DEL,Mixed}\">");
		//
		newHeaders.add("##INFO=<ID=SNP,Number=0,Type=Flag,Description=\"Variant is a SNP\">");
		newHeaders.add("##INFO=<ID=MNP,Number=0,Type=Flag,Description=\"Variant is an MNP\">");
		newHeaders.add("##INFO=<ID=INS,Number=0,Type=Flag,Description=\"Variant is an insertion\">");
		newHeaders.add("##INFO=<ID=DEL,Number=0,Type=Flag,Description=\"Variant is an deletion\">");
		newHeaders.add("##INFO=<ID=MIXED,Number=0,Type=Flag,Description=\"Variant is mixture of INS/DEL/SNP/MNP\">");
		//
		newHeaders.add("##INFO=<ID=HOM,Number=0,Type=Flag,Description=\"Variant is homozygous\">");
		newHeaders.add("##INFO=<ID=HET,Number=0,Type=Flag,Description=\"Variant is heterozygous\">");
		//
		newHeaders.add("##INFO=<ID=" + VARTYPE + ",Number=A,Type=String,Description=\"Comma separated list of variant types. One per allele\">");
		return newHeaders;
	}

	/**
	 * Annotate one entry
	 * @param vcfEntry
	 */
	public void annotate(VcfEntry vcfEntry) {
		// Entry type?
		if (vcfEntry.getChangeType() != null) vcfEntry.addInfo(vcfEntry.getChangeType().toString());

		// Heterozygous?
		Boolean isHet = vcfEntry.calcHetero();
		if (isHet != null) vcfEntry.addInfo(isHet ? "HET" : "HOM");

		// Add vartype according to alleles
		StringBuilder sb = new StringBuilder();
		for (Variant sq : vcfEntry.variants()) {
			if (sb.length() > 0) sb.append(",");
			sb.append(sq.getChangeType());
		}

		if (sb.length() > 0) vcfEntry.addInfo(VARTYPE, sb.toString());
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

		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (showHeader) {
				addHeader(vcf);
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
