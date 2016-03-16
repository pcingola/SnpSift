package org.snpsift;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.snpeff.interval.Marker;
import org.snpeff.interval.Markers;
import org.snpeff.interval.tree.IntervalForest;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;

/**
 * Intersect intervals
 *  
 * @author pcingola
 */
public class SnpSiftCmdIntersect extends SnpSift {

	int cluster;
	int minOverlap;
	boolean intersect;
	List<String> fileNames;
	List<String> notFileNames;
	List<IntervalForest> forests;
	List<IntervalForest> notForests;

	public SnpSiftCmdIntersect(String[] args) {
		super(args, "intersect");
	}

	/**
	 * Intersect marker 'm'
	 * @param m
	 * @return
	 */
	Marker intersect(Marker m) {
		if (forests.size() == 1) return m; // Nothing to do?

		HashSet<Marker> done = new HashSet<Marker>();
		done.add(m);

		int numIntersect = 1;
		boolean updated;
		do {
			updated = false;

			// For every forest...
			for (IntervalForest forest : forests) {
				Markers query = forest.query(m); // Query intersecting intervals

				// For all results..
				for (Marker q : query) {

					// Analyze each result only once.
					if (!done.contains(q)) {
						// Check minimum number of bases that overlap
						if (m.intersectSize(q) >= minOverlap) { // Note: Once marker 'm' has been updated, new query results might not even intersect
							numIntersect++;
							String newId = m.getId() + "+" + q.getId();

							// Intersection or union?
							if (intersect) m = m.intersect(q);
							else m = m.union(q);

							m.setId(newId);
						}
					}
				}
			}
		} while (updated);

		// Check number of 'clustered' markers
		if (numIntersect >= cluster) return m;
		return null;
	}

	/**
	 * Does it intersect with any 'NOT' file
	 * @param marker
	 * @return
	 */
	boolean isNot(Marker marker) {
		if (notForests.isEmpty()) return false;

		// Intersects any forest? => true
		for (IntervalForest intfor : notForests)
			if (!intfor.query(marker).isEmpty()) return true;

		return false;
	}

	@Override
	public void parseArgs(String[] args) {
		if (args.length <= 0) usage(null);

		// Defaults
		intersect = true;
		cluster = 2;
		minOverlap = 1;
		fileNames = new ArrayList<String>();
		notFileNames = new ArrayList<String>();

		for (int argc = 0; argc < args.length; argc++) {
			if (isOpt(args[argc])) {
				String arg = args[argc].toLowerCase();

				if (arg.equals("-intersect")) intersect = true;
				else if (arg.equals("-union")) intersect = false;
				else if (arg.equals("-minoverlap")) minOverlap = Gpr.parseIntSafe(args[++argc]);
				else if (arg.equals("-cluster")) cluster = Gpr.parseIntSafe(args[++argc]);
				else if (arg.equals("-not")) notFileNames.add(args[++argc]);
				else usage("Unknown option '" + args[argc] + "'");

			} else fileNames.add(args[argc]);
		}

		// Sanity check
		if (fileNames.size() < 1) usage("Missing files to intersect");
	}

	/**
	 * Run annotations
	 */
	@Override
	public boolean run() {
		//---
		// Initialize
		//---
		Markers markersAll = new Markers();
		Markers markersNotAll = new Markers();

		//---
		// Loading intervals from files
		//---
		forests = new ArrayList<IntervalForest>();
		for (String fileName : fileNames) {
			// Load file
			if (verbose) Timer.showStdErr("Loading file '" + fileName + "'");
			Markers markers = Markers.readMarkers(fileName);
			if (verbose) Timer.showStdErr("Done. Markers in file : " + markers.size());

			// Create forest
			IntervalForest ifor = new IntervalForest();
			ifor.add(markers);
			ifor.build();
			forests.add(ifor);
			if (verbose) Timer.showStdErr("Interval forest added.");

			// Add markers to 'all'
			markersAll.add(markers);
		}
		if (verbose) Timer.showStdErr("Total number of markers (all files) : " + markersAll.size());

		//---
		// Loading 'NOT' intervals from files
		//---
		notForests = new ArrayList<IntervalForest>();
		for (String fileName : notFileNames) {
			// Load file
			if (verbose) Timer.showStdErr("Loading 'NOT' file '" + fileName + "'");
			Markers markers = Markers.readMarkers(fileName);
			if (verbose) Timer.showStdErr("Done. Markers in file : " + markers.size());

			// Create forest
			IntervalForest ifor = new IntervalForest();
			ifor.add(markers);
			ifor.build();
			notForests.add(ifor);
			if (verbose) Timer.showStdErr("Interval forest added.");

			// Add markers to 'all'
			markersNotAll.add(markers);
		}
		if (verbose && !notFileNames.isEmpty()) Timer.showStdErr("Total number of 'NOT' markers (all files) : " + markersNotAll.size());

		// Show header
		System.out.println("# " + SnpSift.VERSION_NO_NAME);
		System.out.println("# Command line: " + commandLineStr());

		//---
		// Intersect
		//---
		int coutFilteredOut = 0;
		HashSet<String> done = new HashSet<String>();
		markersAll.sort();
		for (Marker m : markersAll) {
			// Intersect
			Marker mi = intersect(m);

			// Any results? Show them
			if (mi != null) {
				// Does it intersect a 'not' interval? Don't show.
				if (isNot(mi)) {
					coutFilteredOut++;
					continue;
				}

				// Output marker data (if not already done)
				String key = mi.getChromosomeName() + "\t" + mi.getStart() + "\t" + (mi.getEnd() + 1);
				if (!done.contains(key)) {
					System.out.println(key + "\t" + mi.getId());
					done.add(key);
				}
			}
		}

		if (verbose) Timer.showStdErr("Total number of markers intersected : " + done.size() + (coutFilteredOut > 0 ? "\n\tFiltered out : " + coutFilteredOut : ""));
		return true;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar [options] file_1.bed file_2.bed ... file_N.bed");
		System.err.println("Options:");
		System.err.println("\t-minOverlap <num> : Minimum number of bases that two intervals have to overlap. Default : " + minOverlap);
		System.err.println("\t-cluster <num>    : An interval has to intersect at least 'num' intervals (from other files) to be considered. Default: " + cluster);
		System.err.println("\t-intersect        : Report the intersection of all intervals. Default: " + intersect);
		System.err.println("\t-union            : Report the union of all intervals. Default: " + !intersect);
		System.err.println("\t-not <file>       : Only report if it does NOT intersect with any interval in this file (i.e. filter out if intersects)");
		System.exit(1);
	}
}