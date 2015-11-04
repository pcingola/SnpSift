package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * An index for a VCF file
 *
 * @author pcingola
 */
public class VcfIndex {

	public static int INDEX_FORMAT_VERSION = 1;
	public static int SHOW_EVERY = 1000000;

	public static final String INDEX_EXT = "sidx";

	boolean verbose;
	boolean debug;
	String fileName;
	Map<String, VcfIndexDataChromo> vcfIndexByChromo;
	Map<String, VcfIndexTree> forest; // A hash of trees
	Genome genome;
	VcfFileIterator vcf;

	public VcfIndex(String fileName) {
		this.fileName = fileName;
		vcfIndexByChromo = new HashMap<>();
	}

	/**
	 * Add an interval parse from 'line'
	 */
	public void add(VcfEntry ve, long filePos) {
		VcfIndexDataChromo vidc = getOrCreate(ve.getChromosomeName());

		List<Variant> vars = ve.variants();

		if (vars.size() == 1) {
			// This is the most common case
			Variant var = vars.get(0);
			vidc.add(var.getStart(), var.getEnd(), filePos); // Only add if not already added
			return;
		}

		// Several variants: Add only add distinct intervals
		Set<String> added = new HashSet<>();
		for (Variant var : vars) {
			String key = var.getStart() + "\t" + var.getEnd();
			if (added.add(key)) {
				vidc.add(var.getStart(), var.getEnd(), filePos); // Only add if not already added
			}
		}
	}

	/**
	 * Sorted list of chromosome names
	 */
	List<String> chromosomes() {
		ArrayList<String> chrs = new ArrayList<>();
		chrs.addAll(vcfIndexByChromo.keySet());
		Collections.sort(chrs);
		return chrs;
	}

	/**
	 * Close file
	 */
	public void close() {
		if (vcf != null) vcf.close();
		vcf = null;
	}

	/**
	 * Create interval forest
	 */
	void createIntervalForest() {
		if (verbose) Timer.showStdErr("Creating interval forest:");

		forest = new HashMap<>();

		// For each 'IntervalFileChromo'...
		for (String chr : chromosomes()) {
			VcfIndexDataChromo vic = getVcfIndexChromo(chr);
			VcfIndexTree vcfTree = new VcfIndexTree(vcf, vic);
			vcfTree.build();
			if (verbose) System.err.println("\t" + vcfTree);

			forest.put(chr, vcfTree);
		}

		if (verbose) Timer.showStdErr("Creating interval forest: Done");
	}

	public Genome getGenome() {
		return genome;
	}

	/**
	 * Get IntervalFileChromo by chromosome name.
	 * Create a new one if it doesn't exists
	 */
	public VcfIndexDataChromo getOrCreate(String chromosome) {
		chromosome = Chromosome.simpleName(chromosome);
		VcfIndexDataChromo ifc = vcfIndexByChromo.get(chromosome);

		if (ifc == null) {
			ifc = new VcfIndexDataChromo(genome, chromosome);
			vcfIndexByChromo.put(chromosome, ifc);
		}

		return ifc;
	}

	public VcfIndexTree getTree(String chromosome) {
		return forest.get(Chromosome.simpleName(chromosome));
	}

	public VcfIndexDataChromo getVcfIndexChromo(String chromosome) {
		return vcfIndexByChromo.get(Chromosome.simpleName(chromosome));
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
			return;
		}

