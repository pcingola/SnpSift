package ca.mcgill.mcb.pcingola.snpSift.gwasCatalog;

import java.util.List;

import ca.mcgill.mcb.pcingola.collections.MultivalueHashMap;
import ca.mcgill.mcb.pcingola.interval.Chromosome;

/**
 * GWAS catalog table.
 * It is small enough to fit in memory
 * 
 * Refernces:
 * 		http://www.genome.gov/gwastudies/
 * 
 * @author pablocingolani
 *
 */
public class GwasCatalog {

	MultivalueHashMap<String, GwasCatalogEntry> gwasEntryByChrPos; // Entry by "chr:pos" key
	MultivalueHashMap<String, GwasCatalogEntry> gwasEntryByRs; // Entry by RS number

	public GwasCatalog(String fileName) {
		gwasEntryByChrPos = new MultivalueHashMap<String, GwasCatalogEntry>();
		gwasEntryByRs = new MultivalueHashMap<String, GwasCatalogEntry>();
		load(fileName);
	}

	/**
	 * Get all entries at a given chromosome & position
	 * Note: Position is zero based.
	 * 
	 * @param chrName
	 * @param pos
	 * @return
	 */
	public List<GwasCatalogEntry> get(String chrName, int pos) {
		return gwasEntryByChrPos.get(key(chrName, pos));
	}

	/**
	 * Get an entry based on 'RS' number
	 * @param rs
	 * @return
	 */
	public List<GwasCatalogEntry> getByRs(String rs) {
		return gwasEntryByRs.get(rs);
	}

	/**
	 * Create a 'key' for the hash
	 * @param chrName
	 * @param pos
	 * @return
	 */
	String key(String chrName, int pos) {
		return Chromosome.simpleName(chrName) + ":" + pos;
	}

	/**
	 * Load data from file
	 * @param fileName
	 */
	protected void load(String fileName) {
		GwasCatalogFileIterator gfile = new GwasCatalogFileIterator(fileName);
		for (GwasCatalogEntry ge : gfile) {
			// Add entries by chr:pos
			String key = key(ge.chrId, ge.chrPos - 1); // Note: Positions in GWAS file are one-based (we use zero-based coordinates everywhere).
			gwasEntryByChrPos.add(key, ge);

			// Add entries by RS
			for (String rs : ge.snps.split(","))
				if (!rs.equals("NR")) gwasEntryByRs.add(rs, ge);
		}
	}
}
