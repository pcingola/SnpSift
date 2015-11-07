package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.vcf.VariantVcfEntry;

/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcf {

	public DbVcfTabix(String dbFileName) {
		super(dbFileName);
	}

	@Override
	public void close() {
		throw new RuntimeException("Unimplemented");
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public List<VariantVcfEntry> query(Variant variant) {
		throw new RuntimeException("Unimplemented");
	}

}
