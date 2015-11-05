package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Interval;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.interval.tree.Itree;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import gnu.trove.list.array.TIntArrayList;

/**
 * Interval tree structure for an 'VcfIndexChromo'
 * The whole tree is stored in a single class as a set of arrays.
 * Nodes are referenced by index in the array
 *
 * @author pcingola
 */
public class VcfIndexTree implements Itree {

	public static final int MAX_DIFF_COLLAPSE = 2; // We only allow 2 characters difference to collapse entries ('\r\n')
	public static final int COLLAPSE_MAX_NUM_ENTRIES = 4; // This number cannot be less then 3 (see comment in code below)
	public static final int COLLAPSE_MAX_BLOCK_SIZE = 4 * 1024; // Minimum file size to index
	public static final int INITIAL_CAPACITY = 1024; // Initial capacity for arrays

	boolean debug;
	boolean verbose;
	boolean inSync;

	String chromosome;
	VcfFileIterator vcf;
	VcfIndexDataChromo vcfIndexChromo;
	int left[]; // Left subtree (index within this IntervalTreeFileChromo)
	int right[]; // Right subtree (index within this IntervalTreeFileChromo)
	int mid[]; // Middle position (genomic coordinate)
	long intersectFilePosStart[][]; // Intervals (file position start) intersecting 'mid-point'
	long intersectFilePosEnd[][]; // Intervals (file position end) intersecting 'mid-point'
	int size; // Arrays size (index of first unused element in the arrays)
	List<VcfEntry> intersect[]; // Cache entries for non-leaf nodes
	int cachedLeafNodeIdx = -1;
	List<VcfEntry> cachedLeafNode = null;

	public VcfIndexTree() {
		this(null, null);
	}

	@SuppressWarnings("unchecked")
	public VcfIndexTree(VcfFileIterator vcf, VcfIndexDataChromo vcfIndexChromo) {
		this.vcfIndexChromo = vcfIndexChromo;
		chromosome = (vcfIndexChromo != null ? vcfIndexChromo.getChromosome() : null);

		left = new int[INITIAL_CAPACITY];
		right = new int[INITIAL_CAPACITY];
		mid = new int[INITIAL_CAPACITY];
		intersectFilePosStart = new long[INITIAL_CAPACITY][];
		intersectFilePosEnd = new long[INITIAL_CAPACITY][];
		intersect = new List[INITIAL_CAPACITY];
		size = 0;
	}

	@Override
	public void add(Marker interval) {
		throw new RuntimeException("Unimplemented! This IntervalTree is backed by a VcfIndexDataChromo class instead of a set of markers");
	}

	@Override
	public void add(Markers markers) {
		throw new RuntimeException("Unimplemented! This IntervalTree is backed by a VcfIndexDataChromo class instead of a set of markers");
	}

	@Override
	public void build() {
		TIntArrayList idxs = new TIntArrayList(vcfIndexChromo.size());
		for (int i = 0; i < vcfIndexChromo.size(); i++)
			idxs.add(i);

		build(idxs);
		inSync = true;
	}

	/**
	 * Index entries in VcfIndexDataChromo
	 * @return Index of added item (-1 if no item was added)
	 */
	int build(TIntArrayList idxs) {
		if (idxs.isEmpty()) return -1;

		// Find middle position
		// Note:If we mode the 'mid' point by one base, the probability of intersecting
		// an interval is significantly reduced (most entries are SNPs). This reduces
		// the index size, the number of 'file.seek()' operations and speeds up the index.
		int center = mean(idxs);
		int firstStart = vcfIndexChromo.getStart(idxs.get(0));
		if (center > firstStart) center--;

		// Index of entry to be added
		int idx = nextEntry();

		// Split indexes into left, right and intersecting
		TIntArrayList left = new TIntArrayList();
		TIntArrayList right = new TIntArrayList();
		TIntArrayList intersecting = new TIntArrayList();

		// Try to collapse consecutive entries if there are only a few (i.e. less 
		// than COLLAPSE_MAX_NUM_ENTRIES) or the block size is small (less 
		// than COLLAPSE_MAX_BLOCK_SIZE bytes)
		if (consecutiveFileBlock(idxs) && // 
				((idxs.size() < COLLAPSE_MAX_NUM_ENTRIES) || (consecutiveFileBlockSize(idxs) < COLLAPSE_MAX_BLOCK_SIZE)) //
		) {
			// Too few intervals forming a consecutive block?
			// Just add them to the intersect
			for (int i = 0; i < idxs.size(); i++) {
				int j = idxs.get(i);
				intersecting.add(j);
			}
		} else {
			// Add indexes into left, right and intersecting
			for (int i = 0; i < idxs.size(); i++) {
				int j = idxs.get(i);

				if (vcfIndexChromo.getEnd(j) < center) left.add(j);
				else if (vcfIndexChromo.getStart(j) > center) right.add(j);
				else intersecting.add(j);
			}
		}

		// Recurse
		int leftIdx = build(left);
		int rightIdx = build(right);

		// Create this entry
		set(idx, leftIdx, rightIdx, center, intersecting);

		return idx;
	}

