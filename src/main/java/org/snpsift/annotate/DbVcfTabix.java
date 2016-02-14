package org.snpsift.annotate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;

import net.sf.samtools.tabix.TabixIterator;
import net.sf.samtools.tabix.TabixReader;

/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcf {

	protected TabixReader tabixReader;
	protected VcfFileIterator vcf;

	public DbVcfTabix(String dbFileName) {
		super(dbFileName);
	}

	@Override
	public void close() {
		if (tabixReader != null) tabixReader.close();
		tabixReader = null;

		if (vcf != null) vcf.close();
		vcf = null;
		vcfHeader = null;
	}

	/**
	 * Initialize tabix reader
	 */
	protected boolean initTabix(String fileName) {
		try {
			// Do we have a tabix file?
			String indexFile = fileName + ".tbi";
			if (!Gpr.exists(indexFile)) throw new RuntimeException("Cannot find tabix index file '" + indexFile + "'");

			// Open tabix reader
			tabixReader = new TabixReader(fileName);
			tabixReader.setShowHeader(false); // Don't show header line in query results
		} catch (IOException e) {
			throw new RuntimeException("Error opening tabix file '" + fileName + "'", e);
		}

		return true;
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		// Open VCF file and read header
		vcf = new VcfFileIterator(dbFileName);
		vcfHeader = vcf.readHeader();

		// Open tabix index
		initTabix(dbFileName);
	}

	@Override
	public List<VariantVcfEntry> query(Variant variant) {
		List<VariantVcfEntry> results = new LinkedList<>();

		// Query and parse results
		TabixIterator ti = tabixReader.query(variant);

		// Any results?
		if (ti != null) {
			for (String line : ti) {
				line = Gpr.removeBackslashR(line);
				VcfEntry ve = vcf.parseVcfLine(line);
				results.addAll(VariantVcfEntry.factory(ve));
			}
		}

		return results;
	}

}
