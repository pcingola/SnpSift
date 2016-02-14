package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
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
		Gpr.debug("Test");

		String msigDb = "test/c1.all.v4.0.symbols.gmt.gz";
		String vcf = "test/test_geneSets.vcf";
		String args[] = { msigDb, vcf };

		// Run command
		SnpSiftCmdGeneSets snpSiftCmdEpistasis = new SnpSiftCmdGeneSets(args);
		snpSiftCmdEpistasis.setVerbose(verbose);
		snpSiftCmdEpistasis.setSuppressOutput(!verbose);
		snpSiftCmdEpistasis.setDebug(debug);
		List<VcfEntry> results = snpSiftCmdEpistasis.run(true);

		// Check
		for (VcfEntry ve : results) {
			if (verbose) System.out.println(ve.toStr() + "\t" + ve.getInfo("MSigDb"));
			Assert.assertEquals(ve.getInfo("MSigDb"), "chr1p36");
		}
	}
}
