package ca.mcgill.mcb.pcingola;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.snpSift.annotate.IntervalFile;
import ca.mcgill.mcb.pcingola.snpSift.annotate.MarkerFile;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

public class Zzz {

	static boolean debug = false;
	static boolean verbose = true;

	public static void main(String[] args) {
		Timer.show("Start");

		String fileName = Gpr.HOME + "/snpEff/cosmic.vcf";

		IntervalFile vcfIndex = new IntervalFile(fileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.index();
		vcfIndex.open();

		Timer.show("Checking");
		VcfFileIterator vcf = new VcfFileIterator(fileName);
		for (VcfEntry ve : vcf) {
			if (debug) System.out.println(ve.toStr());

			// Query database
			Markers results = vcfIndex.query(ve);

			// We should find at least one result
			if (results.size() <= 0) throw new RuntimeException("No results found for entry:\n\t" + ve);

			for (Marker res : results) {

				MarkerFile resmf = (MarkerFile) res;
				VcfEntry veIdx = vcfIndex.read(resmf);
				if (debug) System.out.println("\t" + res + "\t" + veIdx);

				// Check that result does intersect query
				if (!ve.intersects(veIdx)) throw new RuntimeException("Selected interval does not intersect marker form file!");
			}
		}

		vcfIndex.close();
		Timer.show("Done");
	}

}
