package org.snpsift;

import java.util.ArrayList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.ped.PedPedigree;
import org.snpeff.ped.TfamEntry;
import org.snpeff.stats.CountByType;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;

/**
 * Annotate if a variant is 'private'. I.e. only represented within a family (or group)
 *
 * @author pcingola
 */
public class SnpSiftCmdPrivate extends SnpSift {

	boolean headerSummary = true;
	int countLines = 0, countAnnotated = 0;
	String tfamFile; // File names
	String[] sampleNum2group;
	List<String> sampleIds; // Sample IDs
	PedPedigree pedigree;

	public SnpSiftCmdPrivate(String[] args) {
		super(args, "private");
	}

	/**
	 * Parse a single VCF entry
	 */
	@Override
	public boolean annotate(VcfEntry ve) {
		String privateGorup = privateGroup(ve);

		// Is there a private group?
		if (privateGorup != null) {
			ve.addInfo(VcfEntry.VCF_INFO_PRIVATE, privateGorup);
			countAnnotated++;
			return true;
		}

		countLines++;
		return false;
	}

	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		loadTfam();

		countLines = 0;
		countAnnotated = 0;
		return true;
	}

	/**
	 * Load all data
	 */
	void loadTfam() {
		if (verbose) Timer.showStdErr("Loading TFAM file '" + tfamFile + "'");
		pedigree = new PedPedigree();
		pedigree.loadTfam(tfamFile);
	}

	@Override
	public void parseArgs(String[] args) {
		if (args.length <= 0) usage(null);

		for (int argc = 0; argc < args.length; argc++) {
			if (isOpt(args[argc])) usage("Unknown option '" + args[argc] + "'"); // Argument starts with '-'? (all default arguments are processed by SnpSift
			else if (tfamFile == null) tfamFile = args[argc];
			else if (vcfInputFile == null) vcfInputFile = args[argc];
		}

		// Sanity check
		if (tfamFile == null) usage("Missing paramter 'file.tped'");
	}

	/**
	 * Parse VCF header to get sample IDs
	 */
	List<String> parseSampleIds(VcfFileIterator vcf) {
		// Get sample names
		sampleIds = vcf.getSampleNames();

		// Initialize sampleNum2group mapping
		CountByType countByGroup = new CountByType();
		sampleNum2group = new String[sampleIds.size()];
		int sampleNum = 0, missing = 0;
		for (String id : sampleIds) {
			TfamEntry tfam = pedigree.get(id);
			String groupId = "";

			if (tfam == null) {
				missing++;
				System.err.println("WARNING: VCF sample '" + id + "' not found in TFAM file.");
			} else groupId = tfam.getFamilyId();

			// Assign group
			sampleNum2group[sampleNum] = groupId;
			countByGroup.inc(groupId);
			sampleNum++;
		}

		if (missing == sampleIds.size()) throw new RuntimeException("All samples are missing in TFAM file!");

		// Show counts by group
		if (verbose) System.err.println("Counts by group:\nGroup\tCount\n" + countByGroup);

		return sampleIds;
	}

	/**
	 * Name of the group, if this variant private. Null otherwise
	 */
	String privateGroup(VcfEntry ve) {
		String groupPrev = null;
		int sampleNum = 0;

		for (VcfGenotype gen : ve) {
			// Is this genotype a variant?
			if (gen.isVariant()) {
				String group = sampleNum2group[sampleNum]; // Group for this genotype

				// Analyze groups
				if (group.isEmpty()) {
					// Nothing to do
				} else if (groupPrev == null) groupPrev = group;
				else if (!group.equals(groupPrev)) return null; // Variant present in another group? Then it is not private!
			}

			sampleNum++;
		}

		return groupPrev; // If there was at least one variant, then it was private
	}

	/**
	 * Run annotations
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run annotations. Create a list of VcfEntries if 'createList' is true (use for test cases)
	 */
	public List<VcfEntry> run(boolean createList) {
		ArrayList<VcfEntry> vcfEntries = new ArrayList<VcfEntry>();
		VcfFileIterator vcf = openVcfInputFile();
		vcf.setDebug(debug);

		annotateInit(vcf);

		// Read VCF
		for (VcfEntry ve : vcf) {
			// Read header info
			if (vcf.isHeadeSection()) {
				// Get sameple names
				sampleIds = parseSampleIds(vcf);

				// Update header
				vcf.getVcfHeader().addLine("##INFO=<ID=" + VcfEntry.VCF_INFO_PRIVATE + ",Number=1,Type=String,Description=\"If the variant is private (i.e. belongs only to one group or family) the group name is shown. Groups from file = '" + tfamFile + "'\">");

				// Show header
				if (!createList) System.out.println(vcf.getVcfHeader());
				vcfHeaderProcessed = true;
			}

			annotate(ve);

			// Show (or add to list)
			if (createList) vcfEntries.add(ve);
			else System.out.println(ve);
		}

		if (verbose) Timer.showStdErr("Done.\n\tVCF entries: " + countLines + "\n\tVCF entries annotated: " + countAnnotated);

		annotateFinish(vcf);

		return vcfEntries;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar private file.tfam file.vcf");
		System.err.println("Where:");
		System.err.println("\tfile.tfam  : File with genotypes and groups information (in PLINK's TFAM format)");
		System.err.println("\tfile.vcf   : A VCF file (variants and genotype data). Default: 'STDIN'");
		System.exit(1);
	}
}
