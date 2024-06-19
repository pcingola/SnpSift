package org.snpsift;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;


/**
 * A wrapper for a data column of primitive type T
 */
interface DataColumn<T> {

	public String getName();	// Column name
	public T get(int i);	// Get value at index i
	public void set(int i, T value);	// Set value at index i
}

class IntColumn implements DataColumn<Integer> {
	int[] data;
	String name;

	public IntColumn(String name, int[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Integer get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Integer value) {
		data[i] = value;
	}
}

class LongColumn implements DataColumn<Long> {
	long[] data;
	String name;

	public LongColumn(String name, long[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Long get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Long value) {
		data[i] = value;
	}
}

class StringColumn implements DataColumn<String> {
	String[] data;
	String name;

	public StringColumn(String name, String[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, String value) {
		data[i] = value;
	}
}

class DoubleColumn implements DataColumn<Double> {
	double[] data;
	String name;

	public DoubleColumn(String name, double[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Double get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Double value) {
		data[i] = value;
	}
}

/**
 * An index by possition
 */
class PosIndex {
	int[] positions;	// Chromosome positions of each entry


	public PosIndex(int numEntries) {
		positions = new int[numEntries];
	}

	/**
	 * Check that all positions are in non-decreasing order
	 */
	void checkPositions() {
		for (int i = 1; i < positions.length; i++)
			if (positions[i - 1] > positions[i]) throw new RuntimeException("ERROR: Positions are not sorted: " + i + "\t" + positions[i - 1] + " >= " + positions[i]);
	}

	public boolean contains(int pos) {
		return indexOf(pos) >= 0;
	}

	/**
	 * Get position at index 'i'
	 */
	public int get(int i) {
		return positions[i];
	}

	/**
	 * Find the index of a position using binary search
	 * @param pos: A zero-based position
	 * @return index of the position or negative number if not found
	 */
	public int indexOf(int pos) {
		return Arrays.binarySearch(positions, pos);
	}

	/**
	 * Find the index of a position using a (slow) linear search
	 * Used for testing
	 * @param pos: A zero-based position
	 * @return index of the position or negative number if not found
	 */
	int indexOfSlow(int pos) {
		for (int i = 0; i < positions.length; i++)
			if (positions[i] == pos) return i;
		return -1;
	}

	/**
	 * Set position to entry number 'i'
	 */
	public void set(int i, int pos) {
		positions[i] = pos;
	}

	/**
	 * Number of entries
	 */
	public int size() {
		return positions.length;
	}
}


/**
 * Data set of colmuns indexed by position
 */
class IndexedColumnDataSet {
	String alt;	// Alternative allele, i.e. one of 'A', 'C', 'G', 'T'
	PosIndex posIndex;	// Index by position
	Map<String, DataColumn<?>> columns;	// Data columns

	public IndexedColumnDataSet(int numEntries) {
		posIndex = new PosIndex(numEntries);
		columns = new HashMap<>();
	}

	/**
	 * Add a column
	 */
	public void addColumn(String name, DataColumn<?> column) {
		columns.put(name, column);
	}

	/**
	 * Get a column
	 */
	public DataColumn<?> getColumn(String name) {
		return columns.get(name);
	}

	/**
	 * Get data from a column
	 */
	public Object getData(String columnName, int pos) {
		var column = columns.get(columnName);
		if(column == null) return null;
		var idx = posIndex.indexOf(pos);
		if(idx < 0) return null;
		return column.get(idx);
	}
}

/**
 * A data set for a specific SNP 'alt'
 */
class SnpColumnDataSet extends IndexedColumnDataSet {
	String alt;	// Alternative allele, i.e. one of 'A', 'C', 'G', 'T'

	public SnpColumnDataSet(int numEntries, String alt) {
		super(numEntries);
		this.alt = alt;
	}

	String getAlt() {
		return alt;
	}
}

/***
 * This class loaads a "database" VCF file and then annotates another VCF file.
 */
public class Zzz {

	public static final int CHR1_NUM_ENTRIES = 86844566;
	public static final int SHOW_EVERY = 100 * 1000;
	public static final int SHOW_EVERY_LINE = 100 * SHOW_EVERY;
	
	String databaseFileName;	// Database file
	String[] fields;	// Fields to extract
	// PosIndex posIndex;	// Index by position
	SnpColumnDataSet snpA, snpC, snpG, snpT;	// Data sets for each SNP
	IndexedColumnDataSet ins, del;	// Data sets for insertions and deletions
	IndexedColumnDataSet other;	// Data set for other variants

	// Database counters
	int countSnps = 0; // Count SNPs
	int countSnpA = 0, countSnpC = 0, countSnpG = 0, countSnpT = 0;	// Count SNPs
	int countIns = 0, countDel = 0, countOther = 0;	// Count insertions, deletions and other variants
	int countVcfEntries = 0; // Count VCF entries

	static void showProgress(int count, VcfEntry vcfEntry) {
		if (count % SHOW_EVERY == 0) {
			if (count % SHOW_EVERY_LINE == 0) {
				System.out.println(" " + count + ", " + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart() + "\t");
			} else {
				System.out.print('.');
				System.out.flush();
			}
		}
	}

