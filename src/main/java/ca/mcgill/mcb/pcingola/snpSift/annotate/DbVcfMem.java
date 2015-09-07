package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Use an uncompressed sorted VCF file as a database for annotations
 *
 * Note: Assumes that the VCF database file is sorted.
 *       Each VCF entry should be sorted according to position.
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2).
 *       But entries for the same chromosome should be together.
 *
 * Note: Old VCF specifications did not require VCF files to be sorted.
 *
 * @author pcingola
 */
public class DbVcfMem extends DbVcf {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	protected FileIndexChrPos indexDb;

	public DbVcfMem(String dbFileName) {
		super(dbFileName);
	}

	@Override
	public List<VcfEntry> find(Variant variant) {
		// Nothing to do, the whole database is already loaded into memory
		return null;
	}

	/**
	 * Read all DB entries up to 'vcf'
	 */
	@Override
	public List<VcfEntry> find(VcfEntry ve) {
		// Nothing to do, the whole database is already loaded into memory
		return null;
	}

	/**
	 * Load the whole VCF 'database' file into memory
	 */
	void loadDatabase() {
		checkRepeat = false; // We don't need this checking

		if (verbose) Timer.showStdErr("Loading database: '" + dbFileName + "'");
		VcfFileIterator dbFile = new VcfFileIterator(dbFileName);
		dbFile.setDebug(debug);

		int count = 1;
		for (VcfEntry vcfDbEntry : dbFile) {
			add(vcfDbEntry);

			count++;
			if (verbose) {
				if (count % SHOW_LINES == 0) System.err.print("\n" + count + "\t.");
				else if (count % SHOW == 0) System.err.print('.');
			}
		}

		// Show time
		if (verbose) {
			System.err.println("");
			Timer.showStdErr("Done. Database size: " + size());
		}
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		loadDatabase();
	}

}
