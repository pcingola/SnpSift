package org.snpsift.testCases;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEffect;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpeff.vcf.VcfLof;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdFilter;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Filter test cases
 *
 * @author pcingola
 */
public class TestCasesFilter extends TestCase {

	public static boolean verbose = false;
	public static final int STDOUT_BUFFER_SIZE = 10 * 1024 * 1024;

	List<VcfEntry> snpSiftFilter(String args[]) {
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdFilter snpSiftFilter = (SnpSiftCmdFilter) snpSift.cmd();
		return snpSiftFilter.run(true);
	}

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
			for (VcfEffect eff : vcfEntry.getVcfEffects(null)) {
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
			for (VcfEffect eff : vcfEntry.getVcfEffects(null)) {
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
		String args[] = { "filter", "-f", "test/test01.vcf", "-n", expression };
		List<VcfEntry> list = snpSiftFilter(args);

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
		String args[] = { "filter", "-f", "test/test01.vcf", "-p", expression };
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);
			if (vcfEntry.getFilter().equals("PASS")) Assert.assertTrue(vcfEntry.getQuality() >= minQ);
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
		String args[] = { "filter", "-f", "test/test01.vcf", "-a", "ADD", expression };
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);
			if (vcfEntry.getFilter().equals("ADD")) Assert.assertTrue(vcfEntry.getQuality() >= minQ);
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
		String args[] = { "filter", "-f", vcfFile, "-r", "SVM", expression }; // Remove FILTER string 'SVM' from all reference = 'A'
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);
			if (vcfEntry.getFilter().equals("SVM")) Assert.assertTrue(!vcfEntry.getRef().equals("A"));
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
		String args[] = { "filter", "-f", vcfFile, "-n", expression }; // FILTER iNverse
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);

		// Check result (hould be only one entry)
		VcfEntry vcfEntry = list.get(0);
		if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);
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
		String args[] = { "filter", "-f", vcfFile, "--rmFilter", "DP_OK", expression }; // Remove 'PASS' if there is not enough depth
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 3);

		// Check result
		int countOk = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);

			if (vcfEntry.getStart() == 219134261) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				countOk++;
			}
			if (vcfEntry.getStart() == 219134272) {
				Assert.assertEquals("DP_OK;OTHER", vcfEntry.getFilter());
				countOk++;
			}
		}

		Assert.assertEquals("Number of entries checkd does not match expected", countOk, 2);
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
		String args[] = { "filter", "-f", fileName, "exists dbNSFP_SIFT_pred" };
		List<VcfEntry> ves = snpSiftFilter(args);

		Assert.assertEquals(1, ves.size());
	}

	public void test_47() {
		Gpr.debug("Test");
		String fileName = "./test/test46.vcf";
		String args[] = { "-f", fileName, "dbNSFP_SIFT_pred != 'D'" };
		List<VcfEntry> ves = snpSiftFilter(args);
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

	/**
	 * Filter by "(Cases[0] = 3) & (Controls[0] = 0)"
	 * Bug in Field.getReturnType() was causing some trouble.
	 */
	public void test_52() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(Cases[0] = 3) & (Controls[0] = 0)";
		List<VcfEntry> list = snpsiftFilter.filter("test/test52.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) {
			System.out.println("Expression: '" + expression + "'");
			for (VcfEntry vcfEntry : list)
				System.out.println("\t" + vcfEntry);
		}

		// It should select 1 element
		Assert.assertEquals(1, list.size());
	}

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

	/**
	 * Filter: Operator precedence issue
	 */
	public void test_54() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "ANN[*].IMPACT = 'LOW' | ANN[*].IMPACT = 'MODERATE'";
		List<VcfEntry> list = snpsiftFilter.filter("test/test_precedence.vcf", expression, true);

		if (verbose) {
			System.out.println("Expression: '" + expression + "'");
			for (VcfEntry vcfEntry : list)
				if (verbose) System.out.println("VCF entry:\t" + vcfEntry);
		}

		// Check that all lines satisfy the condition
		Assert.assertEquals(7, list.size());
	}

	/**
	 * Test compare a field having 'Number=A' INFO header
	 */
	public void test_55() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "AC <= 1";
		List<VcfEntry> list = snpsiftFilter.filter("test/test55.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals("Number of results expected does not match", 1, list.size());
		if (verbose) Gpr.debug("Result: " + list.get(0));
		Assert.assertEquals("Expected VCF entry does not match (checking POS)", 199, list.get(0).getStart());
	}

	/**
	 * Test compare a field having 'Number=A' INFO header
	 */
	public void test_55_2() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "AC = 1";
		List<VcfEntry> list = snpsiftFilter.filter("test/test55.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals("Number of results expected does not match", 1, list.size());
		if (verbose) Gpr.debug("Result: " + list.get(0));
		Assert.assertEquals("Expected VCF entry does not match (checking POS)", 199, list.get(0).getStart());
	}

	/**
	 * Test compare a field having 'Number=A' INFO header
	 */
	public void test_55_3() {
		Gpr.debug("Test");

		// Filter data
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "AC == 1";
		List<VcfEntry> list = snpsiftFilter.filter("test/test55.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertEquals("Number of results expected does not match", 1, list.size());
		if (verbose) Gpr.debug("Result: " + list.get(0));
		Assert.assertEquals("Expected VCF entry does not match (checking POS)", 199, list.get(0).getStart());
	}

	/**
	 * If header is shown when input file is empty
	 */
	public void test_56_empty_vcf() {
		Gpr.debug("Test");

		// Capture STDOUT to check if header is present
		PrintStream oldOut = System.out;
		String standardOutput = "";
		ByteArrayOutputStream output = new ByteArrayOutputStream(STDOUT_BUFFER_SIZE);
		try {
			// Capture STDOUT
			System.setOut(new PrintStream(output));

			// Run command
			SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
			String expression = "TYPE = 'SNP'"; // Expression doesn't matter here since the input file is empty
			snpsiftFilter.filter("test/empty_with_header.vcf", expression, false);

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			// Get output
			standardOutput = output.toString();

			// Restore old output
			System.setOut(oldOut);
		}

		// Is headeer shown?
		if (verbose) System.out.println("STDOUT:\n----------\n" + standardOutput + "\n----------");
		Assert.assertNotNull(standardOutput);
		Assert.assertFalse(standardOutput.isEmpty());
		Assert.assertTrue(standardOutput.contains("#CHROM\tPOS\tID\tREF\tALT"));
	}

	/**
	 * In AND operators, second expression should not be evaluated if first one is FALSE
	 */
	public void test_57_short_circuit_AND_OR_operators() {
		Gpr.debug("Test");

		// This will throw an exception (division by zero) if short circuit is not correctly implemented.
		SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
		String expression = "(GEN[0067_3_D58].DP > 0) && ((GEN[0067_3_D58].AD[0] / GEN[0067_3_D58].DP) > 0.1)";
		List<VcfEntry> list = snpsiftFilter.filter("test/test57.vcf", expression, true);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
		}
		Assert.assertEquals("Filter results doesn't match the number of expected lines", 29, list.size());
	}

	/**
	 * Remove filter option '-rmFilter'. Check that INFO
	 * field 'FILTER_DELETED' is properly added
	 */
	public void test_58_rmFilter_info_field() {
		Gpr.debug("Test");

		// Filter data
		String expression = "( DP < 5 )";
		String vcfFile = "test/test_rmfilter_2.vcf";
		String args[] = { "-f", vcfFile, "--rmFilter", "DP_OK", expression }; // Remove 'PASS' if there is not enough depth
		List<VcfEntry> list = snpSiftFilter(args);

		// Check that it satisfies the condition
		if (verbose) System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue("List size does not matched expected", list.size() == 4);

		// Check result
		int countOk = 0;
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println(vcfEntry.getFilter() + "\t" + vcfEntry);

			// Filter deleted
			if (vcfEntry.getStart() == 219134261) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				Assert.assertEquals("DP_OK", vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}

			// Nothing done
			if (vcfEntry.getStart() == 219134272) {
				Assert.assertEquals("DP_OK;OTHER", vcfEntry.getFilter());
				Assert.assertEquals(null, vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}

			// Filter deleted + old "FILTER_DELETED" entry kept
			if (vcfEntry.getStart() == 219134298) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				Assert.assertEquals("DELETED_BEFORE,DP_OK", vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}

			// No filter deleted + old "FILTER_DELETED" entry kept
			if (vcfEntry.getStart() == 219134349) {
				Assert.assertEquals("OTHER", vcfEntry.getFilter());
				Assert.assertEquals("DELETED_BEFORE", vcfEntry.getInfo("FILTER_DELETED"));
				countOk++;
			}
		}
		Assert.assertEquals("Number of entries checkd does not match expected", countOk, 4);

	}
}
