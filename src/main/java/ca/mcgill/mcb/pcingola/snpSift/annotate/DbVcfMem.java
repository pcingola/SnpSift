package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.tree.IntervalTreeArray;
import ca.mcgill.mcb.pcingola.interval.tree.Itree;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VariantVcfEntry;
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

	Itree itree; // Use an interval tree as 'database'.

	public DbVcfMem(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Load the whole VCF 'database' file into memory
	 */
	void loadDatabase() {
		if (verbose) Timer.showStdErr("Loading database: '" + dbFileName + "'");
		VcfFileIterator dbFile = new VcfFileIterator(dbFileName);
		dbFile.setDebug(debug);

		int count = 0;
		itree = new IntervalTreeArray();
		for (VcfEntry vcfDbEntry : dbFile) {
			// Update header
			if (vcfHeader == null) vcfHeader = dbFile.getVcfHeader();

			// Make sure all variants from the VcfEntry are added to
			// the interval tree (e.g. multi-allelic VcfEntries)
			for (VariantVcfEntry varVe : VariantVcfEntry.factory(vcfDbEntry)) {
				itree.add(varVe);

				count++;
				if (verbose) {
					if (count % SHOW_LINES == 0) System.err.print("\n" + count + "\t.");
					else if (count % SHOW == 0) System.err.print('.');
				}
			}
		}

		// Show time
		if (verbose) {
			System.err.println("");
			Timer.showStdErr("Done. Added: " + itree.size());
		}

		// Build interval tree
		if (verbose) Timer.showStdErr("Building interval tree");
		itree.build();
		if (verbose) Timer.showStdErr("Done");
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		loadDatabase();
	}

	@Override
	public List<VariantVcfEntry> query(Marker marker) {
		Markers results = itree.query(marker);

		List<VariantVcfEntry> list = new LinkedList<VariantVcfEntry>();
		for (Marker m : results)
			list.add((VariantVcfEntry) m);

		return list;
	}

}
