package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 * 
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean verbose = true;

	/**
	 * Test compare to missing field
	 */
	public void test_42() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

	/**
	 * Test compare to missing field
	 */
	public void test_43() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ < 0 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

	/**
	 * Test compare to missing field
	 */
	public void test_44() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ > 0 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

}
