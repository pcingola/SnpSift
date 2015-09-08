package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Represents a set of intervals stored in an (uncompressed) file
 *
 * E.g.: VCF, GTF, GFF
 *
 * @author pcingola
 */
public class IntervalFile {

	public static int INDEX_FORMAT_VERSION = 1;

	public static final String INDEX_EXT = "sidx";
	public static final int POS_OFFSET = 1; // VCF files are one-based

	boolean verbose;
	boolean debug;
	String fileName;
	Map<String, IntervalFileChromo> intervalFileByChromo;
	Map<String, IntervalTreeFileChromo> intervalForest;
	Genome genome;
	RandomAccessFile file;
	VcfFileIterator vcf;

	public IntervalFile(String fileName) {
		this.fileName = fileName;
		intervalFileByChromo = new HashMap<>();
	}

	/**
	 * Add an interval parse from 'line'
	 */
	public VcfEntry add(String line, int lineNum, long filePos) {
		if (line.startsWith("#")) return null; // Nothing to do

		// Parse VCF entry
		VcfEntry ve = new VcfEntry(vcf, line, lineNum, true);

		// Add to intervals
		getOrCreate(ve.getChromosomeName()).add(ve.getStart(), ve.getEnd(), filePos);

		return ve;
	}

	List<String> chromosomes() {
		ArrayList<String> chrs = new ArrayList<>();
		chrs.addAll(intervalFileByChromo.keySet());
		Collections.sort(chrs);
		return chrs;
	}

	/**
	 * Close file
	 */
	public void close() {
		try {
			if (file != null) file.close();
			if (vcf != null) vcf.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		file = null;
	}

	/**
	 * Create interval forest
	 */
	void createIntervalForest() {
		if (verbose) Timer.showStdErr("Creating interval forest:");

		intervalForest = new HashMap<>();

		// For each 'IntervalFileChromo'...
		for (String chr : intervalFileByChromo.keySet()) {
			IntervalFileChromo ifc = get(chr);
			if (verbose) System.err.println("\t'" + ifc.getChromosome() + "'");
			IntervalTreeFileChromo itfc = new IntervalTreeFileChromo(ifc);
			itfc.index();

			intervalForest.put(chr, itfc);
		}

		if (verbose) Timer.showStdErr("Creating interval forest: Done");
	}

	public IntervalFileChromo get(String chromosome) {
		return intervalFileByChromo.get(Chromosome.simpleName(chromosome));
	}

	public Genome getGenome() {
		return genome;
	}

	/**
	 * Get IntervalFileChromo by chromosome name.
	 * Create a new one if it doesn't exists
	 */
	public IntervalFileChromo getOrCreate(String chromosome) {
		chromosome = Chromosome.simpleName(chromosome);
		IntervalFileChromo ifc = intervalFileByChromo.get(chromosome);

		if (ifc == null) {
			ifc = new IntervalFileChromo(genome, chromosome);
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
		if (verbose) Timer.showStdErr("Checking index file '" + indexFile + "'");
		if (Gpr.exists(indexFile)) {
			loadIndex(indexFile);
			createIntervalForest();
			return;
		}

		// Create index
		if (verbose) Timer.showStdErr("Creating index");
		parseFile();
		saveIndex(indexFile);
		createIntervalForest();
	}

	/**
	 * Load index form a file
	 */
	protected void loadIndex(String indexFile) {
		if (verbose) Timer.showStdErr("Loading index file '" + indexFile + "'");

		DataInputStream in = null;
		try {
			in = new DataInputStream(new GZIPInputStream(new FileInputStream(indexFile)));
			if (genome == null) genome = new Genome("genome");

			// Read data for each chromosome
			IntervalFileChromo ifc = new IntervalFileChromo(genome);
			while (ifc.load(in)) {
				intervalFileByChromo.put(ifc.getChromosome(), ifc);
				ifc = new IntervalFileChromo(genome);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Open file
	 */
	public void open() {
		file = null;

		try {
			// Open file as random access
			File f = new File(fileName);
			file = new RandomAccessFile(f, "r");

			// Prepare file iterator and read header (just in case)
			vcf = new VcfFileIterator(fileName);
			vcf.readHeader();
			genome = vcf.getGenome();

		} catch (FileNotFoundException e) {
			System.err.println("File not found '" + fileName + "'");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse: Load intervals from file
	 */
	void parseFile() {
		if (verbose) Timer.showStdErr("Loading intervals from file '" + fileName + "'");

		try {
			open(); // Open file as random access

			// Read the whole file
			String line;
			String latstChromo = "";
			int latestPos = -1;
			long pos = file.getFilePointer();
			for (int lineNum = 0; (line = file.readLine()) != null; lineNum++) {
				VcfEntry ve = add(line, lineNum, pos);

				// Sanity check: Is file sorted?
				if (ve != null) {
					if (latstChromo.equals(ve.getChromosomeName()) && (latestPos > ve.getStart())) //
						throw new RuntimeException("Input file '" + fileName + "' is not sorted! " //
								+ "Position " + (latestPos + 1) //
								+ " is before position " + (ve.getStart() + 1) //
					);

					latstChromo = ve.getChromosomeName();
					latestPos = ve.getStart();
				}

				// Prepare for next iteration
				pos = file.getFilePointer();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close();
		}

		if (verbose) Timer.showStdErr("Loading intervals: Done\n" + this);
	}

	/**
	 * Query interval forest
	 */
	public Markers query(Marker marker) {
		String chr = marker.getChromosomeName();
		IntervalTreeFileChromo tree = intervalForest.get(chr);
		if (tree == null) return new Markers();
		return tree.query(marker);
	}

	/**
	 * Read a VcfEntry at position 'fileIdx'
	 */
	public VcfEntry read(long fileIdx) {
		try {
			file.seek(fileIdx);
			String line = file.readLine();
			VcfEntry ve = new VcfEntry(vcf, line, -1, true);
			return ve;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read a VcfEntry referenced by 'markerFile'
	 */
	public VcfEntry read(MarkerFile markerFile) {
		return read(markerFile.getFileIdx());
	}

	/**
	 * Save index file
	 */
	public void saveIndex(String indexFile) {
		if (verbose) Timer.showStdErr("Saving index to file '" + indexFile + "'");

		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));

			// Save each chromosome index
			for (String chr : chromosomes())
				get(chr).save(out);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (verbose) Timer.showStdErr("Saving index: Done.");
	}

	public void setDebug(boolean debug) {
		this.debug = debug;

		if (intervalForest != null) {
			for (IntervalTreeFileChromo it : intervalForest.values())
				it.setDebug(debug);
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;

		if (intervalForest != null) {
			for (IntervalTreeFileChromo it : intervalForest.values())
				it.setVerbose(verbose);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("File '" + fileName + "' :\n");

		for (String chr : chromosomes()) {
			IntervalFileChromo ifc = get(chr);
			sb.append("\tChoromsome:" + chr + ", size: " + ifc.size() + ", capacity: " + ifc.capacity() + "\n");
		}

		return sb.toString();
	}

	/**
	 * Show all entries
	 */
	public String toStringAll() {
		StringBuilder sb = new StringBuilder();

		for (String chr : chromosomes())
			sb.append(get(chr) + "\n");

		return sb.toString();
	}

}
