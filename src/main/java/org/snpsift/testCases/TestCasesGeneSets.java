package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdGeneSets;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * SnpSift 'gt' test cases
 *
 * @author pcingola
 */
public class TestCasesGeneSets extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false;

	public void test_01() {
		Log.debug("Test");

		String msigDb = "test/c1.all.v4.0.symbols.gmt.gz";
		String vcf = "test/test_geneSets.vcf";
		String args[] = { "geneSets", msigDb, vcf };

		// Run command
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdGeneSets gs = (SnpSiftCmdGeneSets) snpSift.cmd();

		gs.setVerbose(verbose);
		gs.setSuppressOutput(!verbose);
		gs.setDebug(debug);
		List<VcfEntry> results = gs.run(true);

		// Check
		for (VcfEntry ve : results) {
			if (verbose) Log.info(ve.toStr() + "\t" + ve.getInfo("MSigDb"));
			Assert.assertEquals(ve.getInfo("MSigDb"), "chr1p36");
		}
	}
}
