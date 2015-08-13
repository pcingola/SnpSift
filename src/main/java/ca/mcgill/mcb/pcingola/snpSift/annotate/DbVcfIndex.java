package ca.mcgill.mcb.pcingola.snpSift.annotate;

import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use an index to search entries in a VCF file
 *
 * @author pcingola
 */
public abstract class DbVcfIndex extends DbVcf {

	public DbVcfIndex(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Seek to a new position. Make sure we advance at least one entry
	 */
	protected abstract boolean dbSeek(VcfEntry vcfEntry);

	/**
	 * Open VCF database annotation file
	 */
	@Override
	public abstract void open();

	/**
	 * Find a matching db entry for a vcf entry
	 */
	@Override
	public void readDb(VcfEntry veInput) {
		//---
		// Find db entry
		//---
		if (debug) Gpr.debug("ReadDb: Looking for " //
				+ veInput.getChromosomeName() + ":" + veInput.getStart() //
				+ "\tLatest DB: " + (latestVcfDb == null ? "null" : latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart()) //
				+ "\tNext DB: " + (nextVcfDb == null ? "null" : nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart()));

		// Read database
		while (true) {
			//---
			// Do we have a 'nextVcfDb' entry to process?
			//---
			if (nextVcfDb == null) {
				// Null entry, try getting next entry
				nextVcfDb = vcfDbFile.next(); // Read next DB entry

				// Still null? May be we run out of DB entries
				if (nextVcfDb == null) {
					if (latestVcfDb == null) return; // Something is really wrong here (e.g. empty database)

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

			if (debug) Gpr.debug("ReadDb: Looking for " //
					+ veInput.getChromosomeName() + ":" + veInput.getStart() //
					+ "\tLatest DB: " + (latestVcfDb == null ? "null" : latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart()) //
					+ "\tNext DB: " + (nextVcfDb == null ? "null" : nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart()));

			//---
			// Find entry in DB
			// Note that at this point nextVcfDb must be non-null
			//---
			if (nextVcfDb.isSameChromo(veInput)) {
				// Same chromosome

				// Same position? => Found
				if (veInput.getEnd() >= nextVcfDb.getStart()) {
					// Found db entry! Break loop and proceed with annotations
					if (debug) Gpr.debug("Found Db Entry:" + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart());
					addNextVcfDb();
					nextVcfDb = vcfDbFile.next();
				} else if (veInput.getEnd() < nextVcfDb.getStart()) {
					// Same chromosome, but DB is positioned after input => No (more) db entries found
					if (debug) Gpr.debug("No (more) db entries found:\t" + veInput.getChromosomeName() + ":" + veInput.getStart());
					return;
				} else if (shouldSeek(veInput)) {
					// Is it far enough? Don't iterate, seek
					clear();
					if (!dbSeek(veInput)) return;
					addNextVcfDb();
				} else {
					// Just read next entry to get closer
					clear();
					nextVcfDb = vcfDbFile.next();
					addNextVcfDb();
				}
			} else {
				// May be we finished reading DB for this chromosome? => Nothing else to do
				if (latestVcfDb != null && latestVcfDb.isSameChromo(veInput)) return;

				// Input is on different chromosome? => seek to chromosome
				if (debug) Gpr.debug("Chromosome seek:\t" + nextVcfDb.getChromosomeName() + ":" + nextVcfDb.getStart() + "\t->\t" + veInput.getChromosomeName() + ":" + veInput.getStart());

				// Seek to new position. If chromosome not found, return null
				clear();
				if (!dbSeek(veInput)) return;
				addNextVcfDb();
			}
		}
	}

	/**
	 * Should we perform a dbSeek?
	 */
	protected abstract boolean shouldSeek(VcfEntry vcfEntry);

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Size: " + size() + "\n");
		sb.append("\tLatest VCF entry : " + latestVcfDb + "\n");
		sb.append("\tNext VCF entry   : " + nextVcfDb + "\n");
		sb.append(super.toString());
		return sb.toString();
	}

}
