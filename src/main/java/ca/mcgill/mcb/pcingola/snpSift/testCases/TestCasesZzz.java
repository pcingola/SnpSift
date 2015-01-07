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

	protected String[] defaultExtraArgs = null;

	/**
	 * LOF[*] : Whole field 
	 */
	public void test_51() {
		Gpr.debug("Test");

		String lofStr = "(CAMTA1|ENSG00000171735|17|0.29)";

		// Filter data
		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter();
		String expression = "LOF[*] = '" + lofStr + "'";
		List<VcfEntry> list = snpSiftFilter.filter("test/test45.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);

		for (VcfEntry ve : list) {
			if (verbose) System.out.println(ve);

			boolean ok = false;
			for (String lof : ve.getInfo("LOF").split(",")) {
				if (verbose) System.out.println("\t" + lof);
				ok |= lof.equals(lofStr);
			}

			Assert.assertTrue(ok);
		}

		Assert.assertEquals(1, list.size());
	}

}
