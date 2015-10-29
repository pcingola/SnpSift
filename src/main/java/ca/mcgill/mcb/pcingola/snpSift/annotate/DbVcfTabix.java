package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcf {

	public DbVcfTabix(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public List<VcfEntry> query(Marker marker) {
		throw new RuntimeException("Unimplemented");
	}

}
