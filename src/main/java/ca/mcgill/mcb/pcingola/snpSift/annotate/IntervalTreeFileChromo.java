package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.ArrayList;
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

		int len = intervalFileChromo.size();
		left = new int[len];
		right = new int[len];
		mid = new int[len];
		intersect = new int[len][];
		size = 0;
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
		int addIdx = size++;
		mid[addIdx] = midPos;

		// Too few intervals? Just add them to the intersect array
		// and finish recursion here.
		int count = endIdx - startIdx + 1;
		if (count <= 3) {
			// When we have 3 or less entries, we cannot partition them in 2 groups
			// of 2 entries for a balanced recursion. Plus is not efficient to
			// keep adding nodes if there is so little to divide (a simple linear
			// search can do as well)
			int inter[] = new int[count];
			for (int i = startIdx, j = 0; i <= endIdx; i++, j++)
				inter[j] = i;

			intersect[addIdx] = inter;
			left[addIdx] = right[addIdx] = -1;
			return addIdx;
		}

		// Recurse
		intersect[addIdx] = intersect(startIdx, endIdx, midPos);
		left[addIdx] = index(startIdx, midIdx);
		right[addIdx] = index(midIdx + 1, endIdx);

		return addIdx;
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
	 * Query all intervals intersecting 'marker'.
	 * @return A list of indexes including all markers that intersect 'marker'
	 */
	public Markers query(Marker marker) {
		Markers results = new Markers();
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
		StringBuilder sb = new StringBuilder();
		sb.append("Chromosome: " + intervalFileChromo.getChromosome() + ", size: " + size + "\n");

		for (int i = 0; i < size; i++)
			sb.append("\t" + i //
					+ "\tleftIdx: " + left[i] //
					+ "\trightIdx: " + right[i] //
					+ "\tmidPos: " + mid[i] //
					+ "\tintersectIdx[]: " + Gpr.toString(intersect[i]) //
					+ "\n" //
			);

		return sb.toString();
	}

}
