package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSiftCmdFilter;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	public TestCasesZzz() {
	}

	/**
	 * Remove filter option '-rmFilter'. Check that INFO 
	 * field 'FILTER_DELETED' is properly added
	 */
	public void test_58_rmFilter_info_field() {
		Gpr.debug("Test");

		// Filter data
		String expression = "( DP < 5 )";
		String vcfFile = "test/test_rmfilter.vcf";
		String args[] = { "-f", vcfFile, "--rmFilter", "DP_OK", expression }; // Remove 'PASS' if there is not enough depth
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue("List size does not matched expected", list.size() == 3);

		// Check result (hould be only one entry)
		int countOk = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);

			// Filter deleted
			if (vcfEntry.getStart() == 219134261) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				Assert.assertEquals("DP_OK", vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}

			// Nothing done
			if (vcfEntry.getStart() == 219134272) {
				Assert.assertEquals("DP_OK;OTHER", vcfEntry.getFilter());
				Assert.assertEquals(null, vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}

			// Filter deleted + old "FILTER_DELETED" entry kept
			if (vcfEntry.getStart() == 219134298) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				Assert.assertEquals("DELETED_BEFORE,DP_OK", vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}
		}
		Assert.assertEquals("Number of entries checkd does not match expected", countOk, 3);

	}

}
