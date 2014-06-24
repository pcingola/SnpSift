package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;

import ca.mcgill.mcb.pcingola.fileIterator.GenericMarkerFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.GenericMarker;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.snpEffect.SnpEffectPredictor;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;

/**
 * Annotate a VCF file with ID from another VCF file (database)
 * 
 * @author pablocingolani
 */
public class SnpSiftCmdJoin extends SnpSift {

	boolean showEmpty, showAll, showClosest;
	String file[];
	int inOffset[], colChr[], colStart[], colEnd[];

	Genome genome = new Genome("genome");
	ArrayList<GenericMarker> list[];
	SnpEffectPredictor snpEffectPredictor = new SnpEffectPredictor(genome);

	@SuppressWarnings("unchecked")
	public SnpSiftCmdJoin(String[] args) {
		super(args, "join");

		list = new ArrayList[2];
		for (int i = 0; i < 2; i++)
			list[i] = new ArrayList<GenericMarker>();
	}

	/** 		 
	 * Create SnpEffect predictor and add all peaks
	 * 
	 */
	void build(ArrayList<GenericMarker> list) {
		if (verbose) Timer.showStdErr("Creating interval forest");
		for (Marker m : list)
			snpEffectPredictor.add(m);

		// Build interval forest
		if (verbose) Timer.showStdErr("Building interval forest");
		snpEffectPredictor.setUseChromosomes(false); // We don't want to intersect with chromosome markers
		snpEffectPredictor.buildForest();
	}

	/**
	 * Find closest interval
	 * @param m
	 * @return
	 */
	GenericMarker closest(GenericMarker marker) {
		String chr = marker.getChromosomeName();
		Chromosome chromo = genome.getChromosome(chr);

		int minAddSize = 100;
		int maxAddSize = chromo.size() / 2; // Don't go over this limit

		// Use bigger and bigger intervals, until we find something
		for (int add = minAddSize; add < maxAddSize; add += add) {
			// Resize interval from both ends
			GenericMarker m = new GenericMarker(marker.getParent(), marker.getStart() - add, marker.getEnd() + add, marker.getId());

			// Any intersection?
			Markers markers = snpEffectPredictor.query(m);
			if (markers.size() > 0) {
				int min = Integer.MAX_VALUE;
				GenericMarker gmMin = null;

				// Fin min distance
				for (Marker mm : markers) {
					int dist = marker.distance(mm);
					if (min > dist) {
						min = dist;
						gmMin = (GenericMarker) mm;
					}
				}

				return gmMin;
			}
		}

		return null; // Nothing found
	}

	@Override
	public void init() {
		file = new String[2];
		inOffset = new int[2];
		colChr = new int[2];
		colStart = new int[2];
		colEnd = new int[2];
		verbose = false;
		showEmpty = false;
		showAll = false;
		showClosest = false;
	}

