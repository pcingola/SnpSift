package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Interval tree structure for an 'VcfIndexChromo'
 * The whole tree is stored in a single class as a set of arrays.
 * Nodes are referenced by index in the array
 *
 * @author pcingola
 */
public class VcfIndexTree {

	public static final int MAX_DIFF_COLLAPSE = 2; // We only allow 2 characters difference to collapse entries ('\r\n')
	public static final int MIN_LINES = 4; // This number cannot be less then 3 (see comment in code below)
	public static final int MIN_FILE_SIZE = 4 * 1024; // Minimum file size to index

	public static final int INITIAL_CAPACITY = 1024; // Initial capacity for arrays

	boolean debug;
	boolean verbose;

	VcfFileIterator vcf;
	VcfIndexChromo vcfIndexChromo;
	int left[]; // Left subtree (index within this IntervalTreeFileChromo)
	int right[]; // Right subtree (index within this IntervalTreeFileChromo)
	int mid[]; // Middle position (genomic coordinate)
	long intersectStart[][]; // Intervals (file position start) intersecting 'mid-point'
	long intersectEnd[][]; // Intervals (file position end) intersecting 'mid-point'
	int size; // Arrays size (index of first unused element in the arrays)

	public VcfIndexTree(VcfFileIterator vcf) {
		this(vcf, null);
	}

	public VcfIndexTree(VcfFileIterator vcf, VcfIndexChromo intervalFileChromo) {
		vcfIndexChromo = intervalFileChromo;

		left = new int[INITIAL_CAPACITY];
		right = new int[INITIAL_CAPACITY];
		mid = new int[INITIAL_CAPACITY];
		intersectStart = new long[INITIAL_CAPACITY][];
		intersectEnd = new long[INITIAL_CAPACITY][];
		size = 0;
	}

	int capacity() {
		if (left == null) return 0;
		return left.length;
	}

	void grow() {
		int oldCapacity = capacity();
		int newCapacity = oldCapacity + (oldCapacity >> 1);

		left = Arrays.copyOf(left, newCapacity);
		right = Arrays.copyOf(right, newCapacity);
		mid = Arrays.copyOf(mid, newCapacity);
		intersectStart = Arrays.copyOf(intersectStart, newCapacity);
		intersectEnd = Arrays.copyOf(intersectEnd, newCapacity);
	}

	public void index() {
		index(0, vcfIndexChromo.size() - 1);
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
		int midPos = vcfIndexChromo.getStart(midIdx);

		//---
		// Add entry
		//---
		int idx = nextEntry();

		// Too few intervals? Just add them to the intersect array
		// and finish recursion here.
		long size = vcfIndexChromo.fileSize(startIdx, endIdx);
		int count = endIdx - startIdx + 1;
		if ((count <= MIN_LINES) || (size <= MIN_FILE_SIZE)) {
			// When we have 3 or less entries, we cannot partition them in 2 groups
			// of 2 entries for a balanced recursion. Plus is not efficient to
			// keep adding nodes if there is so little to divide (a simple linear
			// search can do as well)
			long interStart[] = new long[count];
			long interEnd[] = new long[count];
			for (int i = startIdx, j = 0; i <= endIdx; i++, j++) {
				interStart[j] = vcfIndexChromo.getFilePosStart(i);
				interEnd[j] = vcfIndexChromo.getFilePosEnd(i);
			}

			set(idx, -1, -1, midPos, interStart, interEnd);
			return idx;
		}

		// If we mode the 'mid' point by one base, the probability of intersecting 
		// an interval is significantly reduced (most entries are SNPs). This reduces 
		// the index size, the number of 'file.seek()' operations and speeds up the index.
		int newMidIdx;
		for (newMidIdx = midIdx; (midPos == vcfIndexChromo.getStart(newMidIdx)) && (newMidIdx > startIdx); newMidIdx--);
		midPos--;
		midIdx = newMidIdx;

		// Calculate intersecting entries
		int inter[] = intersectIndexes(startIdx, endIdx, midPos);
		long interStart[] = new long[inter.length];
		long interEnd[] = new long[inter.length];
		for (int i = 0; i < inter.length; i++) {
			int j = inter[i];
			interStart[i] = vcfIndexChromo.getFilePosStart(j);
			interEnd[i] = vcfIndexChromo.getFilePosEnd(j);
		}

		//---
		// Recurse
		//---
		int leftIdx = index(startIdx, midIdx);
		int rightIdx = index(midIdx + 1, endIdx);
		set(idx, leftIdx, rightIdx, midPos, interStart, interEnd);

		if ((left[idx] == idx) || (right[idx] == idx)) // Sanity check
			throw new RuntimeException("Infinite recursion (index: " + idx + "):\n\t" + toString(idx));

		return idx;
	}

