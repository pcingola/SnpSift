package ca.mcgill.mcb.pcingola.snpSift;

import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.interval.tree.IntervalForest;
import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalog;
import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalogEntry;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo.VcfInfoNumber;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Annotate a VCF file using GWAS catalog database
 *
 * Loads GWAS catalog in memory, thus it makes no assumption about order.
 *
 * @author pablocingolani
 */
public class SnpSiftCmdGwasCatalog extends SnpSift {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	public final String GWAS_CATALOG = "GWASCAT";
	public final String CONFIG_GWAS_DB_NAME = "gwascatalog";

	GwasCatalog gwasCatalog;
	IntervalForest intervalForest;

	public SnpSiftCmdGwasCatalog() {
		this(null);
	}

	public SnpSiftCmdGwasCatalog(String args[]) {
		super(args, "gwasCat");
	}

	/**
	 * Annotate input VCF file
	 */
	void annotate() {
		// Open file
		VcfFileIterator vcf = openVcfInputFile();
		vcf.setDebug(debug);

		annotateInit(vcf);

		int countAnnotated = 0, count = 0;
		boolean showHeader = true;

		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (showHeader) {
				addHeaders(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
				showHeader = false;
			}

			boolean annotated = annotate(vcfEntry);

			// Show entry
			System.out.println(vcfEntry);

			if (annotated) countAnnotated++;
			count++;
		}

		annotateFinish();

		double perc = (100.0 * countAnnotated) / count;
		if (verbose) Timer.showStdErr("Done." //
				+ "\n\tTotal annotated entries : " + countAnnotated //
				+ "\n\tTotal entries           : " + count //
				+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
		);
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		// Anything found? => Annotate
		boolean annotated = false;

		// Query interval tree
		for (Variant var : vcfEntry.variants()) {
			// Skip non-variants and huge deletions
			if (var.isVariant() || var.isStructuralHuge()) continue;

			Markers results = intervalForest.query(var);

			// Any results? Annotate VcfEntry
			if (!results.isEmpty()) {
				// First we need to get the original gwas-catalog entries (marker IDs are the keys)
				List<GwasCatalogEntry> resultsGwasCat = new LinkedList<>();

				for (Marker m : results) {
					// Map marker ID to original gwas-catalog entries
					String key = m.getId();
					List<GwasCatalogEntry> resultsKey = gwasCatalog.get(key);
					if (resultsKey != null) resultsGwasCat.addAll(resultsKey);
				}

				// Annotate
				vcfAnnotation(vcfEntry, resultsGwasCat);
				annotated = true;
			}
		}

		return annotated;
	}

	@Override
	public boolean annotateFinish() {
		return true; // Nothing to do
	}

	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {

		// Get database name from config file?
		// Note this can happen when invoked a VcfAnnotator (e.g. form ClinEff)
		if (dbFileName == null && config != null) {
			dbFileName = config.getDatabaseLocal(CONFIG_GWAS_DB_NAME);
		}

		// Read database
		readDb();

		// Convert to markers
		Genome genome = new Genome();
		Markers markers = new Markers();
		for (GwasCatalogEntry ge : gwasCatalog) {
			ge.chrId = Chromosome.simpleName(ge.chrId); // Conver to to simple name (e.g. no 'chr')

			int pos = ge.chrPos - 1; // Zero-based coordinates
			Chromosome chr = genome.getOrCreateChromosome(ge.chrId);
			String id = gwasCatalog.key(ge);

			Marker m = new Marker(chr, pos, pos, false, id);
			markers.add(m);
		}

		// Create tree
		if (verbose) Timer.showStdErr("Creating interval tree for GWAS catalog");
		intervalForest = new IntervalForest(markers);
		intervalForest.build();

		return true;
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> newHeaders = super.headers();

		newHeaders.add(new VcfHeaderInfo(GWAS_CATALOG + "_TRAIT", VcfInfoType.String, VcfInfoNumber.UNLIMITED.toString(), "GWAS catalog: Associated trait"));
		newHeaders.add(new VcfHeaderInfo(GWAS_CATALOG + "_P_VALUE", VcfInfoType.Float, VcfInfoNumber.UNLIMITED.toString(), "GWAS catalog: p-value"));
		newHeaders.add(new VcfHeaderInfo(GWAS_CATALOG + "_OR_BETA", VcfInfoType.Float, VcfInfoNumber.UNLIMITED.toString(), "GWAS catalog: OR or Beta"));
		newHeaders.add(new VcfHeaderInfo(GWAS_CATALOG + "_REPORTED_GENE", VcfInfoType.String, VcfInfoNumber.UNLIMITED.toString(), "GWAS catalog: Reported gene"));
		newHeaders.add(new VcfHeaderInfo(GWAS_CATALOG + "_PUBMED_ID", VcfInfoType.String, VcfInfoNumber.UNLIMITED.toString(), "GWAS catalog: Original paper's Pubmed ID"));

		return newHeaders;
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
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (isOpt(arg) && (arg.equals("-h") || arg.equals("-help"))) usage(null);
			else {
				if (vcfInputFile == null) vcfInputFile = arg;
				else usage("VCF input file already assigned to '" + vcfInputFile + "'");
			}
		}
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
		dbFileName = databaseFind();

		if (verbose) Timer.showStdErr("Annotating\n" //
				+ "\tInput file    : '" + (vcfInputFile != null ? vcfInputFile : "STDIN") + "'\n" //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		annotate();
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar gwasCat [file.vcf] > newFile.vcf.");
		usageGenericAndDb();

		System.exit(1);
	}

	/**
	 * Add annotation to buffer
	 */
	void vcfAnnotate(StringBuilder sb, String value) {
		if (sb.length() > 0) sb.append(",");

		if (value == null || value.isEmpty()) value = VcfFileIterator.MISSING;

		sb.append(VcfEntry.vcfInfoValueSafe(value));
	}

	/**
	 * Create an annotation string
	 */
	void vcfAnnotation(VcfEntry vcfEntry, List<GwasCatalogEntry> list) {
		new StringBuilder();

		// Add values from all GWAS catalog entries
		StringBuilder sbPubmedId = new StringBuilder();
		StringBuilder sbTrait = new StringBuilder();
		StringBuilder sbReportedGene = new StringBuilder();
		StringBuilder sbPvalue = new StringBuilder();
		StringBuilder sbOr = new StringBuilder();

		for (GwasCatalogEntry ge : list) {
			vcfAnnotate(sbPubmedId, ge.pubmedId);
			vcfAnnotate(sbTrait, ge.trait);
			vcfAnnotate(sbReportedGene, ge.reportedGene);
			vcfAnnotate(sbPvalue, "" + ge.pValue);

			if (Gpr.parseDoubleSafe(ge.orBeta) > 0) vcfAnnotate(sbOr, ge.orBeta);
		}

		// Add INFO fields
		if (sbTrait.length() > 0) vcfEntry.addInfo(GWAS_CATALOG + "_TRAIT", sbTrait.toString());
		if (sbPvalue.length() > 0) vcfEntry.addInfo(GWAS_CATALOG + "_P_VALUE", sbPvalue.toString());
		if (sbOr.length() > 0) vcfEntry.addInfo(GWAS_CATALOG + "_OR_BETA", sbOr.toString());
		if (sbReportedGene.length() > 0) vcfEntry.addInfo(GWAS_CATALOG + "_REPORTED_GENE", sbReportedGene.toString());
		if (sbPubmedId.length() > 0) vcfEntry.addInfo(GWAS_CATALOG + "_PUBMED_ID", sbPubmedId.toString());
	}

}
