package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfLof;

/**
 * Filter test cases
 *
 * @author pcingola
 */
public class TestCasesFilter extends TestCase {

	public static boolean verbose = false;

	/**
	 * Filter by quality
	 */
	public void test_01() {
		Gpr.debug("Test");

		double minQ = 50;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "QUAL >= " + minQ;
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getQuality() >= minQ);
		}
	}

	/**
	 * Filter by chromosome
	 */
	public void test_02() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(CHROM = '19')";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getChromosomeName().equals("19"));
		}
	}

	/**
	 * Filter by position
	 */
	public void test_03() {
		Gpr.debug("Test");

		int minPos = 20175;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS > " + minPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry.getStart() + "\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getStart() > (minPos - 1));
		}
	}

	/**
	 * Filter by position
	 */
	public void test_04() {
		Gpr.debug("Test");

		int minPos = 20175;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry.getStart() + "\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getStart() >= (20175 - 1));
		}
	}

	/**
	 * Filter by position
	 */
	public void test_05() {
		Gpr.debug("Test");

		int maxPos = 20175;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS < " + maxPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getStart() < (maxPos - 1));
		}
	}

	/**
	 * Filter by position
	 */
	public void test_06() {
		Gpr.debug("Test");

		int maxPos = 20175;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS <= " + maxPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getStart() <= (maxPos - 1));
		}
	}

	/**
	 * Filter by position (AND test)
	 */
	public void test_07() {
		Gpr.debug("Test");

		int minPos = 20175;
		int maxPos = 35549;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ") & (POS <= " + maxPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getStart() >= (minPos - 1));
			Assert.assertTrue(vcfEntry.getStart() <= (maxPos - 1));
		}
	}

	/**
	 * Filter by position (OR test)
	 */
	public void test_08() {
		Gpr.debug("Test");

		int minPos = 20175;
		int maxPos = 35549;

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ") | (POS <= " + maxPos + ")";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue( //
			(vcfEntry.getStart() >= (minPos - 1)) //
					|| (vcfEntry.getStart() <= (maxPos - 1)) //
			);
		}
	}

	/**
	 * Regexp test
	 */
	public void test_09() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( CHROM =~ 'NT_' )";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getChromosomeName().startsWith("NT_"));
		}
	}

	/**
	 * REF and ALT values
	 */
	public void test_10() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( REF = 'C' ) & ( ALT = 'T') ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getRef().equals("C"));
			Assert.assertTrue(vcfEntry.getAltsStr().equals("T"));
		}
	}

	/**
	 * Filter by coverage
	 */
	public void test_11() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( DP >= 5 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(Gpr.parseIntSafe(vcfEntry.getInfo("DP")) >= 5);
		}
	}

	/**
	 * Filter by INDEL info tag
	 */
	public void test_12() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( exists INDEL ) ";
		snpsiftFilter.setVerbose(verbose);
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals(182, list.size());
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertNotNull(vcfEntry.getInfo("INDEL"));
		}
	}

	/**
	 * Filter by INDEL info tag
	 */
	public void test_13() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( exists INDEL ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertNotNull(vcfEntry.getInfo("INDEL"));
		}
	}

	/**
	 * Filter by PL genottype tag
	 */
	public void test_14() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( GEN[0].PL[1] > 100 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);

		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			String pl = vcfGenotype.get("PL");
			String plSub = pl.split(",")[1];
			int plSubInt = Gpr.parseIntSafe(plSub);

			Assert.assertTrue(plSubInt > 10);
			count++;
		}

		Assert.assertTrue(count == 24);
	}

	/**
	 * Filter by GT genottype tag
	 */
	public void test_15() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( GEN[0].GT = '1/1' ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			String gt = vcfGenotype.get("GT");

			Assert.assertEquals("1/1", gt);
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_16() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( isHom ( GEN[0] ) ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		int count = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			Assert.assertTrue(vcfGenotype.isHomozygous());
			count++;
		}

		Assert.assertEquals(821, count);
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_17() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( isHet ( GEN[0] ) ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			Assert.assertTrue(vcfGenotype.isHeterozygous());
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_18() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( isRef ( GEN[0] ) ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			Assert.assertTrue(!vcfGenotype.isVariant());
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_19() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( isVariant ( GEN[0] ) ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			Assert.assertTrue(vcfGenotype.isVariant());
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_20() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "isVariant ( GEN[0] ) & isHom( GEN[0] ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			String gt = vcfGenotype.get("GT");
			Assert.assertEquals("1|1", gt);
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_21() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "isVariant ( GEN[1] ) & isHom( GEN[1] ) & isRef( GEN[2] )";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String gt = vcfEntry.getVcfGenotype(1).get("GT");
			Assert.assertEquals("1|1", gt);

			gt = vcfEntry.getVcfGenotype(2).get("GT");
			Assert.assertEquals("0|0", gt);
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_22() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		snpsiftFilter.addSet("test/set_rs_test01.txt");
		snpsiftFilter.addSet("test/set_rs_test02.txt");
		String expression = "ID in SET[1]";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);

		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String id = vcfEntry.getId();
			Assert.assertTrue(id.equals("rs58108140") //
					|| id.equals("rs71262674") //
					|| id.equals("rs71262673"));
		}

		Assert.assertEquals(3, list.size());
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_22_3() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		snpsiftFilter.addSet("test/set_rs_test01.txt");
		snpsiftFilter.addSet("test/set_rs_test02.txt");
		snpsiftFilter.addSet("test/set_rs_test03.txt");
		String expression = "ID in SET[2]";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);

		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
		}

		Assert.assertEquals(67, list.size());
	}

	/**
	 * Filter by GT[*] (any genottype)
	 */
	public void test_23() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "GEN[*].GT = '1|1'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			for (VcfGenotype gen : vcfEntry)
				any |= (gen.getGenotype()[0] == 1) && (gen.getGenotype()[1] == 1);

			Assert.assertEquals(true, any);
		}

		Assert.assertEquals(147, list.size());
	}

	/**
	 * Filter by GT[0].VV[*] (any sub field in a genottype)
	 */
	public void test_24() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "GEN[0].AP[*] > 0.8";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;

			String ap = vcfEntry.getVcfGenotypes().get(0).get("AP");
			for (String a : ap.split(","))
				any |= Gpr.parseDoubleSafe(a) > 0.8;

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by GT[*].VV[*] (any sub field in any genottype)
	 */
	public void test_25() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "GEN[*].AP[*] > 0.95";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;

			for (VcfGenotype gen : vcfEntry) {
				String ap = gen.get("AP");
				for (String a : ap.split(","))
					any |= Gpr.parseDoubleSafe(a) > 0.95;
			}
			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by EFF[0].EFFECT (effect)
	 */
	public void test_26() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[0].EFFECT = 'SYNONYMOUS_CODING'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String eff = vcfEntry.getInfo("EFF").split("\\(")[0];
			Assert.assertEquals(eff, "SYNONYMOUS_CODING");
		}

		Assert.assertEquals(2, list.size());
	}

	/**
	 * Filter by ANN[0].EFFECT (effect)
	 */
	public void test_26_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[0].EFFECT = 'synonymous_variant'";
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
	 * Filter by EFF[*].EFFECT (any effect)
	 */
	public void test_27() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].EFFECT = 'SYNONYMOUS_CODING'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\(")[0];
				any |= e.equals("SYNONYMOUS_CODING");
			}

			Assert.assertEquals(true, any);
		}

		Assert.assertEquals(4, list.size()); // Check that total number of lines is OK
	}

	/**
	 * Filter by ANN[*].EFFECT (any effect)
	 */
	public void test_27_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].EFFECT = 'synonymous_variant'";
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
	 * Filter by ANN[*].EFFECT (any effect)
	 */
	public void test_27_ann2() {
		Gpr.debug("Test");

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

	/**
	 * Test countHom function
	 */
	public void test_28() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( countHom() = 3 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (gen.isHomozygous()) count++;
			}

			Assert.assertTrue(count == 3);
		}

		Assert.assertEquals(105, list.size());
	}

	/**
	 * Test countHet function
	 */
	public void test_29() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( countHet() = 3 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (gen.isHeterozygous()) count++;
			}

			Assert.assertTrue(count == 3);
		}
	}

	/**
	 * Test countRef function
	 */
	public void test_30() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( countRef() = 3 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (!gen.isVariant()) count++;
			}

			Assert.assertEquals(3, count);
		}
		Assert.assertEquals(84, list.size());
	}

	/**
	 * Test countVariant function
	 */
	public void test_31() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( countVariant() = 3 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (gen.isVariant()) count++;
			}

			Assert.assertTrue(count == 3);
		}
	}

	/**
	 * Filter by EFF[*].CODING
	 */
	public void test_32() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].CODING = 'CODING'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|")[6];
				any |= e.equals("CODING");
			}

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by ANN[*].CODING
	 */
	public void test_32_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].BIOTYPE = 'protein_coding'";
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
	 * Filter by EFF[*].CODING
	 */
	public void test_33() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].CODING = 'NON_CODING'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\|")[6];
				any |= e.equals("NON_CODING");
			}

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by ANN[*].CODING
	 */
	public void test_33_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].BIOTYPE = 'lincRNA'";
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
	 * Filter by EFF[ALL].EFFECT
	 */
	public void test_34() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(EFF[ALL].EFFECT = 'DOWNSTREAM')";
		List<VcfEntry> list = snpsiftFilter.filter("test/downstream.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			boolean all = true;
			String effStr = vcfEntry.getInfo("EFF");
			for (String eff : effStr.split(",")) {
				String e = eff.split("\\(")[0];
				all &= e.equals("DOWNSTREAM");
			}

			if (!all) Gpr.debug("Error: " + effStr);
			Assert.assertEquals(true, all);
		}
	}

	/**
	 * Filter by ANN[ALL].EFFECT
	 */
	public void test_34_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(ANN[ALL].EFFECT = 'downstream_gene_variant')";
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
	public void test_35() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].GENE = 'BICD1'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test_gene.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			for (VcfEffect eff : vcfEntry.parseEffects(null)) {
				Assert.assertEquals("BICD1", eff.getGeneName());
				any = true;
			}

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by EFF[*].GENE
	 */
	public void test_35_ann() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].GENE = 'BICD1'";
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

	/**
	 * Inverse of a filter
	 */
	public void test_36() {
		Gpr.debug("Test");

		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-n", expression };
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertTrue(vcfEntry.getQuality() < minQ);
		}
	}

	/**
	 * Use filter field (add 'PASS' if expression is true)
	 */
	public void test_37() {
		Gpr.debug("Test");

		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-p", expression };
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilterPass() + "\t" + vcfEntry);
			if (vcfEntry.getFilterPass().equals("PASS")) Assert.assertTrue(vcfEntry.getQuality() >= minQ);
			else Assert.assertTrue(vcfEntry.getQuality() < minQ);
		}
	}

	/**
	 * Add a string to FILTER if expression is true)
	 */
	public void test_38() {
		Gpr.debug("Test");

		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-a", "ADD", expression };
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilterPass() + "\t" + vcfEntry);
			if (vcfEntry.getFilterPass().equals("ADD")) Assert.assertTrue(vcfEntry.getQuality() >= minQ);
			else Assert.assertTrue(vcfEntry.getQuality() < minQ);
		}
	}

	/**
	 * Remove FILTER strings
	 */
	public void test_39() {
		Gpr.debug("Test");

		// Filter data
		String expression = "REF = 'A'";
		String vcfFile = "test/downstream.vcf";
		String args[] = { "-f", vcfFile, "-r", "SVM", expression }; // Remove FILTER string 'SVM' from all reference = 'A'
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilterPass() + "\t" + vcfEntry);
			if (vcfEntry.getFilterPass().equals("SVM")) Assert.assertTrue(!vcfEntry.getRef().equals("A"));
		}
	}

	/**
	 * Inverse FILTER strings
	 */
	public void test_40() {
		Gpr.debug("Test");

		// Filter data
		String expression = "( EFF[*].EFFECT = 'SPLICE_SITE_ACCEPTOR' )";
		String vcfFile = "test/test_jim.vcf";
		String args[] = { "-f", vcfFile, "-n", expression }; // FILTER iNverse
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);

		// Check result (hould be only one entry)
		VcfEntry vcfEntry = list.get(0);
		if (verbose) System.out.println(vcfEntry.getFilterPass() + "\t" + vcfEntry);
		Assert.assertEquals(219134272, vcfEntry.getStart());
	}

	/**
	 * Remove filter option '-rmFilter'
	 * Bug reported by Jim Johnson
	 */
	public void test_41() {
		Gpr.debug("Test");

		// Filter data
		String expression = "( DP < 5 )";
		String vcfFile = "test/test_rmfilter.vcf";
		String args[] = { "-f", vcfFile, "--rmFilter", "DP_OK", expression }; // Remove 'PASS' if there is not enough depth
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = snpsiftFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 2);

		// Check result (hould be only one entry)
		VcfEntry vcfEntry = list.get(0);
		if (verbose) System.out.println(vcfEntry.getFilterPass() + "\t" + vcfEntry);

		if (vcfEntry.getStart() == 219134261) Assert.assertEquals("OTHER", vcfEntry.getFilterPass());
		if (vcfEntry.getStart() == 219134272) Assert.assertEquals("DP_OK;OTHER", vcfEntry.getFilterPass());
	}

	/**
	 * Test compare to missing field
	 */
	public void test_42() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ = 3 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

	/**
	 * Test compare to missing field
	 */
	public void test_43() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ < 0 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	/**
	 * Test compare to missing field
	 */
	public void test_44() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ > 0 ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	/**
	 * LOF[*].PERC > 0.1
	 */
	public void test_45() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter();
		String expression = "LOF[*].PERC > 0.1";
		List<VcfEntry> list = snpSiftFilter.filter("test/test45.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);

		int count = 0;
		for (VcfEntry ve : list) {
			if (verbose) System.out.println(ve);

			for (VcfLof lof : ve.parseLof()) {
				if (verbose) System.out.println("\t" + lof);
				Assert.assertTrue(lof.getPercentAffected() >= 0.1);
				count++;
			}
		}

		Assert.assertEquals(2, count);
	}

	public void test_46() {
		Gpr.debug("Test");

		String fileName = "./test/test46.vcf";
		String args[] = { "-f", fileName, "exists dbNSFP_SIFT_pred" };

		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> ves = snpSiftFilter.run(true);

		Assert.assertEquals(1, ves.size());
	}

	public void test_47() {
		Gpr.debug("Test");

		String fileName = "./test/test46.vcf";
		String args[] = { "-f", fileName, "dbNSFP_SIFT_pred != 'D'" };

		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> ves = snpSiftFilter.run(true);

		Assert.assertEquals(1, ves.size());
		String field = ves.get(0).getInfo("dbNSFP_SIFT_pred");
		Assert.assertEquals("T", field);
	}

	public void test_48() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ = NaN ) ";
		List<VcfEntry> list = snpsiftFilter.filter("test/test48.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		Assert.assertEquals(list.get(0).getInfo("ZZZ"), "NaN");
	}

	public void test_49() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "( DP < (AC+4))";
		List<VcfEntry> list = snpsiftFilter.filter("test/test49.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		Assert.assertEquals("4", list.get(0).getInfo("AC"));
	}

	/**
	 * Filter by EFF[*] (whole field comparison)
	 */
	public void test_50() {
		Gpr.debug("Test");

		String effStr = "NON_SYNONYMOUS_CODING(MODERATE|MISSENSE|Cat/Tat|H52Y|AL669831.1|protein_coding|CODING|ENST00000358533|exon_1_721320_722513)";

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*] = '" + effStr + "'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean ok = false;
			if (verbose) System.out.println(vcfEntry);
			for (String eff : vcfEntry.getInfo("EFF").split(",")) {
				ok |= eff.equals(effStr);
				if (verbose) System.out.println("\t" + eff);
			}

			Assert.assertEquals(true, ok);
		}

		Assert.assertEquals(1, list.size());
	}

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
