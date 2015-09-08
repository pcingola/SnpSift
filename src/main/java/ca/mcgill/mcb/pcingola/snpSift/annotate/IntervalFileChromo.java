package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.Arrays;

import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;

/**
 * Represents a set of intervals stored in an (uncompressed) file
 * All intervals belong to the same chromosome
 *
 * E.g.: VCF, GTF, GFF
 *
 * @author pcingola
 */
public class IntervalFileChromo {

	public static final int MIN_CAPACITY = 1024;

	Genome genome;
	String chromosome;
	int start[]; // Intervals start position
	int end[]; // Intervals end position
	long fileIdx[]; // Position within a file
	int size; // Arrays size

	public IntervalFileChromo(Genome genome, String chromosome) {
		this.genome = genome;
		this.chromosome = chromosome;
		start = new int[MIN_CAPACITY];
		end = new int[MIN_CAPACITY];
		fileIdx = new long[MIN_CAPACITY];
		size = 0;
	}

	/**
	 * Add [start, end] => filePos
	 */
	public void add(int start, int end, long fileIdx) {
		// Do we need to resize
		if (size >= capacity()) {
			grow(MIN_CAPACITY);
		}

		this.start[size] = start;
		this.end[size] = end;
		this.fileIdx[size] = fileIdx;
		size++;
	}

	public int capacity() {
		if (start == null) return 0;
		return start.length;
	}

	public String getChromosome() {
		return chromosome;
	}

	public int getEnd(int idx) {
		return end[idx];
	}

	public long getFileIdx(int idx) {
		return fileIdx[idx];
	}

	public int getStart(int idx) {
		return start[idx];
	}

	void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = capacity();
		int newCapacity = oldCapacity + (oldCapacity >> 1);

		start = Arrays.copyOf(start, newCapacity);
		end = Arrays.copyOf(end, newCapacity);
		fileIdx = Arrays.copyOf(fileIdx, newCapacity);
	}

	/**
	 * Does interval at position 'idx' intersect positions 'pos'?
	 */
	public boolean intersects(int idx, int pos) {
		return (start[idx] <= pos) && (pos <= end[idx]);
	}

	/**
	 * Does interval at position 'idx' intersect 'marker'?
	 */
	public boolean intersects(int idx, Marker marker) {
		if (!chromosome.equals(marker.getChromosomeName())) return false;
		int istart = Math.max(start[idx], marker.getStart());
		int iend = Math.min(end[idx], marker.getEnd());
		return (istart <= iend);
	}

	/**
	 * Create a marker form data at position 'idx'
	 */
	public Marker marker(int idx) {
		Chromosome chr = genome.getOrCreateChromosome(chromosome);
		return new MarkerFile(chr, start[idx], end[idx], fileIdx[idx]);
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chromosome: " + chromosome + ", size: " + size + ", capacity: " + capacity() + "\n");

		for (int i = 0; i < size; i++)
			sb.append("\t" + i + "\t[ " + start[i] + ", " + end[i] + " ]\t" + fileIdx[i] + "\n");

		return sb.toString();
	}

}
