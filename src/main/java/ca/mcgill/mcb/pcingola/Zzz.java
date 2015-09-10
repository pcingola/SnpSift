package ca.mcgill.mcb.pcingola;

import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Markers;
import ca.mcgill.mcb.pcingola.snpSift.annotate.VcfIndex;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import net.sf.samtools.tabix.TabixReader;
import net.sf.samtools.tabix.TabixReader.TabixIterator;

public class Zzz {

	public static final int MAX_LINES = 2000000;
	static boolean debug = false;
	static boolean verbose = true || debug;

	public static void main(String[] args) {
		Timer.show("Start");

		// String fileName = Gpr.HOME + "/snpEff/cosmic_1.vcf";
		// String fileName = Gpr.HOME + "/snpEff/cosmic.vcf";
		String fileName = Gpr.HOME + "/snpEff/db/GRCh37/dbSnp/dbSnp.vcf";
		// String fileName = Gpr.HOME + "/snpEff/cosmic_tabix.vcf.gz";

		Zzz zzz = new Zzz();

		zzz.testIndex(fileName);
		//		zzz.testTabix(fileName);
		//		zzz.testVcfRead(fileName, false);
		//		zzz.testVcfRead(fileName, true);
	}

	void testIndex(String fileName) {
		//---
		// Index
		//---
		VcfIndex vcfIndex = new VcfIndex(fileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.setDebug(debug);
		vcfIndex.index();
		vcfIndex.open();
		if (debug) Gpr.debug("Index:\n" + vcfIndex.toStringAll());

		Timer.show("Checking");
		VcfFileIterator vcf = new VcfFileIterator(fileName);

		//---
		// Test search
		//---
		int countOk = 0;
		for (VcfEntry ve : vcf) {
			if (debug) Gpr.debug(ve.toStr());

			// Query database
			if (debug) Gpr.debug("\n\nQuery: " + ve.toStr());
			Markers results = vcfIndex.query(ve);

			// We should find at least one result
			if (results.size() <= 0) throw new RuntimeException("No results found for entry:\n\t" + ve);

			for (Marker res : results) {
				VcfEntry veRes = (VcfEntry) res;

				if (debug) Gpr.debug("query: " + ve.toStr() + "\tresult_marker: " + res + "\tresult_vcf: " + veRes.toStr());

				// Check that result does intersect query
				if (!ve.intersects(veRes)) { throw new RuntimeException("Selected interval does not intersect marker form file!\n\tQuery: " + ve + "\n\tResult:" + veRes); }
			}

			countOk++;
			if (countOk % 100000 == 0) Timer.showStdErr("\t" + countOk + "\t" + ve.toStr());
		}

		vcfIndex.close();
		Timer.show("Done: " + countOk + " tests OK");
	}

	void testTabix(String fileName) {
		Timer timer = new Timer();
		timer.start();

		try {
			TabixReader tabix = new TabixReader(fileName);

			TabixReader.debug = true;
			TabixIterator ti = tabix.query("chr1:5778060-5794968");
			System.out.println("Iterator:" + ti);

			for (String line : ti)
				System.out.println(line);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.err.println("Done");
	}

	void testVcfRead(String fileName, boolean useSeekable) {
		Timer timer = new Timer();
		timer.start();

		try {
			VcfFileIterator vcf;
			if (useSeekable) vcf = new VcfFileIterator(new SeekableBufferedReader(fileName));
			else vcf = new VcfFileIterator(fileName);

			int lineNum = 1;
			for (VcfEntry ve : vcf) {
				if (verbose && lineNum % 10000 == 0) Timer.showStdErr(lineNum + "\t" + ve.toStr());
				lineNum++;
				if (lineNum > MAX_LINES) break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.err.println("Finished reading " + MAX_LINES + " lines from '" + fileName + "': " + timer);
	}

}
