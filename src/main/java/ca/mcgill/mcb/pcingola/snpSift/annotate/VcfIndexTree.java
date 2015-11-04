package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

	private static final VcfEntry EMPTY_VCFENTRY_ARRAY[] = new VcfEntry[0];

	public static final int MAX_DIFF_COLLAPSE = 2; // We only allow 2 characters difference to collapse entries ('\r\n')
	public static final int MIN_LINES = 4; // This number cannot be less then 3 (see comment in code below)
	public static final int MIN_FILE_SIZE = 4 * 1024; // Minimum file size to index
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

		// TODO: Too few intervals? Just add them to the intersect

		// Split indexes into left, right and intersecting
		TIntArrayList left = new TIntArrayList();
		TIntArrayList right = new TIntArrayList();
		TIntArrayList intersecting = new TIntArrayList();

		for (int i = 0; i < idxs.size(); i++) {
			int j = idxs.get(i);

			if (vcfIndexChromo.getEnd(j) < center) left.add(j);
			else if (vcfIndexChromo.getStart(j) > center) right.add(j);
			else intersecting.add(j);
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
	public Markers query(Interval marker) {
		Markers results = new Markers();
		query(marker, 0, results);
		return results;
	}

	/**
	 * Query index to find all VCF entries intersecting 'marker', starting from node 'idx'
	 * Store VCF entries in 'results'
	 */
	protected void query(Interval marker, int idx, Markers results) {
		// Negative index? Nothing to do
		if (idx < 0) return;

		// Check all intervals intersecting
		queryIntersects(marker, idx, results);

		// Recurse left or right
		int midPos = mid[idx];
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
	protected void queryIntersects(Interval marker, int idx, Markers results) {
		if (intersectFilePosStart[idx] == null) return;
		if (debug) Gpr.debug("intersects( " + marker + ", " + idx + " )");

		int len = intersectFilePosStart[idx].length;
		for (int i = 0; i < len; i++) {
			if (debug) Gpr.debug("\tintersect[" + idx + "][" + i + "]:\t[" + intersectFilePosStart[idx][i] + " , " + intersectFilePosEnd[idx][i] + " ]");
			long startPos = intersectFilePosStart[idx][i];
			long endPos = intersectFilePosEnd[idx][i];

			try {
				List<VcfEntry> vcfEntries = intersect[idx];

				// No cache? Read from file
				if (vcfEntries == null) {
					vcf.seek(startPos);

					// Read entries from file
					vcfEntries = new ArrayList<VcfEntry>();
					for (VcfEntry ve : vcf) {
						vcfEntries.add(ve);
						if (vcf.getFilePointer() >= endPos) break; // Finished reading?
					}
				}

				// Find matching entries
				for (VcfEntry ve : vcfEntries) {
					// If any variant within the vcfEntry intersects the query
					// marker, we store this VCF entry as a result
					for (Variant var : ve.variants()) {
						if (var.intersects(marker)) {
							if (debug) Gpr.debug("\tVcfEntry [" + vcf.getFilePointer() + "]: " + ve);
							results.add(ve);
							break; // Store this entry only once
						}
					}
				}

				// Should we cache?
				if (intersect[idx] == null && !isLeaf(idx)) {
					intersect[idx] = vcfEntries;
					Gpr.debug("Caching: " + vcfEntries.size());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
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
		// TODO:  Try to collapse intersecting intervals
		if (intersecting.isEmpty()) {
			intersectFilePosStart[idx] = intersectFilePosEnd[idx] = null;
		} else {
			intersectFilePosStart[idx] = new long[intersecting.size()];
			intersectFilePosEnd[idx] = new long[intersecting.size()];
			for (int i = 0; i < intersecting.size(); i++) {
				int j = intersecting.get(i);
				intersectFilePosStart[idx][i] = vcfIndexChromo.getFilePosStart(j);
				intersectFilePosEnd[idx][i] = vcfIndexChromo.getFilePosEnd(j);
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
