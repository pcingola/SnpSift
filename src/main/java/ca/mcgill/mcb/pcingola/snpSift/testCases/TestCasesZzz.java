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

	public static boolean debug = true;
	public static boolean verbose = false || debug;

	protected String[] defaultExtraArgs = null;

	/**
	 * Filter by ANN[0].EFFECT (effect)
	 */
	public void test_26_ann() {
		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[0].EFFECT = 'synonymous_variant'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		int count = 0;
		System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String eff = vcfEntry.getInfo("ANN").split("\\|")[1];
			Assert.assertEquals("synonymous_variant", eff);
			count++;
		}

		Assert.assertEquals(3, count);
	}

	/**
	 * Filter by ANN[*].EFFECT (any effect)
	 */
	public void test_27_ann() {
		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].EFFECT = 'synonymous_variant'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|")[1];
				any |= e.equals("synonymous_variant");
			}

			Assert.assertEquals(true, any);
			if (any) count++;
		}

		Assert.assertEquals(3, count);
	}

	//	/**
	//	 * Filter by ANN[*].CODING
	//	 */
	//	public void test_32_ann() {
	//		// Filter data
	//		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
	//		String expression = "ANN[*].BIOTYPE = 'CODING'";
	//		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);
	//
	//		// Check that it satisfies the condition
	//		System.out.println("Expression: '" + expression + "'");
	//		for (VcfEntry vcfEntry : list) {
	//			if (verbose) System.out.println("\t" + vcfEntry);
	//
	//			boolean any = false;
	//			String effStr = vcfEntry.getInfo("EFF");
	//			for (String eff : effStr.split(",")) {
	//				String e = eff.split("\\|")[6];
	//				any |= e.equals("CODING");
	//			}
	//
	//			Assert.assertEquals(true, any);
	//		}
	//	}
	//
	//	/**
	//	 * Filter by ANN[*].CODING
	//	 */
	//	public void test_33_ann() {
	//		// Filter data
	//		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
	//		String expression = "ANN[*].CODING = 'NON_CODING'";
	//		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);
	//
	//		// Check that it satisfies the condition
	//		System.out.println("Expression: '" + expression + "'");
	//		for (VcfEntry vcfEntry : list) {
	//			if (verbose) System.out.println("\t" + vcfEntry);
	//
	//			boolean any = false;
	//			String effStr = vcfEntry.getInfo("EFF");
	//			for (String eff : effStr.split(",")) {
	//				String e = eff.split("\\|")[6];
	//				any |= e.equals("NON_CODING");
	//			}
	//
	//			Assert.assertEquals(true, any);
	//		}
	//	}
	//
	//	/**
	//	 * Filter by ANN[ALL].EFFECT
	//	 */
	//	public void test_34_ann() {
	//		// Filter data
	//		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
	//		String expression = "(ANN[ALL].EFFECT = 'DOWNSTREAM')";
	//		List<VcfEntry> list = snpsiftFilter.filter("test/downstream.vcf", expression, true);
	//
	//		// Check that it satisfies the condition
	//		System.out.println("Expression: '" + expression + "'");
	//		for (VcfEntry vcfEntry : list) {
	//			boolean all = true;
	//			String effStr = vcfEntry.getInfo("EFF");
	//			for (String eff : effStr.split(",")) {
	//				String e = eff.split("\\(")[0];
	//				all &= e.equals("DOWNSTREAM");
	//			}
	//
	//			if (!all) Gpr.debug("Error: " + effStr);
	//			Assert.assertEquals(true, all);
	//		}
	//	}
	//
	//	/**
	//	 * Filter by EFF[*].GENE
	//	 */
	//	public void test_35_ann() {
	//		// Filter data
	//		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
	//		String expression = "ANN[*].GENE = 'BICD1'";
	//		List<VcfEntry> list = snpsiftFilter.filter("test/test_gene.vcf", expression, true);
	//
	//		// Check that it satisfies the condition
	//		System.out.println("Expression: '" + expression + "'");
	//		for (VcfEntry vcfEntry : list) {
	//			if (verbose) System.out.println("\t" + vcfEntry);
	//
	//			boolean any = false;
	//			for (VcfEffect eff : vcfEntry.parseEffects(null)) {
	//				Assert.assertEquals("BICD1", eff.getGeneName());
	//				any = true;
	//			}
	//
	//			Assert.assertEquals(true, any);
	//		}
	//	}

}
