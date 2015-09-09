package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * Interval tree structure for an 'IntervalFileChromo'
 * The whole tree is stored in a single class as a set of arrays.
 * Nodes are referenced by index in the array
 *
 * @author pcingola
 */
public class IntervalTreeFileChromo {

	public static final int MIN_LINES = 4; // This number cannot be less then 3 (see comment in code below)
	public static final int MIN_FILE_SIZE = 4 * 1024; // Minimum file size to index

	public static final int INITIAL_CAPACITY = 1024; // Initial capacity for arrays

	boolean debug;
	boolean verbose;

	IntervalFileChromo intervalFileChromo;
	int left[]; // Left subtree (index within this IntervalTreeFileChromo)
	int right[]; // Right subtree (index within this IntervalTreeFileChromo)
	int mid[]; // Middle position (genomic coordinate)
	int intersect[][]; // Intervals (indexes intervalFileChromo) intersecting 'mid-point'
	int size; // Arrays size (index of first unused element in the arrays)

	public IntervalTreeFileChromo(IntervalFileChromo intervalFileChromo) {
		this.intervalFileChromo = intervalFileChromo;

		left = new int[INITIAL_CAPACITY];
		right = new int[INITIAL_CAPACITY];
		mid = new int[INITIAL_CAPACITY];
		intersect = new int[INITIAL_CAPACITY][];
		size = 0;
	}

	int capacity() {
		if (left == null) return 0;
		return left.length;
	}

	void grow() {
		int oldCapacity = capacity();
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		Gpr.debug("Grow:" + oldCapacity + "\t" + newCapacity);

		String before = toStringAll();

		left = Arrays.copyOf(left, newCapacity);
		right = Arrays.copyOf(right, newCapacity);
		mid = Arrays.copyOf(mid, newCapacity);
		intersect = Arrays.copyOf(intersect, newCapacity);

		String after = toStringAll();
		if (!before.equals(after)) throw new RuntimeException("Before and after do not match!");
	}

	public void index() {
		index(0, intervalFileChromo.size() - 1);
	}

	/**
	 * Index intervals from 'start' to 'end' (index to intervalFileChromo)
	 * @return Index of added item (-1 if no item was added)
	 */
	int index(int startIdx, int endIdx) {
		if (debug) Gpr.debug("index( " + startIdx + ", " + endIdx + " )");
		if (startIdx >= endIdx) return -1;

		// Find middle position
		int midIdx = (startIdx + endIdx) / 2;
		int midPos = intervalFileChromo.getStart(midIdx);

		//---
		// Add entry
		//---
		int idx = nextEntry();

		// Too few intervals? Just add them to the intersect array
		// and finish recursion here.
		long size = intervalFileChromo.fileSize(startIdx, endIdx);
		int count = endIdx - startIdx + 1;
		if ((count <= MIN_LINES) || (size <= MIN_FILE_SIZE)) {
			// When we have 3 or less entries, we cannot partition them in 2 groups
			// of 2 entries for a balanced recursion. Plus is not efficient to
			// keep adding nodes if there is so little to divide (a simple linear
			// search can do as well)
			int inter[] = new int[count];
			for (int i = startIdx, j = 0; i <= endIdx; i++, j++)
				inter[j] = i;

			set(idx, -1, -1, midPos, inter);
			return idx;
		}

		// Recurse
		int intersects[] = intersect(startIdx, endIdx, midPos);
		int leftIdx = index(startIdx, midIdx);
		int rightIdx = index(midIdx + 1, endIdx);
		set(idx, leftIdx, rightIdx, midPos, intersects);

		if ((left[idx] == idx) || (right[idx] == idx)) // Sanity check
			throw new RuntimeException("Infinite recursion (index: " + idx + "):\n\t" + toString(idx));

		return idx;
	}

	/**
	 * Find all interval indexes from intervals within [startIdx, endIdx] that intersect 'pos'
	 */
	int[] intersect(int startIdx, int endIdx, int pos) {
		List<Integer> list = null;

		// Find all intersecting intervals
		for (int idx = startIdx; idx <= endIdx; idx++) {
			if (intervalFileChromo.intersects(idx, pos)) {
				// Add this position
				if (list == null) list = new ArrayList<>();
				list.add(idx);
			}
		}

		// No results
		if (list == null) return null;

		// Create an array
		int i = 0;
		int ints[] = new int[list.size()];
		for (int idx : list)
			ints[i++] = idx;

		return ints;
	}

	/**
	 * Get next index for entry and make sure there
	 * is enough capacity to store it
	 */
	int nextEntry() {
		if (size >= capacity()) grow();
		return size++;
	}

	/**
	 * Query all intervals intersecting 'marker'.
	 * @return A list of indexes including all markers that intersect 'marker'
	 */
	public Markers query(Marker marker) {
		Markers results = new Markers();
		//		if (debug) {
		//			Gpr.debug(toStringAll());
		//			Gpr.toFile(Gpr.HOME + "/snpEff/" + intervalFileChromo.getChromosome() + ".txt", toStringAll());
		//		}

		query(marker, 0, results);
		return results;
	}

	public void query(Marker marker, int idx, Markers results) {
		if (debug) Gpr.debug("query( " + marker.toStr() + ", " + idx + " )");

		// Negative index? Nothing to do
		if (idx < 0) return;

		// Check all intervals intersecting
		if (intersect[idx] != null) {
			for (int i : intersect[idx]) {
				if (intervalFileChromo.intersects(i, marker)) { // Does it intersect 'marker'?
					results.add(intervalFileChromo.marker(i)); // Add it to results
				}
			}
		}

		// Recurse left or right
		int midPos = mid[idx];
		if (debug) Gpr.debug("midPos:" + midPos);
		if (marker.getEnd() < midPos) {
			query(marker, left[idx], results);
		}

		if (midPos < marker.getStart()) {
			query(marker, right[idx], results);
		}
	}

	/**
	 * Set all parameters in one 'row'
	 *
	 * WARNIGN: If we don't do it this way, we get strange errors
	 * due to array resizing (array appears to be filled with
	 * zeros after being set)
	 */
	void set(int idx, int leftIdx, int rightIdx, int midPos, int intersects[]) {
		left[idx] = leftIdx;
		right[idx] = rightIdx;
		mid[idx] = midPos;
		intersect[idx] = intersects;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return "Chromosome: " + intervalFileChromo.getChromosome() //
				+ ", size: " + size //
		//+ ", capacity: " + capacity() //
		;
	}

	public String toString(int i) {
		return i //
				+ "\tleftIdx: " + left[i] //
				+ "\trightIdx: " + right[i] //
				+ "\tmidPos: " + mid[i] //
				+ "\tintersectIdx (" + (intersect[i] != null ? intersect[i].length : 0) + "): " + Gpr.toString(intersect[i]) //
				;
	}

	public String toStringAll() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString() + "\n");

		for (int i = 0; i < size; i++)
			sb.append("\t" + toString(i) + "\n");

		return sb.toString();
	}

}