	/**
	 * Main
	 */
	public static void main(String[] args) {
		var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_small_chr1.vcf";
		// var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_test.vcf";
		var fields = new String[] { "RS" };

		// Load the database
		Zzz zzz = new Zzz(databaseFileName, fields);
		zzz.create();	// Load the "database" VCF file

		// // Test: Find 10150
		// var pos1 = 10150 - 1;
		// var idx = zzz.posIndex.indexOf(pos1);
		// System.out.println("Pos: " + pos1 + "\tIndex: " + idx);
		// idx = zzz.posIndex.indexOfSlow(pos1);
		// System.out.println("Pos: " + pos1 + "\tIndex: " + idx);

		// // Annotate another VCF file
		// // Test entry: 
		// // 		chr1    10150   rs371194064     C       T
		// var vcfInput = Gpr.HOME + "/snpEff/wgs_test/zzz.vcf";
		// System.out.println("Annotating file: " + vcfInput);
		// var found = 0;
		// var count = 0;
		// for(var vcfEntry : new VcfFileIterator(vcfInput)) {
		// 	var pos = vcfEntry.getStart();
		// 	if( zzz.posIndex.contains(pos)) {
		// 		found++;
		// 	}
		// 	count++;
		// 	showProgress(count, vcfEntry);
		// }
		// System.out.println("Done. Found: " + found + " out of " + count + " entries.");
	}

	public Zzz(String dbFile, String[] fields) {
		databaseFileName = dbFile;
		this.fields = fields;
		// posIndex = new PosIndex(numEntries);
		// rsid = new long[numEntries];
	}

	/**
	 * Count the number of variants in a VCF file
	 */
	private int countVcfVariants() {
		System.out.println("Counting number of variants in " + databaseFileName);
		var vcfFile = new VcfFileIterator(databaseFileName);
		var count = 0;
		var countVcfEntries = 0;
		var countSnps = 0;
		for (VcfEntry vcfEntry : vcfFile) {
			// Count all ALTs
			for(var variant: vcfEntry.variants()) {
				count++;
				if(variant.isSnp()) {
					countSnps++;
					switch (variant.getAlt()) {
					case "A":
						countSnpA++;
						break;
					case "C":
						countSnpC++;
						break;
					case "G":
						countSnpG++;
						break;
					case "T":
						countSnpT++;
						break;
					default:
						throw new RuntimeException("Unknown SNP: " + variant.getAlt() + "\t" + vcfEntry.toString());
					} 
				} else if(variant.isIns()) {
					countIns++;
				} else if(variant.isDel()) {
					countDel++;
				} else {
					countOther++;
				}
				showProgress(count, vcfEntry); // Show progress
			}
			countVcfEntries++;
		}
		System.out.println("\nTotal VCF entries: " + countVcfEntries
							+ "\n\tVariants: " + count
							+ "\n\tSNPs: " + countSnps + " (" + (100.0 * countSnps / count) + "%)"
							+ "\n\tSNP A: " + countSnpA
							+ "\n\tSNP C: " + countSnpC
							+ "\n\tSNP G: " + countSnpG
							+ "\n\tSNP T: " + countSnpT
							+ "\n\tInsertions: " + countIns
							+ "\n\tDeletions: " + countDel
							+ "\n\tOther: " + countOther
							);
		return count;
	}

	/**
	 * Create index from VCF "database" file
	 */
	private void create() {
		// First, count the number of entries in the VCF file
		int numEntries = countVcfVariants();
		// // Load data
		// System.out.println("Creating index from file: " + databaseFileName);
		// var vcfFile = new VcfFileIterator(databaseFileName);
		// var i = 0; // Current entry number
		// for (var vcfEntry : vcfFile) {
		// 	// Load data
		// 	var pos = vcfEntry.getStart();

		// 	// Iterate over all variants
		// 	for(var variant: vcfEntry.variants()) {
		// 		// posIndex.set(i, pos);	// Add position to index
		// 		// // Load RSID
		// 		// rsid[i] = vcfEntry.getInfo("RS").isEmpty() ? -1 : Long. parseLong(vcfEntry.getInfo("RS"));

		// 		showProgress(i, vcfEntry); // Show progress

		// 		// Next entry
		// 		i++;
		// 	}
		// }
		// System.out.println("Done: " + i + " entries.");

		// Make sure all positions are in order (i.e. suitable for binary search)
		// posIndex.checkPositions();
	}

	/**
	 * Test: Find all positions
	 *  Check that all positions extracted from zzz.positions are found
	 */
	void testFindAll() {
		// // Time the total search time
		// long startTime = System.currentTimeMillis();
		// System.out.println("Testing: Find all positions...");
		// for (int i = 0; i < entriesToCheck; i++) {
		// 	int p = posIndex.get(i);
		// 	int idx = posIndex.indexOf(p);
		// 	if (idx < 0) throw new RuntimeException("ERROR: Position not found: " + p + "\tIndex: " + idx);
		// }
		// long endTime = System.currentTimeMillis();
		// double timePerEntry = ((double) (endTime - startTime)) / entriesToCheck / 1000.0;
		// System.out.println("All positions found in " + (endTime - startTime) + "ms" + "\tTime per entry: " + timePerEntry + " second");
	}
}
