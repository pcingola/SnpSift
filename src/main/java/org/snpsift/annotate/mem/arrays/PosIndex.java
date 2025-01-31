package org.snpsift.annotate.mem.arrays;

import java.util.Arrays;

/**
 * The PosIndex class is designed to manage and index chromosome positions efficiently converting chromosome positions to zero-based indices.
 * It stores an array of integer positions and provides methods to manipulate and query these positions.
 * 
 * Intended Use:
 * - This class is used to store chromosome positions and allows for efficient searching and validation.
 * - It ensures that positions are stored in a non-decreasing order to facilitate binary search operations.
 * 
 * How it Works:
 * - The positions are stored in an integer array, and the size of the array is determined by the number of entries specified during instantiation.
 * - The class provides methods to set positions, check if positions are sorted, and search for positions using both binary and linear search algorithms.
 * - The `check` method ensures that all positions are in non-decreasing order.
 * - The `indexOf` method uses binary search to find the index of a given position (while the `indexOfSlow` method uses a linear search, it's only used for testing and debugging).
 * - The `size` method returns the number of entries in use, and the `sizeBytes` method returns the approximate memory size of the object in bytes.
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