	/**
	 * Find all interval indexes from intervals within [startIdx, endIdx] that intersect 'pos'
	 */
	int[] intersectIndexes(int startIdx, int endIdx, int pos) {
		List<Integer> list = null;

		// Find all intersecting intervals
		for (int idx = startIdx; idx <= endIdx; idx++) {
			if (vcfIndexChromo.intersects(idx, pos)) {
				// Add this position
				if (list == null) list = new ArrayList<>();
				list.add(idx);
			}
		}

		// No results
		if (list == null) return new int[0];

		// Create an array
		int i = 0;
		int ints[] = new int[list.size()];
		for (int idx : list)
			ints[i++] = idx;

		return ints;
	}

	/**
	 * Is node 'idx' a leaf node?
	 */
	boolean isLeaf(int idx) {
		return (left[idx] == -1) && (right[idx] == -1);
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
	 * Query index to find all VCF entries intersecting 'marker'
	 * Store VCF entries in 'results'
	 */
	public Markers query(Marker marker) {
		Markers results = new Markers();
		query(marker, 0, results);
		return results;
	}

	/**
	 * Query index to find all VCF entries intersecting 'marker', starting from node 'idx'
	 * Store VCF entries in 'results'
	 */
	public void query(Marker marker, int idx, Markers results) {
		if (debug) Gpr.debug("query( " + marker.toStr() + ", " + idx + " )\t" + toString(idx));

		// Negative index? Nothing to do
		if (idx < 0) return;

		// Check all intervals intersecting
		queryIntersects(marker, idx, results);

		// Recurse left or right
		int midPos = mid[idx];
		if (debug) Gpr.debug("midPos:" + midPos);
		if ((marker.getEnd() < midPos) && (left[idx] >= 0)) {
			query(marker, left[idx], results);
		}

		if ((midPos < marker.getStart()) && (right[idx] >= 0)) {
			query(marker, right[idx], results);
		}
	}

	/**
	 * Query VCF entries intersecting 'marker' at node 'idx'
	 */
	public void queryIntersects(Marker marker, int idx, Markers results) {
		if (debug) Gpr.debug("intersects( " + marker.toStr() + ", " + idx + " )");

		int len = intersectStart[idx].length;
		for (int i = 0; i < len; i++) {
			if (debug) Gpr.debug("\tintersect[" + idx + "][" + i + "]:\t[" + intersectStart[idx][i] + " , " + intersectEnd[idx][i] + " ]");
			long startPos = intersectStart[idx][i];
			long endPos = intersectEnd[idx][i];

			try {
				vcf.seek(startPos);

				for (VcfEntry ve : vcf) {
					if (ve.intersects(marker)) {
						results.add(ve);
						if (debug) Gpr.debug("\tVcfEntry [" + vcf.getFilePointer() + "]: " + ve);
					}

					if (vcf.getFilePointer() > endPos) break;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Set all parameters in one 'row'
	 *
	 * WARNIGN: If we don't do it this way, we get strange errors
	 * due to array resizing (array appears to be filled with
	 * zeros after being set)
	 */
	void set(int idx, int leftIdx, int rightIdx, int midPos, long intStart[], long intEnd[]) {
		// Try to collapse intervals
		int len = intStart.length;
		if (len > 1) {
			int j = 0;
			for (int i = 1; i < len; i++) {
				long diff = intStart[i] - intEnd[j];

				// Collapse these intervals (make 'end' position cover interval 'i' as well)
				if (diff <= MAX_DIFF_COLLAPSE) intEnd[j] = intEnd[i];
				else j++;
			}

			// Any collapsed? Pack them in a new array
			if (j < (len - 1)) {
				long iStart[] = new long[j + 1];
				long iEnd[] = new long[j + 1];
				for (int i = 0; i <= j; i++) {
					iStart[i] = intStart[i];
					iEnd[i] = intEnd[i];
				}
				intStart = iStart;
				intEnd = iEnd;
			}
		}

		// Assign values
		left[idx] = leftIdx;
		right[idx] = rightIdx;
		mid[idx] = midPos;
		intersectStart[idx] = intStart;
		intersectEnd[idx] = intEnd;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setVcf(VcfFileIterator vcf) {
		this.vcf = vcf;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return "Chromosome: " + vcfIndexChromo.getChromosome() //
				+ ", size: " + size //
				+ ", capacity: " + capacity() //
				;
	}

	public String toString(int idx) {
		if (idx < 0) return "None";

		StringBuilder sb = new StringBuilder();
		sb.append(idx //
				+ "\tleftIdx: " + left[idx] //
				+ "\trightIdx: " + right[idx] //
				+ "\tmidPos: " + mid[idx] //
		);

		if (intersectStart[idx] != null) {
			sb.append("\tintersect: (" + intersectStart[idx].length + "): ");
			for (int i = 0; i < intersectStart[idx].length; i++)
				sb.append("\t[" + intersectStart[idx][i] + ", " + intersectEnd[idx][i] + "] size " + (intersectEnd[idx][i] - intersectStart[idx][i] + 1));
		}

		return sb.toString();
	}

	public String toStringAll() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString() + "\n");

		for (int i = 0; i < size; i++)
			sb.append("\t" + toString(i) + "\n");

		return sb.toString();
	}

}
