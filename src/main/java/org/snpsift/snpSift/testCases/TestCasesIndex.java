package org.snpsift.snpSift.testCases;

import java.io.File;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Marker;
import org.snpeff.interval.Markers;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.snpSift.annotate.VcfIndex;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesIndex extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	/**
	 * Index a VCF file and query all entries
	 */
	public void test_01() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_index_01.vcf";

		// Make sure index file is deleted
		String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
		(new File(indexFileName)).delete();

		// Index VCF file
		VcfIndex vcfIndex = new VcfIndex(dbFileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.open();
		vcfIndex.index();

		// Check that all entries can be found & retrieved
		if (verbose) Gpr.debug("Checking");
		VcfFileIterator vcf = new VcfFileIterator(dbFileName);
		for (VcfEntry ve : vcf) {
			if (verbose) System.out.println(ve.toStr());

			// Query database
			for (Variant var : ve.variants()) {
				Markers results = vcfIndex.query(var);

				// We should find at least one result
				Assert.assertTrue("No results found for entry:\n\t" + ve, results.size() > 0);

				// Check each result
				for (Marker res : results) {
					VcfEntry veIdx = (VcfEntry) res;
					if (verbose) System.out.println("\t" + res + "\t" + veIdx);

					// Check that result does intersect query
					Assert.assertTrue("Selected interval does not intersect marker form file!" //
							+ "\n\tVcfEntry            : " + ve //
							+ "\n\tVariant             : " + var //
							+ "\n\tResult              : " + res //
							+ "\n\tVcfEntry from result:" + veIdx//
							, ve.intersects(veIdx) //
					);
				}
			}
		}

		vcfIndex.close();

	}

	/**
	 * Index a VCF file and query all entries
	 */
	public void test_02() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_index_02.vcf";

		// Index VCF file
		String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
		(new File(indexFileName)).delete();

		// Create index file
		VcfIndex vcfIndex = new VcfIndex(dbFileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.index();

		// Make sure index file was created
		Assert.assertTrue("Index file '" + indexFileName + "' does not exist", Gpr.exists(indexFileName));

		// Restart so we force to read from index file
		vcfIndex = new VcfIndex(dbFileName);
		vcfIndex.setVerbose(verbose);
		vcfIndex.open();
		vcfIndex.index();

		// Check that all entries can be found & retrieved
		if (verbose) Gpr.debug("Checking");
		VcfFileIterator vcf = new VcfFileIterator(dbFileName);
		for (VcfEntry ve : vcf) {
			if (verbose) System.out.println(ve.toStr());

			// Query database
			for (Variant var : ve.variants()) {
				Markers results = vcfIndex.query(var);

				// We should find at least one result
				Assert.assertTrue("No results found for entry:\n\t" + ve, results.size() > 0);

				// Check each result
				for (Marker res : results) {
					VcfEntry veIdx = (VcfEntry) res;
					if (verbose) System.out.println("\t" + res + "\t" + veIdx);

					// Check that result does intersect query
					Assert.assertTrue("Selected interval does not intersect marker form file!" //
							+ "\n\tVcfEntry            : " + ve //
							+ "\n\tVariant             : " + var //
							+ "\n\tResult              : " + res //
							+ "\n\tVcfEntry from result:" + veIdx//
							, ve.intersects(veIdx) //
					);
				}
			}
		}

		vcfIndex.close();
	}

}
