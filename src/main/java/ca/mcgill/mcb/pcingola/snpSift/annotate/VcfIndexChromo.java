package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;

/**
 * Represents a set of VCF entries stored in an (uncompressed) file
 * All entries belong to the same chromosome
 *
 * @author pcingola
 */
public class VcfIndexChromo {

	public static final int INITIAL_CAPACITY = 1024;

	Genome genome;
	String chromosome;
	int start[]; // Intervals start position
	int end[]; // Intervals end position
	long filePosStart[]; // Position within a file
	long filePosEnd; // Last position within a file
	int size; // Arrays size

	public VcfIndexChromo(Genome genome) {
		this.genome = genome;
		size = 0;
	}

	public VcfIndexChromo(Genome genome, String chromosome) {
		this.genome = genome;
		this.chromosome = chromosome;
		start = new int[INITIAL_CAPACITY];
		end = new int[INITIAL_CAPACITY];
		filePosStart = new long[INITIAL_CAPACITY];
		size = 0;
	}

	/**
	 * Add [start, end] => filePos
	 */
	public void add(int start, int end, long fileIdx) {
		// Do we need to resize
		if (size >= capacity()) grow();

		this.start[size] = start;
		this.end[size] = end;
		filePosStart[size] = fileIdx;
		size++;
	}

	int capacity() {
		if (start == null) return 0;
		return start.length;
	}

	/**
	 * File size between startIdx and endIdx inclusive
	 */
	public long fileSize(int startIdx, int endIdx) {
		long startFilePos = (startIdx >= 0 ? startFilePos = getFilePosStart(startIdx) : 0);

		if (endIdx >= (size - 1)) endIdx = size - 2;
		long endFilePos = getFilePosStart(endIdx + 1);

		return endFilePos - startFilePos;
	}

	public String getChromosome() {
		return chromosome;
	}

	public int getEnd(int idx) {
		return end[idx];
	}

	public long getFilePosEnd(int idx) {
		if ((idx + 1) >= size) return filePosEnd;
		return filePosStart[idx + 1];
	}

	public long getFilePosStart(int idx) {
		return filePosStart[idx];
	}

	public int getStart(int idx) {
		return start[idx];
	}

	void grow() {
		// overflow-conscious code
		int oldCapacity = capacity();
		int newCapacity = oldCapacity + (oldCapacity >> 1);

		start = Arrays.copyOf(start, newCapacity);
		end = Arrays.copyOf(end, newCapacity);
		filePosStart = Arrays.copyOf(filePosStart, newCapacity);
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
	 * Read data from input stream
	 * @return true on success
	 */
	public boolean load(DataInputStream in) {
		try {
			size = in.readInt();
			if (size < 0) return false;

			chromosome = in.readUTF();

			// Allocate arrays
			start = new int[size];
			end = new int[size];
			filePosStart = new long[size];

			// Read array data
			for (int i = 0; i < size; i++) {
				start[i] = in.readInt();
				end[i] = in.readInt();
				filePosStart[i] = in.readLong();
			}
		} catch (EOFException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	/**
	 * Create a marker form data at position 'idx'
	 */
	public Marker marker(int idx) {
		Chromosome chr = genome.getOrCreateChromosome(chromosome);
		return new MarkerFile(chr, start[idx], end[idx], filePosStart[idx]);
	}

	/**
	 * Save to output stream
	 */
	public void save(DataOutputStream out) {
		try {
			out.writeInt(size);
			out.writeUTF(chromosome);

			// Dump array data
			for (int i = 0; i < size; i++) {
				out.writeInt(start[i]);
				out.writeInt(end[i]);
				out.writeLong(filePosStart[i]);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setFilePosEnd(long filePosEnd) {
		this.filePosEnd = filePosEnd;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chromosome: " + chromosome + ", size: " + size + ", capacity: " + capacity() + "\n");

		for (int i = 0; i < size; i++)
			sb.append("\t" + i + "\t[ " + start[i] + ", " + end[i] + " ]\t" + filePosStart[i] + "\n");

		return sb.toString();
	}

}