	/**
	 * Parse command line arguments
	 * @param args
	 */
	@Override
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			// Argument starts with '-'?
			if (args[i].startsWith("-")) {

				if (args[i].equals("-if1")) {
					if ((i + 1) < args.length) inOffset[0] = Gpr.parseIntSafe(args[++i]);
				} else if (args[i].equals("-if2")) {
					if ((i + 1) < args.length) inOffset[1] = Gpr.parseIntSafe(args[++i]);
				} else if (args[i].equals("-cols1")) {
					if ((i + 1) < args.length) parseCols(0, args[++i]);
				} else if (args[i].equals("-cols2")) {
					if ((i + 1) < args.length) parseCols(1, args[++i]);
				} else if (args[i].equals("-v")) {
					verbose = true;
				} else if (args[i].equals("-empty")) {
					showEmpty = true;
				} else if (args[i].equals("-all")) {
					showAll = true;
				} else if (args[i].equals("-closest")) {
					showClosest = true;
				}

			} else {
				if (file[0] == null) file[0] = args[i];
				else if (file[1] == null) file[1] = args[i];
			}
		}

		// Sanity check
		if (file[0] == null) usage("Missing file1");
		if (file[1] == null) usage("Missing file2");
	}

	/**
	 * Parse column definition string
	 * @param num
	 * @param columnDef
	 */
	void parseCols(int num, String columnDef) {
		columnDef = columnDef.toLowerCase();
		if (columnDef.equals("bed")) {
			colChr[num] = 0;
			colStart[num] = 1;
			colEnd[num] = 2;
			inOffset[num] = 0; // Zero-based
		} else if (columnDef.equals("vcf")) {
			colChr[num] = 0;
			colStart[num] = 1;
			colEnd[num] = 1;
			inOffset[num] = 1; // One-based
		} else {
			String fields[] = columnDef.split(",");
			if (fields.length >= 2) {
				colChr[num] = Gpr.parseIntSafe(fields[0]) - 1;
				colStart[num] = Gpr.parseIntSafe(fields[1]) - 1;

				if (fields.length >= 2) colEnd[num] = Gpr.parseIntSafe(fields[2]) - 1;
				else colEnd[num] = colStart[num];

			} else usage("Error parsing oclumn definition: '" + columnDef + "'");
		}
	}

	/** 
	 * Read both files
	 * 
	 */
	void readFiles() {
		for (int i = 0; i < 2; i++) {
			if (verbose) Timer.showStdErr("Reading file '" + file[i] + "'");
			list[i] = readMarkers(i);
			updateChromos(list[i]);
			if (verbose) Timer.showStdErr("Done, " + list[i].size());
		}
	}

	/**
	 * Read a peaks file from an XLS file (MACS 'negative peaks' format).
	 * @param fileName
	 * @param add
	 * @return
	 */
	ArrayList<GenericMarker> readMarkers(int num) {
		ArrayList<GenericMarker> list = new ArrayList<GenericMarker>();
		GenericMarkerFileIterator gmfi = new GenericMarkerFileIterator(file[num], colChr[num], colStart[num], colEnd[num], inOffset[num]);
		for (GenericMarker gm : gmfi)
			list.add(gm);
		return list;
	}

	/**
	 * Run algorithm
	 */
	@Override
	public void run() {
		readFiles();
		build(list[1]);

		// Find which markers intersect
		int countIntersect = 0;
		for (GenericMarker m : list[0]) {
			Markers markers = snpEffectPredictor.query(m);
			if (markers.size() > 0) {
				countIntersect++;

				int max = 0;
				GenericMarker gmMax = null;
				for (Marker mm : markers) {
					GenericMarker gm = (GenericMarker) mm;
					if (showAll) System.out.println("INTERSECT\t" + m.getLine() + "\t" + gm.getLine());
					else {
						int size = m.intersectSize(mm);
						if (max < size) {
							max = size;
							gmMax = gm;
						}
					}
				}

				// Show only one interval? Use the one with max overlap
				if (!showAll) System.out.println("INTERSECT\t" + m.getLine() + "\t" + gmMax.getLine());
			} else if (showClosest) {
				// Show closest interval
				GenericMarker gmClosest = closest(m);
				if (gmClosest != null) System.out.println("CLOSEST\t" + m.getLine() + "\t" + gmClosest.getLine());
				else if (showEmpty) System.out.println("NONE\t" + m.getLine());
			} else if (showEmpty) System.out.println("NONE\t" + m.getLine());

		}

		if (verbose) {
			double perc = 100.0 * countIntersect / list[0].size();
			Timer.showStdErr("Done.\n\tTotal intervals: " + list[0].size() + "\n\tTotal intersected: " + countIntersect + " " + String.format("(%.2f%%)", perc));
		}
	}

	/**
	 * Update missing chromosome, also update chromosome length
	 */
	void updateChromos(ArrayList<GenericMarker> list) {
		for (Marker m : list) {
			// Get chromosome
			String chr = m.getChromosomeName();
			Chromosome chromo = genome.getChromosome(chr);

			// Need to create chromosome?
			if (chromo == null) {
				chromo = new Chromosome(genome, 0, 0, chr);
				genome.add(chromo); // Add chromosome if missing
			}

			// Update size?
			if (chromo.getEnd() < m.getEnd()) chromo.setEnd(m.getEnd());
		}
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar join [options] file1 file2 \nNote: It is assumed that both files fit in memory.");
		System.err.println("Options:");
		System.err.println("\t-if1 <num>       : Offset for file1 (e.g. 1 if coordinates are one-based. Default: 1");
		System.err.println("\t-if2 <num>       : Offset for file2 (e.g. 2 if coordinates are one-based. Default: 1");
		System.err.println("\t-cols1 <colDef>  : Column definition for file 1. Format: chrCol,startCol,endCol (e.g. '1,2,3'). Shortcuts 'bed' or 'vcf' are allowed. Default: 'vcf'");
		System.err.println("\t-cols2 <colDef>  : Column definition for file 2. Format: chrCol,startCol,endCol (e.g. '1,2,3'). Shortcuts 'bed' or 'vcf' are allowed. Default: 'vcf'");
		System.err.println("\t-all             : For each interval, show all intersecting. Default: show only one (largest intersection)");
		System.err.println("\t-closest         : Show closest intervals in file2 if none intersect. Default: off");
		System.err.println("\t-empty           : Show intervals in file1 even if they do not intersect with any other interval. Default: off");
		System.exit(1);
	}
}
