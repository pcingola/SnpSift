package org.snpsift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Genome;
import org.snpeff.interval.Marker;
import org.snpeff.interval.Markers;
import org.snpeff.interval.Variant;
import org.snpeff.interval.tree.IntervalForest;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.gwasCatalog.GwasCatalog;
import org.snpsift.gwasCatalog.GwasCatalogEntry;

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
		super();
	}

	public SnpSiftCmdGwasCatalog(String args[]) {
		super(args);
	}

	/**
	 * Annotate input VCF file
	 */
	List<VcfEntry> annotate(boolean saveResults) {
		// Open file
		VcfFileIterator vcf = openVcfInputFile();
		vcf.setDebug(debug);

		annotateInit(vcf);

		int countAnnotated = 0, count = 0;
		boolean showHeader = true;
		List<VcfEntry> vcfEntries = saveResults ? new ArrayList<VcfEntry>() : null;

		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (showHeader) {
				addHeaders(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) print(headerStr);
				showHeader = false;
			}

			boolean annotated = annotate(vcfEntry);

			// Show entry
			if (saveResults) vcfEntries.add(vcfEntry);
			else print(vcfEntry);

			if (annotated) countAnnotated++;
			count++;
		}

		annotateFinish(vcf);

		double perc = (100.0 * countAnnotated) / count;
		if (verbose) Timer.showStdErr("Done." //
				+ "\n\tTotal annotated entries : " + countAnnotated //
				+ "\n\tTotal entries           : " + count //
				+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
		);

		return vcfEntries;
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		// Anything found? => Annotate
		boolean annotated = false;

		// Query interval tree
		for (Variant var : vcfEntry.variants()) {
			// Skip non-variants and huge deletions
			if (!var.isVariant() || var.isStructuralHuge()) continue;

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
	public boolean annotateFinish(VcfFileIterator vcfFile) {
		super.annotateFinish(vcfFile);
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

		buildIntervalForest();

		return true;
	}

	/**
	 * Build interval forest from markers
	 */
	void buildIntervalForest() {
		// Convert to markers
		Genome genome = new Genome();
		Markers markers = new Markers();
		for (GwasCatalogEntry ge : gwasCatalog) {
			ge.chrId = Chromosome.simpleName(ge.chrId); // Convert to to simple name (e.g. no 'chr')

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
		super.init();
		needsConfig = true;
		needsDb = true;
		needsGenome = true;
		dbTabix = false;
		dbType = "gwascatalog";
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length < 1) usage(null);

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
	public boolean run() {
		run(false);
		return true;
	}

	public List<VcfEntry> run(boolean saveResults) {
		// Read config
		if (config == null) loadConfig();

		// Find or download database
		dbFileName = databaseFind();

		if (verbose) Timer.showStdErr("Annotating:" //
				+ "\tInput file : '" + (vcfInputFile != null ? vcfInputFile : "STDIN") + "'" //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		return annotate(saveResults);
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar gwasCat [-db path/to/gwascat.txt] [file.vcf] > newFile.vcf.");
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
