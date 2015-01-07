package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdGtFilter;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
		String expression = "(GQ < 50) | (DP < 20)";

		SnpSiftCmdGtFilter gtfilter = new SnpSiftCmdGtFilter();
		List<VcfEntry> list = gtfilter.filter("test/test45.vcf", expression, true);

		throw new RuntimeException("Unimplemented!");
	}

}
