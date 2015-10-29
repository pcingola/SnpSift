package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Loads a VCF file into memory.
 * 
 * 
 * WARNING: This is used only for testing and debugging purposes and should 
 * never be used in production!
 *
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

	/**
	 * Load the whole VCF 'database' file into memory
	 */
	void loadDatabase() {
		//		checkRepeat = false; // We don't need this checking
		//
		//		if (verbose) Timer.showStdErr("Loading database: '" + dbFileName + "'");
		//		VcfFileIterator dbFile = new VcfFileIterator(dbFileName);
		//		dbFile.setDebug(debug);
		//
		//		int count = 1;
		//		for (VcfEntry vcfDbEntry : dbFile) {
		//			add(vcfDbEntry);
		//
		//			count++;
		//			if (verbose) {
		//				if (count % SHOW_LINES == 0) System.err.print("\n" + count + "\t.");
		//				else if (count % SHOW == 0) System.err.print('.');
		//			}
		//		}
		//
		//		// Show time
		//		if (verbose) {
		//			System.err.println("");
		//			Timer.showStdErr("Done. Database size: " + size());
		//		}
	}

	//	/**
	//	 * Read all DB entries up to 'vcf'
	//	 */
	//	@Override
	//	public List<VcfEntry> find(VcfEntry ve) {
	//		// Nothing to do, the whole database is already loaded into memory
	//		return null;
	//	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		loadDatabase();
	}

	@Override
	public List<VcfEntry> query(Marker marker) {
		// Nothing to do, the whole database is already loaded into memory
		return null;
	}

}
