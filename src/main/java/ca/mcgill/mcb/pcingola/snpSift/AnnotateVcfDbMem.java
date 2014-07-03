package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate using a VCF "database"
 *
 * Note: Reads and loads the whole VCF file into memory
 *
 * @author pcingola
 */
public class AnnotateVcfDbMem extends AnnotateVcfDb {

	protected AnnotateVcfDbMem(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Load the whole VCF 'database' file into memory
	 */
	void loadDatabase() {
		if (verbose) Timer.showStdErr("Loading database: '" + dbFileName + "'");
		VcfFileIterator dbFile = new VcfFileIterator(dbFileName);

		int count = 1;
		for (VcfEntry vcfDbEntry : dbFile) {
			addDbCurrent(vcfDbEntry);

			count++;
			if (verbose) {
				if (count % SHOW_LINES == 0) System.err.print("\n" + count + "\t.");
				else if (count % SHOW == 0) System.err.print('.');
			}
		}

		// Show time
		if (verbose) {
			System.err.println("");
			Timer.showStdErr("Done. Database size: " + dbCurrentId.size());
		}
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() throws IOException {
		loadDatabase();
	}

	/**
	 * Read all DB entries up to 'vcf'
	 */
	@Override
	protected void readDb(VcfEntry ve) {
		// Nothing to do, the whole database is already loaded into memory
	}
}
