package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VariantVcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use an uncompressed sorted VCF file as a database for annotations
 *
 * Note: Assumes that the VCF database file is sorted and uncompressed.
 *       Each VCF entry should be sorted according to position (as the VCF norm specifies).
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2).
 *       But entries for the same chromosome should be together.
 *
 * Note: Old VCF specifications did not require VCF files to be sorted.
 *
 * @author pcingola
 */
public class DbVcfSorted extends DbVcf {

	VcfIndex vcfIndex;

	public DbVcfSorted(String dbFileName) {
		super(dbFileName);
	}

	@Override
	public void close() {
		if (vcfIndex != null) {
			vcfIndex.close(); // We have to close vcfDbFile because it was opened using a BufferedReader (this sets autoClose to 'false')
			vcfIndex = null;
		}
	}

	/**
	 * Open VCF database annotation file
	 */
	@Override
	public void open() {
		if (debug) Gpr.debug("Open database file:" + dbFileName);

		vcfIndex = new VcfIndex(dbFileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.setDebug(debug);
		vcfIndex.open();
		vcfHeader = vcfIndex.getVcf().getVcfHeader();
		vcfIndex.index();
	}

	@Override
	public List<VariantVcfEntry> query(Variant variant) {
		Markers results = vcfIndex.query(variant);

		List<VariantVcfEntry> list = new LinkedList<VariantVcfEntry>();
		for (Marker m : results) {
			VcfEntry ve = (VcfEntry) m;
			list.addAll(VariantVcfEntry.factory(ve));
		}

		return list;
	}

}
