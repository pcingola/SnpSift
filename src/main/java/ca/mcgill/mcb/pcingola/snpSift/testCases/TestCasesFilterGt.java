package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdGtFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

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
		Gpr.debug("Test");
		String expression = "(GQ < 50) | (DP < 20)";

		SnpSiftCmdGtFilter gtfilter = new SnpSiftCmdGtFilter();
		List<VcfEntry> list = gtfilter.filter("test/test45.vcf", expression, true);

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
					Assert.assertTrue(gtStr.equals("."));
				}

				if (verbose) System.out.println("\tGT: " + gtStr + " " + filtered + "\tGQ: " + gqStr + "\tDP: " + dpStr + "\t" + gt);
			}
		}
	}

}
