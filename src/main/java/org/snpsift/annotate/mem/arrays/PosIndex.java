package org.snpsift.annotate.mem.arrays;

import java.util.Arrays;

/**
 * An index by possition
 */
public class PosIndex implements java.io.Serializable {
	private static final long serialVersionUID = 2024073105L;

	int[] positions;	// Chromosome positions of each entry
	int size = 0;	// Maximum position

	public PosIndex(int numEntries) {
		positions = new int[numEntries];
	}

	public int capacity() {
		return positions.length;
	}

	/**
	 * Check that all positions are in non-decreasing order.
	 * This is necesary because we use binary search to find positions
	 */
	public void check() {
		for (int i = 1; i < size; i++)
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
		return Arrays.binarySearch(positions, 0, size, pos);
	}

	/**
	 * Find the index of a position using a (slow) linear search
	 * Used for testing
	 * @param pos: A zero-based position
	 * @return index of the position or negative number if not found
	 */
	public int indexOfSlow(int pos) {
		for (int i = 0; i < size; i++)
			if (positions[i] == pos) return i;
		return -1;
	}

	/**
	 * Set position to entry number 'i'
	 */
	public void set(int i, int pos) {
		positions[i] = pos;
		size = Math.max(size, i + 1);
	}

	/**
	 * Number of entries in use
	 */
	public int size() {
		return size;
	}

	/**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return positions.length * 4;
    }

}


