package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Represents a set of intervals stored in an (uncompressed) file
 *
 * E.g.: VCF, GTF, GFF
 *
 * @author pcingola
 */
public class IntervalFile {

	public static final String INDEX_EXT = "sidx";
	public static final int POS_OFFSET = 1; // VCF files are one-based

	Map<String, IntervalFileChromo> intervalFileByChromo;
	String fileName;

	public IntervalFile(String fileName) {
		this.fileName = fileName;
		intervalFileByChromo = new HashMap<>();
	}

	/**
	 * Add an interval parse from 'line'
	 */
	public VcfEntry add(VcfFileIterator vcf, String line, int lineNum, long filePos) {
		if (line.startsWith("#")) return null; // Nothing to do

		// Parse VCF entry
		VcfEntry ve = new VcfEntry(vcf, line, lineNum, true);

		// Add to intervals
		getOrCreate(ve.getChromosomeName()).add(ve.getStart(), ve.getEnd(), filePos);

		return ve;
	}

	/**
	 * Create index
	 */
	void createIndex() {
		RandomAccessFile file = null;

		try {
			// Open file
			File f = new File(fileName);
			file = new RandomAccessFile(f, "r");

			// Prepare file iterator and read header (just in case)
			VcfFileIterator vcf = new VcfFileIterator(fileName);
			vcf.readHeader();

			// Read the whole file
			String line;
			long pos = file.getFilePointer();
			for (int lineNum = 0; (line = file.readLine()) != null; lineNum++) {
				add(vcf, line, lineNum, pos);
				pos = file.getFilePointer();
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found '" + fileName + "'");
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (file != null) try {
				file.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public IntervalFileChromo get(String chromosome) {
		return intervalFileByChromo.get(Chromosome.simpleName(chromosome));
	}

	/**
	 * Get IntervalFileChromo by chromosome name.
	 * Create a new one if it doesn't exists
	 */
	public IntervalFileChromo getOrCreate(String chromosome) {
		chromosome = Chromosome.simpleName(chromosome);
		IntervalFileChromo ifc = intervalFileByChromo.get(chromosome);

		if (ifc == null) {
			ifc = new IntervalFileChromo(chromosome);
			intervalFileByChromo.put(chromosome, ifc);
		}

		return ifc;
	}

	/**
	 * Load or create index
	 */
	public void index() {
		// Load a pre-existing index file?
		String indexFile = fileName + "." + INDEX_EXT;
		if (Gpr.exists(indexFile)) {
			loadIndex(indexFile);
			return;
		}

		// Create index
		createIndex();
	}

	protected void loadIndex(String indexFile) {

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("File '" + fileName + "' :\n");

		ArrayList<String> chrs = new ArrayList<>();
		chrs.addAll(intervalFileByChromo.keySet());
		Collections.sort(chrs);

		for (String chr : chrs) {
			IntervalFileChromo ifc = get(chr);
			sb.append("\tChoromsome:" + chr + ", size: " + ifc.size() + ", capacity: " + ifc.capacity() + "\n");
		}

		return sb.toString();
	}

	public String toStringAll() {
		StringBuilder sb = new StringBuilder();

		ArrayList<String> chrs = new ArrayList<>();
		chrs.addAll(intervalFileByChromo.keySet());
		Collections.sort(chrs);

		for (String chr : chrs)
			sb.append(get(chr) + "\n");

		return sb.toString();
	}

}