	int capacity() {
		if (left == null) return 0;
		return left.length;
	}

	long consecutiveFileBlockSize(TIntArrayList idxs) {
		long max = -1;
		long min = Long.MAX_VALUE;

		for (int i = 0; i < idxs.size(); i++) {
			int idx = idxs.get(i);
			min = Math.min(min, vcfIndexChromo.getFilePosStart(idx));
			max = Math.max(max, vcfIndexChromo.getFilePosEnd(idx));
		}

		return max - min;
	}

	/**
	 * Are entries indexed by 'idxs' consecutive position in the file?
	 */
	boolean consecutiveFileBlock(TIntArrayList idxs) {
		long end = -1;

		for (int i = 0; i < idxs.size(); i++) {
			int idx = idxs.get(i);
			if (end < 0) end = vcfIndexChromo.getFilePosEnd(idx);

			long start = vcfIndexChromo.getFilePosStart(idx);
			if ((start - end) > 0) return false;

			// Prepare for next iteration
			end = vcfIndexChromo.getFilePosEnd(idx);
		}

		return true;
	}

	long consecutiveFileBlockMax(TIntArrayList idxs) {
		long max = -1;

		for (int i = 0; i < idxs.size(); i++) {
			int idx = idxs.get(i);
			max = Math.max(max, vcfIndexChromo.getFilePosEnd(idx));
		}

		return max;
	}

	long consecutiveFileBlockMin(TIntArrayList idxs) {
		long min = Long.MAX_VALUE;

		for (int i = 0; i < idxs.size(); i++) {
			int idx = idxs.get(i);
			min = Math.min(min, vcfIndexChromo.getFilePosStart(idx));
		}

		return min;
	}

	public String getChromosome() {
		return chromosome;
	}

	@Override
	public Markers getIntervals() {
		throw new RuntimeException("Unimplemented! This IntervalTree is backed by a VcfIndexDataChromo class instead of a set of markers");
	}

