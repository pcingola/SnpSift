package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfLof;

/**
 * Try test cases in this class before adding them to long test cases
 * 
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean verbose = true;

	/**
	 * LOF[*].PERC > 0.1
	 */
	public void test_45() {
		// Filter data
		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter();
		String expression = "LOF[*].PERC > 0.1";
		List<VcfEntry> list = snpSiftFilter.filter("test/test45.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);

		int count = 0;
		for (VcfEntry ve : list) {
			System.out.println(ve);

			for (VcfLof lof : ve.parseLof()) {
				System.out.println("\t" + lof);
				Assert.assertTrue(lof.getPercentAffected() >= 0.1);
				count++;
			}
		}

		Assert.assertEquals(2, count);
	}
}
