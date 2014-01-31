package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.geneSets.GeneSet;
import ca.mcgill.mcb.pcingola.geneSets.GeneSets;
import ca.mcgill.mcb.pcingola.stats.CountByType;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate a VCF file using Gene sets (MSigDb) or gene ontology (GO)
 * 
 * @author pablocingolani
 */
public class SnpSiftCmdGeneSets extends SnpSift {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	public static final String INFO_GENE_SETS = "MSigDb";

	String vcfFile;
	String msigdb;
	GeneSets geneSets;
	CountByType countByGeneSet;

	public SnpSiftCmdGeneSets(String args[]) {
		super(args, "geneSets");
		countByGeneSet = new CountByType();
	}

	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();
		newHeaders.add("##INFO=<ID=" + INFO_GENE_SETS + ",Number=.,Type=String,Description=\"Gene set from MSigDB database (GSEA)\">");
		return newHeaders;
	}

	/**
	 * Annotate one entry
	 * @param vcfEntry
	 */
	public void annotate(VcfEntry vcfEntry) {
		HashSet<String> sets = null;

		// For all effects
		for (VcfEffect eff : vcfEntry.parseEffects()) {
			String gene = eff.getGene();

			// Do we have gene name field?
			if ((gene != null) && !gene.isEmpty()) {
				// Create hash
				if (sets == null) sets = new HashSet<String>();

				// Find all gene sets that this gene belongs to
				HashSet<GeneSet> geneSetsByGene = geneSets.getGeneSetsByGene(gene);
				if (geneSetsByGene != null) {
					for (GeneSet gs : geneSetsByGene) {
						String geneSetName = gs.getName();
						sets.add(geneSetName);
					}
				}
			}
		}

		// Anything to annotate?
		if ((sets != null) && !sets.isEmpty()) {
			// Count
			for (String geneSetName : sets)
				countByGeneSet.inc(geneSetName);

			// Sort
			ArrayList<String> setsSorted = new ArrayList<String>();
			setsSorted.addAll(sets);
			Collections.sort(setsSorted);

			// Create INFO string
			StringBuilder setssb = new StringBuilder();
			for (String s : setsSorted) {
				String setName = VcfEntry.vcfInfoSafe(s);
				setssb.append((setssb.length() > 0 ? "," : "") + setName);
			}

			// Add INFO field
			vcfEntry.addInfo(INFO_GENE_SETS, setssb.toString());
		}
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		int argNum = 0;
		if (args.length == 0) usage(null);

		// Parse arguments
		if (args.length > argNum) msigdb = args[argNum++];
		if (args.length > argNum) vcfFile = args[argNum++];

		// Sanity check
		if (msigdb == null) usage("Missing 'msigdb.gmt'");
		if (vcfFile == null) usage("Missing 'file.vcf'");
	}

	/**
	 * Annotate entries
	 */
	@Override
	public void run() {
		if (verbose) Timer.showStdErr("Reading MSigDb from file: '" + msigdb + "'");
		geneSets = new GeneSets(msigdb);
		if (verbose) Timer.showStdErr("Done. Total:\n\t" + geneSets.getGeneSetCount() + " gene sets\n\t" + geneSets.getGeneCount() + " genes");

		if (verbose) Timer.showStdErr("Annotating variants from: '" + vcfFile + "'");
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (vcf.isHeadeSection()) {
				addHeader(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
			}

			annotate(vcfEntry);
			System.out.println(vcfEntry);
		}

		if (verbose) {
			Timer.showStdErr("Done.");
			System.err.println("# Summary");
			System.err.println("#\tgene_set\tgene_set_size\tvariants");
			for (String gs : countByGeneSet.keysSorted())
				System.err.println("#\t" + gs + "\t" + geneSets.getGeneSet(gs).size() + "\t" + countByGeneSet.get(gs));
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar miSigDb.gmt file.vcf > file.geneSets.vcf");
		System.exit(1);
	}
}