	void grow() {
		int oldCapacity = capacity();
		int newCapacity = oldCapacity + (oldCapacity >> 1);

		left = Arrays.copyOf(left, newCapacity);
		right = Arrays.copyOf(right, newCapacity);
		mid = Arrays.copyOf(mid, newCapacity);
		intersectFilePosStart = Arrays.copyOf(intersectFilePosStart, newCapacity);
		intersectFilePosEnd = Arrays.copyOf(intersectFilePosEnd, newCapacity);
		intersect = Arrays.copyOf(intersect, newCapacity);
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

	@Override
	public boolean isEmpty() {
		return vcfIndexChromo.size() <= 0;
	}

	@Override
	public boolean isInSync() {
		return inSync;
	}

	/**
	 * Is node 'idx' a leaf node?
	 */
	boolean isLeaf(int idx) {
		return (left[idx] == -1) && (right[idx] == -1);
	}

	@Override
	public Iterator<Marker> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Read data from input stream
	 * @return true on success
	 */
	@SuppressWarnings("unchecked")
	public boolean load(DataInputStream in) {
		try {
			chromosome = in.readUTF();
			size = in.readInt();
			if (verbose) Timer.showStdErr("\tReading index for chromosome '" + chromosome + "' (index size: " + size + " )");

			// Sanity cgheck
			if (size < 0) return false;

			// Allocate arrays
			left = new int[size];
			right = new int[size];
			mid = new int[size];

			intersectFilePosStart = new long[size][];
			intersectFilePosEnd = new long[size][];
			intersect = new List[size];

			// Read array data
			for (int i = 0; i < size; i++) {
				left[i] = in.readInt();
				right[i] = in.readInt();
				mid[i] = in.readInt();

				int len = in.readInt();
				if (len > 0) {
					// Allocate
					intersectFilePosStart[i] = new long[len];
					intersectFilePosEnd[i] = new long[len];

					// Read values
					for (int j = 0; j < len; j++) {
						intersectFilePosStart[i][j] = in.readLong();
						intersectFilePosEnd[i][j] = in.readLong();
					}
				} else {
					intersectFilePosStart[i] = intersectFilePosEnd[i] = null;
				}
			}
		} catch (EOFException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public void load(String fileName, Genome genome) {
		throw new RuntimeException("Unimplemented! This IntervalTree is loaded as part of a whole index");
	}

	/**
	 * Mean coordinates from entries indexed by 'idxs'
	 */
	int mean(TIntArrayList idxs) {
		if (idxs.isEmpty()) return 0;

		TIntArrayList coordinates = new TIntArrayList(2 * idxs.size());
		for (int i = 0; i < idxs.size(); i++) {
			int idx = idxs.get(i);

			coordinates.add(vcfIndexChromo.getStart(idx));
			coordinates.add(vcfIndexChromo.getEnd(idx));
		}
		coordinates.sort();

		return coordinates.get(coordinates.size() / 2);
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
	@Override
	public Markers query(Interval queryMarker) {
		Markers results = new Markers();
		if (debug) Gpr.debug("Query: " + queryMarker.getChromosomeName() + ":" + queryMarker.getStart() + "-" + queryMarker.getEnd() + "\t" + queryMarker);
		query(queryMarker, 0, results);
		return results;
	}

	/**
	 * Query index to find all VCF entries intersecting 'marker', starting from node 'idx'
	 * Store VCF entries in 'results'
	 */
	protected void query(Interval queryMarker, int idx, Markers results) {
		// Negative index? Nothing to do
		if (idx < 0) return;

		if (debug) Gpr.debug("Node: " + toString(idx) + (results.isEmpty() ? "" : "\n\tResults: " + results));

		// Check all intervals intersecting
		queryIntersects(queryMarker, idx, results);

		// Recurse left or right
		int midPos = mid[idx];
		if ((queryMarker.getEnd() < midPos) && (left[idx] >= 0)) {
			query(queryMarker, left[idx], results);
		}

		if ((midPos < queryMarker.getStart()) && (right[idx] >= 0)) {
			query(queryMarker, right[idx], results);
		}
	}

	List<VcfEntry> readEntries(int idx) {
		// Cached?
		if (cachedLeafNodeIdx == idx) return cachedLeafNode;
		List<VcfEntry> vcfEntries = intersect[idx];
		if (vcfEntries != null) return vcfEntries;

		try {
			// There might be several non-contiguous file regions
			int len = intersectFilePosStart[idx].length;

			// Read each file region
			vcfEntries = new ArrayList<VcfEntry>();
			Set<VcfEntry> added = new HashSet<>();
			for (int i = 0; i < len; i++) {
				if (debug) Gpr.debug("\tintersect[" + idx + "][" + i + "]:\t[" + intersectFilePosStart[idx][i] + " , " + intersectFilePosEnd[idx][i] + " ]");

				long startPos = intersectFilePosStart[idx][i];
				long endPos = intersectFilePosEnd[idx][i];

				// No cache? Read from file
				vcf.seek(startPos);

				// Read entries from file
				for (VcfEntry ve : vcf) {
					if (added.add(ve)) { // Make sure we add entries only once
						vcfEntries.add(ve);
						if (debug) Gpr.debug("\tParsing VcfEntry [" + vcf.getFilePointer() + "]: " + ve);
					}

					// Finished reading?
					if (vcf.getFilePointer() >= endPos) break;
				}
			}

			// Cache data
			if (isLeaf(idx)) {
				cachedLeafNodeIdx = idx;
				cachedLeafNode = vcfEntries;
			} else if (intersect[idx] == null) {
				// Cache non-leaf nodes, which have very few intersect entries
				intersect[idx] = vcfEntries;
			}

			return vcfEntries;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Query VCF entries intersecting 'marker' at node 'idx'
	 */
	protected void queryIntersects(Interval queryMarker, int idx, Markers results) {
		if (intersectFilePosStart[idx] == null) return;
		if (debug) Gpr.debug("queryIntersects\tidx: " + idx);

		// Read entries from disk
		List<VcfEntry> vcfEntries = readEntries(idx);

		// Find matching entries
		for (VcfEntry ve : vcfEntries) {
			// If any variant within the vcfEntry intersects the query
			// marker, we store this VCF entry as a result
			for (Variant var : ve.variants()) {
				if (var.intersects(queryMarker)) {
					if (debug) Gpr.debug("\tAdding matchin result: " + ve);
					results.add(ve);
					break; // Store this entry only once
				}
			}
		}
	}

	/**
	 * Save to output stream
	 */
	public void save(DataOutputStream out) {
		try {
			out.writeUTF(chromosome);
			out.writeInt(size);

			// Dump array data
			for (int i = 0; i < size; i++) {
				out.writeInt(left[i]);
				out.writeInt(right[i]);
				out.writeInt(mid[i]);

				// Intersect data

				int len = intersectFilePosStart[i] != null ? intersectFilePosStart[i].length : 0;
				out.writeInt(len);
				for (int j = 0; j < len; j++) {
					out.writeLong(intersectFilePosStart[i][j]);
					out.writeLong(intersectFilePosEnd[i][j]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set all parameters in one 'row'
	 *
	 * WARNIGN: If we don't do it this way, we get strange errors
	 * due to array resizing (array appears to be filled with
	 * zeros after being set)
	 */
	void set(int idx, int leftIdx, int rightIdx, int midPos, TIntArrayList intersecting) {

		// Assign values
		left[idx] = leftIdx;
		right[idx] = rightIdx;
		mid[idx] = midPos;

		// Assign intersecting values
		if (intersecting.isEmpty()) {
			intersectFilePosStart[idx] = intersectFilePosEnd[idx] = null;
		} else {
			// First try to collapse intersecting all intervals as a single file block
			if (intersecting.size() > 0 && consecutiveFileBlock(intersecting)) {
				// OK, we can collapse all entries into a file block
				intersectFilePosStart[idx] = new long[1];
				intersectFilePosEnd[idx] = new long[1];

				intersectFilePosStart[idx][0] = consecutiveFileBlockMin(intersecting);
				intersectFilePosEnd[idx][0] = consecutiveFileBlockMax(intersecting);
			} else {
				// Add entries individually
				intersectFilePosStart[idx] = new long[intersecting.size()];
				intersectFilePosEnd[idx] = new long[intersecting.size()];
				for (int i = 0; i < intersecting.size(); i++) {
					int j = intersecting.get(i);
					intersectFilePosStart[idx][i] = vcfIndexChromo.getFilePosStart(j);
					intersectFilePosEnd[idx][i] = vcfIndexChromo.getFilePosEnd(j);
				}
			}
		}
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

	@Override
	public int size() {
		return size;
	}

	@Override
	public Markers stab(int point) {
		throw new RuntimeException("Unimplemented! ");
	}

	@Override
	public String toString() {
		return "Chromosome: " + chromosome //
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

		if (intersectFilePosStart[idx] != null) {
			sb.append("\tintersect: (" + intersectFilePosStart[idx].length + "): ");
			for (int i = 0; i < intersectFilePosStart[idx].length; i++)
				sb.append("\t[" + intersectFilePosStart[idx][i] + ", " + intersectFilePosEnd[idx][i] + "] size " + (intersectFilePosEnd[idx][i] - intersectFilePosStart[idx][i] + 1));
		}

		if (intersect[idx] != null) sb.append("\tCache: " + intersect[idx].size());
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
