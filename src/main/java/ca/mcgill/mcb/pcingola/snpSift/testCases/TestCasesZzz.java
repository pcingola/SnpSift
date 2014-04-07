package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 * 
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean verbose = false;
	public static boolean debug = false;

	public void test_48() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ = NaN ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test48.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		Assert.assertEquals(list.get(0).getInfo("ZZZ"), "NaN");
	}

}
