package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
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

	List<VcfEntry> snpSiftFilter(String args[]) {
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdFilter snpSiftFilter = (SnpSiftCmdFilter) snpSift.cmd();
		return snpSiftFilter.run(true);
	}

	/**
	 * Inverse of a filter
	 */
	public void test_36() {
		Gpr.debug("Test");

		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "filter", "-f", "test/test01.vcf", "-n", expression };
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getQuality() < minQ);
		}
	}

}
