package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
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
	 * Filter by ANN[0].EFFECT (effect)
	 */
	public void test_26_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[0].EFFECT = 'synonymous_variant'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		int count = 0;
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String eff = vcfEntry.getInfo("ANN").split("\\|")[1];

			Assert.assertEquals("synonymous_variant", eff); // Check that all lines match the expression
			count++;
		}

		Assert.assertEquals(3, count); // Check that total number of lines is OK
	}

	/**
	 * Filter by ANN[*].EFFECT (any effect)
	 */
	public void test_27_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].EFFECT = 'synonymous_variant'";
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
				if (e.equals("synonymous_variant")) {
					count++;
					any = true;
				}
			}

			Assert.assertEquals(true, any); // Check that all lines match the expression
		}

		Assert.assertEquals(3, count); // Check that total number of lines is OK
	}

	/**
	 * Filter by ANN[*].CODING
	 */
	public void test_32_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].BIOTYPE = 'protein_coding'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("ANN");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|", -1)[7];
				if (e.equals("protein_coding")) {
					count++;
					any = true;
				}
			}
			Assert.assertEquals(true, any); // Check that all lines match the expression
		}

		Assert.assertEquals(113, count); // Check that total number of lines is OK
	}

	/**
	 * Filter by ANN[*].CODING
	 */
	public void test_33_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].BIOTYPE = 'lincRNA'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.ann.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("ANN");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|", -1)[7];
				any |= (e.equals("lincRNA"));
			}

			if (any) count++;
			Assert.assertEquals(true, any); // Check that all lines match the expression
		}

		Assert.assertEquals(727, count); // Check that total number of lines is OK
	}

	/**
	 * Filter by ANN[ALL].EFFECT
	 */
	public void test_34_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(EFF[ALL].EFFECT = 'downstream_gene_variant')";
		List<VcfEntry> list = snpsiftFilter.filter("test/downstream.ann.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			boolean all = true;
			String effStr = vcfEntry.getInfo("ANN");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|")[1];
				all &= e.equals("downstream_gene_variant");
			}

			if (!all) Gpr.debug("Error: " + effStr);
			if (all) count++;
			Assert.assertEquals(true, all); // Check that all lines match the expression
		}

		Assert.assertEquals(2, count); // Check that total number of lines is OK
	}

	/**
	 * Filter by EFF[*].GENE
	 */
	public void test_35_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].GENE = 'BICD1'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test_gene.ann.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			for (VcfEffect eff : vcfEntry.parseEffects(null)) {
				Assert.assertEquals("BICD1", eff.getGeneName());
				any = true;
			}

			if (any) count++;
			Assert.assertEquals(true, any); // Check that all lines match the expression
		}

		Assert.assertEquals(1, count); // Check that total number of lines is OK
	}

}
