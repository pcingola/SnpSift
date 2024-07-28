package org.snpsift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.geneSets.GeneSet;
import org.snpeff.geneSets.GeneSets;
import org.snpeff.stats.CountByType;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEffect;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;

/**
 * Annotate a VCF file using Gene sets (MSigDb) or gene ontology (GO)
 *
 * @author pablocingolani
 */
public class SnpSiftCmdGeneSets extends SnpSift {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	public static final String INFO_GENE_SETS = "MSigDb";

	String msigdb;
	GeneSets geneSets;
	CountByType countByGeneSet;

	public SnpSiftCmdGeneSets() {
		super();
		countByGeneSet = new CountByType();
	}

	public SnpSiftCmdGeneSets(String args[]) {
		super(args);
		countByGeneSet = new CountByType();
	}

	/**
	 * Annotate one entry
	 */
	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		HashSet<String> sets = null;
		boolean annotated = false;

		// For all effects
		for (VcfEffect eff : vcfEntry.getVcfEffects()) {
			String gene = eff.getGeneName();

			// Do we have gene name field?
			if ((gene != null) && !gene.isEmpty()) {
				// Create hash
				if (sets == null) sets = new HashSet<>();

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
			ArrayList<String> setsSorted = new ArrayList<>();
			setsSorted.addAll(sets);
			Collections.sort(setsSorted);

			// Create INFO string
			StringBuilder setssb = new StringBuilder();
			for (String s : setsSorted) {
				String setName = VcfEntry.vcfInfoValueSafe(s);
				setssb.append((setssb.length() > 0 ? "," : "") + setName);
			}

			// Add INFO field
			vcfEntry.addInfo(INFO_GENE_SETS, setssb.toString());
			annotated = true;
		}

		return annotated;
	}

	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		super.annotateInit(vcfFile);

		if (msigdb == null || msigdb.isEmpty()) Log.fatalError("Missing argument / config: MSigDb file name");

		if (verbose) Log.info("Reading MSigDb from file: '" + msigdb + "'");
		geneSets = new GeneSets(msigdb);
		if (verbose) Log.info("Done. Total:\n\t" + geneSets.getGeneSetCount() + " gene sets\n\t" + geneSets.getGeneCount() + " genes");

		return true;
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> newHeaders = super.headers();
		newHeaders.add(new VcfHeaderInfo(INFO_GENE_SETS, VcfInfoType.String, VcfInfoNumber.UNLIMITED.toString(), "Gene set from MSigDB database (GSEA)"));
		return newHeaders;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		int argNum = 0;
		if (args.length == 0) usage(null);

		// Parse arguments
		if (args.length > argNum) msigdb = args[argNum++];
		if (args.length > argNum) vcfInputFile = args[argNum++];

		// Sanity check
		if (msigdb == null) usage("Missing 'msigdb.gmt'");
		if (vcfInputFile == null) usage("Missing 'file.vcf'");
	}

	/**
	 * Annotate entries
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	public List<VcfEntry> run(boolean createList) {
		LinkedList<VcfEntry> results = new LinkedList<>();

		if (verbose) Log.info("Annotating variants from: '" + vcfInputFile + "'");
		VcfFileIterator vcf = new VcfFileIterator(vcfInputFile);
		vcf.setDebug(debug);

		annotateInit(vcf);

		for (VcfEntry vcfEntry : vcf) {
			// Show header?
			if (vcf.isHeadeSection()) {
				addHeaders(vcf);
				String headerStr = vcf.getVcfHeader().toString();
				if (!headerStr.isEmpty()) print(headerStr);
			}

			annotate(vcfEntry);
			print(vcfEntry);

			if (createList) results.add(vcfEntry);
		}

		if (verbose) {
			Log.info("Done.");
			System.err.println("# Summary");
			System.err.println("#\tgene_set\tgene_set_size\tvariants");
			for (String gs : countByGeneSet.keysSorted())
				System.err.println("#\t" + gs + "\t" + geneSets.getGeneSet(gs).size() + "\t" + countByGeneSet.get(gs));
		}

		return results;
	}

	public void setMsigdb(String msigdb) {
		this.msigdb = msigdb;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar miSigDb.gmt file.vcf > file.geneSets.vcf");
		System.exit(1);
	}
}
