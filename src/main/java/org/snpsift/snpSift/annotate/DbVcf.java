package org.snpsift.snpSift.annotate;

import java.util.Collection;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfHeader;

/**
 * Use a VCF file as a database for annotations
 *
 * A VCF database consists of a VCF file and an index.
 * When a query is made, the index is used to quickly get the file positions
 * where matching VCF entries are. File is read, entries are parsed and returned
 * as query() result.
 *
 * WARNING: VcfEntry may hold multiple variants (e.g. multi-allelic VcfEntries). So
 *          we index by variant and return all matching vcfEntries for
 *          a given variant. This is why we use 'VariantVcfEntry' as opposed
 *          to 'VcfEntry'
 *
 * TODO: If another query matches the same region of the file, then we could use
 * some sort of caching to speed up the process.
 *
 * TODO: If the same file region is matched multiple times by successive
 * queries, creating an intervalTree from the VCF entries matching the region
 * might be effective
 *
 *
 * @author pcingola
 */
public abstract class DbVcf implements DbMarker<Variant, VariantVcfEntry> {

	protected boolean debug = false;
	protected boolean verbose = false;
	protected String dbFileName;
	protected VcfHeader vcfHeader;

	public DbVcf(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	@Override
	public abstract void close();

	public VcfHeader getVcfHeader() {
		return vcfHeader;
	}

	@Override
	public abstract void open();

	/**
	 * Find matching entries in the database
	 */
	@Override
	public abstract Collection<VariantVcfEntry> query(Variant variant);

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
