package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Filter test cases for 'ALL' index
 *
 * @author pcingola
 */
public class TestCasesFilterALL extends TestCase {

	public static boolean verbose = false;

	/**
	 * Filter by EFF[ALL].EFFECT
	 */
	public void test_34() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(EFF[ALL].EFFECT = 'DOWNSTREAM')";
		List<VcfEntry> list = vcfFilter.filter("test/downstream.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean all = true;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\(")[0];
				all &= e.equals("DOWNSTREAM");
			}

			if (!all) Gpr.debug("Error: " + effStr);
			Assert.assertEquals(true, all);
		}
	}

}
