package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = true;
	public static boolean verbose = true || debug;

	/**
	 * Filter using 'has' operator
	 */
	public void test_53() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].EFFECT has 'synonymous_variant'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test_filter_has.vcf", expression, true);

		if (verbose) {
			System.out.println("Expression: '" + expression + "'");
			for (VcfEntry vcfEntry : list)
				if (verbose) System.out.println("VCF entry:\t" + vcfEntry);
		}

		// Check that one line satisfies the condition
		Assert.assertEquals(1, list.size());
	}

}
