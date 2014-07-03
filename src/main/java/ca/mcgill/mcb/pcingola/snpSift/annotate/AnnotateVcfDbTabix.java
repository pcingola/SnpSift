package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate using a tabix indexed VCF "database"
 *
 * @author pcingola
 *
 */
public class AnnotateVcfDbTabix extends AnnotateVcfDb {

	public static final int MIN_JUMP = 100;

	public AnnotateVcfDbTabix(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() throws IOException {
		// Re-open VCF db file
		vcfDbFile = new VcfFileIterator(dbFileName);
		if (!vcfDbFile.isTabix()) throw new RuntimeException("Could not open VCF file as TABIX-indexed: '" + dbFileName + "'");
	}

	/**
	 * Find a matching db entry for a vcf entry
	 */
	@Override
	protected void readDb(VcfEntry vcfEntry) {
		//---
		// Find db entry
		//---
		if (debug) System.err.println("Looking for " + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart() + ". Current DB: " + (latestVcfDb == null ? "null" : latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart()));
		while (true) {

			if (latestVcfDb == null) {
				// Null entry, try getting next entry
				latestVcfDb = vcfDbFile.next(); // Read next DB entry

				// Still null? May be we run out of DB entries for this chromosome
				if (latestVcfDb == null) {
					// Is vcfEntry still in 'latestChromo'? Then we have no DbEntry, return null
					if (latestChromo.equals(vcfEntry.getChromosomeName())) {
						// End of 'latestChromo' section in database?
						clearCurrent();
						return;
					}

					// VCfEntry is in another chromosome? Jump to 'new' chromosome
					if (debug) Gpr.debug("New chromosome '" + latestChromo + "' != '" + vcfEntry.getChromosomeName() + "': We should jump");
					vcfDbFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart());
					latestVcfDb = vcfDbFile.next();

					// Still null? well it looks like we don't have any dbEntry for this chromosome
					if (latestVcfDb == null) {
						latestChromo = vcfEntry.getChromosomeName(); // Make sure we don't try jumping again
						clearCurrent();
						return;
					}
				}
			}

			if (debug) Gpr.debug("Current Db Entry:" + latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart() + "\tLooking for: " + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());

			// Find entry
			if (latestVcfDb.getChromosomeName().equals(vcfEntry.getChromosomeName())) {
				// Same chromosome

				// Same position? => Found
				if (vcfEntry.getStart() == latestVcfDb.getStart()) {
					// Found db entry! Break loop and proceed with annotations
					if (debug) Gpr.debug("Found Db Entry:" + latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart());
					addDbCurrent(latestVcfDb);
					latestVcfDb = vcfDbFile.next();
				} else if (vcfEntry.getStart() < latestVcfDb.getStart()) {
					// Same chromosome, but positioned after => No db entry found
					if (debug) Gpr.debug("No db entry found:\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
					return;
				} else if ((vcfEntry.getStart() - latestVcfDb.getStart()) > MIN_JUMP) {
					// Is it far enough? Don't iterate, jump
					if (debug) Gpr.debug("Position jump:\t" + latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart() + "\t->\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
					clearCurrent();
					vcfDbFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart());
					latestVcfDb = vcfDbFile.next();
				} else {
					// Just read next entry to get closer
					latestVcfDb = vcfDbFile.next();
					clearCurrent();
				}
			} else if (!latestVcfDb.getChromosomeName().equals(vcfEntry.getChromosomeName())) {
				// Different chromosome? => Jump to chromosome
				if (debug) Gpr.debug("Chromosome jump:\t" + latestVcfDb.getChromosomeName() + ":" + latestVcfDb.getStart() + "\t->\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());

				// Jump to new position. If chromosome not found, return null
				if (!vcfDbFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart())) return;

				clearCurrent();
				latestVcfDb = vcfDbFile.next();
			}

			if (latestVcfDb != null) latestChromo = latestVcfDb.getChromosomeName();
		}
	}
}
