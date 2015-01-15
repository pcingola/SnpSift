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

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	/**
	 * Filter by ANN[*].EFFECT (any effect)
	 */
	public void test_27_ann() {
		Gpr.debug("Test");
		verbose = true;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].EFFECT = 'missense_variant'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String effStr = vcfEntry.getInfo("ANN");
			boolean any = false;
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|")[1];
				if (e.equals("missense_variant")) {
					count++;
					any = true;
				}
			}

			Assert.assertEquals(true, any); // Check that all lines match the expression
		}

		Assert.assertEquals(6, count); // Check that total number of lines is OK
	}
}
