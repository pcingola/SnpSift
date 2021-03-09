package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSiftCmdFilter;

import junit.framework.Assert;
import junit.framework.TestCase;

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
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean all = true;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\(")[0];
				all &= e.equals("DOWNSTREAM");
			}

			if (!all) Log.debug("Error: " + effStr);
			Assert.assertEquals(true, all);
		}

		Assert.assertEquals(163, list.size());

	}

}
