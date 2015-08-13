package ca.mcgill.mcb.pcingola.snpSift.annotate;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
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

	/**
	 * Seek to a new position. Make sure we advance at least one entry
	 */
	@Override
	protected boolean dbSeek(VcfEntry vcfEntry) {
		// Current coordinates
		String chr = "";
		int pos = -1;

		if (nextVcfDb != null) {
			pos = nextVcfDb.getStart();
			chr = nextVcfDb.getChromosomeName();
			if (debug) Gpr.debug("Position seek:\t" + chr + ":" + pos + "\t->\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
		}

		// Seek
		if (!vcfDbFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart())) return false;

		// Make sure we actually advance at least one entry
		do {
			nextVcfDb = vcfDbFile.next();
			if (nextVcfDb == null) return false; // Not found

			if (debug) Gpr.debug("After seek: " + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart() + "\t" + chr + ":" + pos);
		} while (nextVcfDb.getChromosomeName().equals(chr) && nextVcfDb.getStart() <= pos);

		return true;
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
