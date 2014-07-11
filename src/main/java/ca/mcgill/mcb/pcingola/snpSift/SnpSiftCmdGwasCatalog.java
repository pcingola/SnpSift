package ca.mcgill.mcb.pcingola.snpSift;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalog;
import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalogEntry;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate a VCF file using GWAS catalog database
 *
 * Loads GWAS catalog in memory, thus it makes no assumption about order.
 *
 * @author pablocingolani
 */
public class SnpSiftCmdGwasCatalog extends SnpSift {

	public final String GWAS_CATALOG_TRAIT = "GWASCAT";

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	String vcfFile;
	GwasCatalog gwasCatalog;

	public SnpSiftCmdGwasCatalog(String args[]) {
		super(args, "gwasCat");
	}

	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();
		newHeaders.add("##INFO=<ID=" + GWAS_CATALOG_TRAIT + ",Number=.,Type=String,Description=\"Trait related to this chromosomal position, according to GWAS catalog\">");
		return newHeaders;
	}

	void annotate() {
		readDb();

		if (verbose) Timer.showStdErr("Annotating entries from: '" + vcfFile + "'");

		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		vcf.setDebug(debug);

		int countAnnotated = 0, count = 0;
		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (showHeader) {
				addHeader(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
				showHeader = false;
			}

			// Anything found? => Annotate
			boolean annotated = false;
			List<GwasCatalogEntry> list = gwasCatalog.get(vcfEntry.getChromosomeName(), vcfEntry.getStart());

			// Any annotations? Add them
			if ((list != null) && (!list.isEmpty())) {
				annotated = true;
				String annotation = vcfAnnotation(list);
				vcfEntry.addInfo(GWAS_CATALOG_TRAIT, annotation);
			}

			// Show entry
			System.out.println(vcfEntry);

			if (annotated) countAnnotated++;
			count++;
		}

		double perc = (100.0 * countAnnotated) / count;
		if (verbose) Timer.showStdErr("Done." //
				+ "\n\tTotal annotated entries : " + countAnnotated //
				+ "\n\tTotal entries           : " + count //
				+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
		);
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		needsConfig = true;
		needsDb = true;
		dbTabix = false;
		dbType = "gwascatalog";
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length < 1) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			vcfFile = arg;
		}

		if (vcfFile == null) usage("Missing 'file.vcf'");
	}

	/**
	 * Read database
	 */
	public void readDb() {
		if (verbose) Timer.showStdErr("Loading database: '" + dbFileName + "'");
		gwasCatalog = new GwasCatalog(dbFileName);
	}

	/**
	 * Annotate entries
	 */
	@Override
	public void run() {
		// Read config
		if (config == null) loadConfig();

		// Find or download database
		dbFileName = databaseFindOrDownload();

		if (verbose) Timer.showStdErr("Annotating\n" //
				+ "\tInput file    : '" + vcfFile + "'\n" //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		annotate();
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar gwasCat file.vcf > newFile.vcf.");

		usageGenericAndDb();

		System.exit(1);
	}

	/**
	 * Create an annotation string
	 * @return
	 */
	String vcfAnnotation(List<GwasCatalogEntry> list) {
		StringBuilder sb = new StringBuilder();

		// Add all traits (comma separated)
		for (GwasCatalogEntry ge : list) {
			if (sb.length() > 0) sb.append(",");
			sb.append(ge.getTraitCode());
		}

		return sb.toString();
	}
}
