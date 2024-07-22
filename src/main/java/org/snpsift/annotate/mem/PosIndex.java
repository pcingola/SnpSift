package org.snpsift.annotate.mem;

import java.util.Arrays;

/**
 * An index by possition
 */
public class PosIndex implements java.io.Serializable {
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
	public int indexOfSlow(int pos) {
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


