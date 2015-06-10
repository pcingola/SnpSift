package ca.mcgill.mcb.pcingola.snpSift.annotate;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcf {

	public static final int MIN_SEEK = 100;

	public DbVcfTabix(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Seek to a new position. Make sure we advance at least one entry
	 */
	boolean dbSeek(VcfEntry vcfEntry) {
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
		// Open database
		vcfDbFile = new VcfFileIterator(dbFileName);
		vcfDbFile.setDebug(debug);
		if (!vcfDbFile.isTabix()) throw new RuntimeException("Could not open VCF file as TABIX-indexed: '" + dbFileName + "'");
		nextVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
		addNextVcfDb();
	}

	/**
	 * Find a matching db entry for a vcf entry
	 */
	@Override
	public void readDb(VcfEntry veInput) {
		//---
		// Find db entry
		//---
		if (debug) System.err.println("ReadDb: Looking for " //
				+ veInput.getChromosomeName() + ":" + veInput.getStart() //
				+ ". Current DB: " + (latestVcfDb == null ? "null" : latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart()));

		while (true) {
			if (nextVcfDb == null) {
				// Null entry, try getting next entry
				nextVcfDb = vcfDbFile.next(); // Read next DB entry

				// Still null? May be we run out of DB entries for this chromosome
				if (nextVcfDb == null) {
					// Is vcfEntry still in 'latestChromo'? Then we have no DbEntry, return null
					if (latestVcfDb.isSameChromo(veInput)) {
						// End of 'latestChromo' section in database
						if (debug) Gpr.debug("Reading: DB finished reading chromosome " + veInput.getChromosomeName() + "\n" + this);
						return;
					}

					// VCfEntry is in another chromosome? seek to 'new' chromosome
					if (!dbSeek(veInput)) return;

					// Still null? well it looks like we don't have any dbEntry for this chromosome
					if (nextVcfDb == null) {
						if (debug) Gpr.debug("Reading: No more DB entries for chromosome " + veInput.getChromosomeName() + "\n" + this);
						return;
					}
				}
			}

			if (debug) Gpr.debug("ReadDb: Current Db Entry:" + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart() + "\tLooking for: " + veInput.getChromosomeName() + ":" + veInput.getStart());

			// Find entry. Note that at this point nextVcfDb must be non-null
			if (nextVcfDb.isSameChromo(veInput)) {
				// Same chromosome

				// Same position? => Found
				if (veInput.getStart() == nextVcfDb.getStart()) {
					// Found db entry! Break loop and proceed with annotations
					if (debug) Gpr.debug("Found Db Entry:" + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart());
					add(nextVcfDb);
					nextVcfDb = vcfDbFile.next();
				} else if (veInput.getEnd() < nextVcfDb.getStart()) {
					// Same chromosome, but DB is positioned after input => No (more) db entries found
					if (debug) Gpr.debug("No db entry found:\t" + veInput.getChromosomeName() + ":" + veInput.getStart());
					return;
				} else if ((veInput.getEnd() - nextVcfDb.getStart()) > MIN_SEEK) {
					// Is it far enough? Don't iterate, seek
					clear();
					if (!dbSeek(veInput)) return;
				} else {
					// Just read next entry to get closer
					nextVcfDb = vcfDbFile.next();
					clear();
				}
			} else {
				// Different chromosome? => seek to chromosome
				if (debug) Gpr.debug("Chromosome seek:\t" + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart() + "\t->\t" + veInput.getChromosomeName() + ":" + veInput.getStart());

				clear();

				// Seek to new position. If chromosome not found, return null
				if (!dbSeek(veInput)) return;
			}

			// Have we managed to get an entry
			if (nextVcfDb != null) addNextVcfDb();
		}
	}
}
