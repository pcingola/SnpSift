package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = true;
	public static boolean verbose = true || debug;

	protected String[] defaultExtraArgs = null;

	/**
	 * Filter by PL genottype tag
	 */
	public void test_14() {
		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( GEN[0].PL[1] > 10 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			String pl = vcfGenotype.get("PL");
			String plSub = pl.split(",")[1];
			int plSubInt = Gpr.parseIntSafe(plSub);

			Assert.assertTrue(plSubInt > 10);
		}
	}

}