		// Create index
		if (verbose) Timer.showStdErr("Creating index");
		loadIntervals();
		createIntervalForest();
		save(indexFile);
	}

	/**
	 * Load index form a file
	 */
	protected void loadIndex(String indexFile) {
		if (verbose) Timer.showStdErr("Loading index file '" + indexFile + "'");

		DataInputStream in = null;
		forest = new HashMap<>();

		try {
			in = new DataInputStream(new GZIPInputStream(new FileInputStream(indexFile)));
			if (genome == null) genome = new Genome("genome");

			// Read data for each chromosome
			VcfIndexTree vcfTree = new VcfIndexTree();
			vcfTree.setVerbose(verbose);
			vcfTree.setDebug(debug);
			while (vcfTree.load(in)) {
				forest.put(vcfTree.getChromosome(), vcfTree);

				vcfTree = new VcfIndexTree();
				vcfTree.setVerbose(verbose);
				vcfTree.setDebug(debug);
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
	 * Parse input VCF file and load intervals
	 */
	void loadIntervals() {
		if (verbose) Timer.showStdErr("Loading intervals from file '" + fileName + "'");

		try {
			open(); // Open VCF file

			// Read the whole file
			boolean title = true;
			long pos = vcf.getFilePointer();
			long fileLen = (new File(fileName)).length();
			Timer timer = new Timer();
			timer.start();
			vcf.setErrorIfUnsorted(true); // Make sure all lines are sorted
			for (VcfEntry ve : vcf) {
				add(ve, pos);

				// Show progress
				if (verbose && vcf.getLineNum() % SHOW_EVERY == 0) {
					double perc = ((double) pos) / fileLen;

					long elapsed = timer.elapsed();
					double remain = elapsed / perc * (1.0 - perc);
					long remainMs = (long) remain;

					if (title) {
						System.err.println(String.format("\t\t%8s\t%10s\t%3s\t%10s\t%10s" //
								, "LineNum" //
								, "chr:pos" //
								, "%" //
								, "Elapsed" //
								, "Remaining" //
						));
						title = false;
					}

					System.err.println(String.format("\t\t%8d\t%10s\t%.1f%%\t%10s\t%10s" //
							, vcf.getLineNum() //
							, ve.getChromosomeName() + ":" + (ve.getStart() + 1) //
							, 100.0 * perc //
							, Timer.toString(elapsed, false) //
							, Timer.toString(remainMs, false) //
					));
				}

				// Prepare for next iteration
				pos = vcf.getFilePointer();
				getVcfIndexChromo(ve.getChromosomeName()).setFilePosEnd(pos);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close();
		}

		if (verbose) Timer.showStdErr("Loading intervals: Done\n" + this);
	}

	/**
	 * Open file
	 */
	public void open() {
		try {
			// Open file as random access
			SeekableBufferedReader seekableReader = new SeekableBufferedReader(fileName);
			vcf = new VcfFileIterator(seekableReader); // Prepare file iterator and genome
			vcf.readHeader();
			genome = vcf.getGenome();
		} catch (FileNotFoundException e) {
			System.err.println("File not found '" + fileName + "'");
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Query interval forest
	 */
	public Markers query(Marker marker) {
		String chr = marker.getChromosomeName();
		VcfIndexTree tree = forest.get(chr);
		if (tree == null) return new Markers();
		tree.setVcf(vcf);
		return tree.query(marker);
	}

	/**
	 * Read a VcfEntry at position 'fileIdx'
	 */
	public VcfEntry read(long fileIdx) {
		try {
			vcf.seek(fileIdx);
			return vcf.next();
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
	public void save(String indexFile) {
		if (verbose) Timer.showStdErr("Saving index to file '" + indexFile + "'");

		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));

			// Save each chromosome index
			for (String chr : chromosomes())
				getTree(chr).save(out);

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

		if (forest != null) {
			for (VcfIndexTree it : forest.values())
				it.setDebug(debug);
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;

		if (forest != null) {
			for (VcfIndexTree it : forest.values())
				it.setVerbose(verbose);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("File '" + fileName + "' :\n");

		for (String chr : chromosomes()) {
			VcfIndexDataChromo ifc = getVcfIndexChromo(chr);
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
			sb.append(getVcfIndexChromo(chr) + "\n");

		for (String chr : chromosomes())
			sb.append(getTree(chr).toStringAll() + "\n");

		return sb.toString();
	}

}
