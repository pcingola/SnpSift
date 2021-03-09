package org.snpsift.testCases;

import java.util.List;

import org.junit.Assert;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.SnpSiftCmdFilterGt;

import junit.framework.TestCase;

/**
 * Filter test cases for 'GT Filter'
 *
 * @author pcingola
 */
public class TestCasesFilterGt extends TestCase {

	public static boolean verbose = false;

	/**
	 * Filter
	 */
	public void test_01() {
		Log.debug("Test");
		String expression = "(GQ < 50) | (DP < 20)";

		SnpSiftCmdFilterGt gtfilter = new SnpSiftCmdFilterGt();
		List<VcfEntry> list = gtfilter.filter("test/testGtFilter01.vcf", expression, true);

		for (VcfEntry ve : list) {
			if (verbose) System.out.println(ve);

			for (VcfGenotype gt : ve.getVcfGenotypes()) {
				String gtStr = gt.get("GT");
				String gqStr = gt.get("GQ");
				String dpStr = gt.get("DP");

				int gq = Gpr.parseIntSafe(gqStr);
				int dp = Gpr.parseIntSafe(dpStr);

				String filtered = "";
				if ((gq < 50) | (dp < 20)) {
					filtered = "FILTERED";
					Assert.assertEquals("./.", gtStr);
				}

				if (verbose) System.out.println("\tGT: " + gtStr + " " + filtered + "\tGQ: " + gqStr + "\tDP: " + dpStr + "\t" + gt);
			}
		}
	}

	/**
	 * Filter
	 */
	public void test_02() {
		Log.debug("Test");
		String expression = "(GQ < 50) | (DP < 20)";

		SnpSiftCmdFilterGt gtfilter = new SnpSiftCmdFilterGt();
		List<VcfEntry> list = gtfilter.filter("test/testGtFilter02.vcf", expression, true);

		for (VcfEntry ve : list) {
			if (verbose) System.out.println(ve);

			for (VcfGenotype gt : ve.getVcfGenotypes()) {
				String gtStr = gt.get("GT");
				String gqStr = gt.get("GQ");
				String dpStr = gt.get("DP");

				int gq = Gpr.parseIntSafe(gqStr);
				int dp = Gpr.parseIntSafe(dpStr);

				String filtered = "";
				if ((gq < 50) | (dp < 20)) {
					filtered = "FILTERED";
					Assert.assertEquals("./././.", gtStr);
				}

				if (verbose) System.out.println("\tGT: " + gtStr + " " + filtered + "\tGQ: " + gqStr + "\tDP: " + dpStr + "\t" + gt);
			}
		}
	}

}
