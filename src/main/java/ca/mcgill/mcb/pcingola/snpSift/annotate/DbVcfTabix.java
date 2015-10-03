package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcfIndex {

	public static final int MIN_SEEK = 100;

	public DbVcfTabix(String dbFileName) {
		super(dbFileName);
	}

	@Override
	protected boolean dbSeekEntry(VcfEntry vcfEntry) {
		// Using original chromosome name
		return vcfDbFile.seek(vcfEntry.getChromosomeNameOri(), vcfEntry.getStart());
	}

	@Override
	public List<VcfEntry> find(Variant variant) {
		throw new RuntimeException("Unimplemented");
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		if (debug) Gpr.debug("Open database file:" + dbFileName);

		// Open database
		vcfDbFile = new VcfFileIterator(dbFileName);
		vcfDbFile.setDebug(debug);
		if (!vcfDbFile.isTabix()) throw new RuntimeException("Could not open VCF file as TABIX-indexed: '" + dbFileName + "'");
		nextVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
		addNextVcfDb();
	}

	@Override
	protected boolean shouldSeek(VcfEntry vcfEntry) {
		return ((vcfEntry.getEnd() - nextVcfDb.getStart()) > MIN_SEEK);
	}

}
