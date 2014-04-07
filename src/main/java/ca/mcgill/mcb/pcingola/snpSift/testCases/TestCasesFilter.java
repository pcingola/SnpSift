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
		double minQ = 50;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "QUAL >= " + minQ;
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(CHROM = '19')";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int minPos = 20175;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS > " + minPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int minPos = 20175;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int maxPos = 20175;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS < " + maxPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int maxPos = 20175;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS <= " + maxPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int minPos = 20175;
		int maxPos = 35549;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ") & (POS <= " + maxPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		int minPos = 20175;
		int maxPos = 35549;

		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(POS >= " + minPos + ") | (POS <= " + maxPos + ")";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( CHROM =~ 'NT_' )";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( REF = 'C' ) & ( ALT = 'T') ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( DP >= 5 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( exists INDEL ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);
			Assert.assertNotNull(vcfEntry.getInfo("INDEL"));
		}
	}

	/**
	 * Filter by INDEL info tag
	 */
	public void test_13() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( exists INDEL ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( GEN[0].PL[1] > 10 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

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

	/**
	 * Filter by GT genottype tag
	 */
	public void test_15() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( GEN[0].GT = '1/1' ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( isHom ( GEN[0] ) ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(0);
			Assert.assertTrue(vcfGenotype.isHomozygous());
		}
	}

	/**
	 * Filter by GT genottype functions
	 */
	public void test_17() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( isHet ( GEN[0] ) ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( isRef ( GEN[0] ) ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( isVariant ( GEN[0] ) ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "isVariant ( GEN[0] ) & isHom( GEN[0] ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "isVariant ( GEN[1] ) & isHom( GEN[1] ) & isRef( GEN[2] )";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		vcfFilter.addSet("test/set_rs_test01.txt");
		vcfFilter.addSet("test/set_rs_test02.txt");
		String expression = "ID in SET[1]";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String id = vcfEntry.getId();
			Assert.assertTrue(id.equals("rs58108140") //
					|| id.equals("rs71262674") //
					|| id.equals("rs71262673"));
		}
	}

	/**
	 * Filter by GT[*] (any genottype)
	 */
	public void test_23() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "GEN[*].GT = '1|1'";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			for (VcfGenotype gen : vcfEntry)
				any |= (gen.getGenotype()[0] == 1) && (gen.getGenotype()[1] == 1);

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Filter by GT[0].VV[*] (any sub field in a genottype)
	 */
	public void test_24() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "GEN[0].AP[*] > 0.8";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "GEN[*].AP[*] > 0.95";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "EFF[0].EFFECT = 'SYNONYMOUS_CODING'";
		List<VcfEntry> list = vcfFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			String eff = vcfEntry.getInfo("EFF").split("\\(")[0];
			Assert.assertEquals(eff, "SYNONYMOUS_CODING");
		}
	}

	/**
	 * Filter by EFF[*].EFFECT (any effect)
	 */
	public void test_27() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].EFFECT = 'SYNONYMOUS_CODING'";
		List<VcfEntry> list = vcfFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
	}

	/**
	 * Test countHom function
	 */
	public void test_28() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( countHom() = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (gen.isHomozygous()) count++;
			}

			Assert.assertTrue(count == 3);
		}
	}

	/**
	 * Test countHet function
	 */
	public void test_29() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( countHet() = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( countRef() = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			int count = 0;
			for (VcfGenotype gen : vcfEntry) {
				if (!gen.isVariant()) count++;
			}

			Assert.assertTrue(count == 3);
		}
	}

	/**
	 * Test countVariant function
	 */
	public void test_31() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( countVariant() = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test02.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].CODING = 'CODING'";
		List<VcfEntry> list = vcfFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
	 * Filter by EFF[*].CODING 
	 */
	public void test_33() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].CODING = 'NON_CODING'";
		List<VcfEntry> list = vcfFilter.filter("test/test03.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
	 * Filter by EFF[ALL].CODING 
	 */
	public void test_34() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "(EFF[ALL].EFFECT = 'DOWNSTREAM')";
		List<VcfEntry> list = vcfFilter.filter("test/downstream.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
	 * Filter by EFF[*].GENE 
	 */
	public void test_35() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "EFF[*].GENE = 'BICD1'";
		List<VcfEntry> list = vcfFilter.filter("test/test_gene.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		for (VcfEntry vcfEntry : list) {
			if (verbose) System.out.println("\t" + vcfEntry);

			boolean any = false;
			for (VcfEffect eff : vcfEntry.parseEffects(null)) {
				Assert.assertEquals("BICD1", eff.getGene());
				any = true;
			}

			Assert.assertEquals(true, any);
		}
	}

	/**
	 * Inverse of a filter
	 */
	public void test_36() {
		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-n", expression };
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		double minQ = 50;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-p", expression };
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		double minQ = 50;
		verbose = true;

		// Filter data
		String expression = "QUAL >= " + minQ;
		String args[] = { "-f", "test/test01.vcf", "-a", "ADD", expression };
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter("test/test01.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		verbose = true;

		// Filter data
		String expression = "REF = 'A'";
		String vcfFile = "test/downstream.vcf";
		String args[] = { "-f", vcfFile, "-r", "SVM", expression }; // Remove FILTER string 'SVM' from all reference = 'A'
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		verbose = true;

		// Filter data
		String expression = "( EFF[*].EFFECT = 'SPLICE_SITE_ACCEPTOR' )";
		String vcfFile = "test/test_jim.vcf";
		String args[] = { "-f", vcfFile, "-n", expression }; // FILTER iNverse 
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		verbose = true;

		// Filter data
		String expression = "( DP < 5 )";
		String vcfFile = "test/test_rmfilter.vcf";
		String args[] = { "-f", vcfFile, "--rmFilter", "DP_OK", expression }; // Remove 'PASS' if there is not enough depth  
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> list = vcfFilter.filter(vcfFile, expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
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
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ = 3 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

	/**
	 * Test compare to missing field
	 */
	public void test_43() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ < 0 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

	/**
	 * Test compare to missing field
	 */
	public void test_44() {
		// Filter data
		SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
		String expression = "( ZZZ > 0 ) ";
		List<VcfEntry> list = vcfFilter.filter("test/test42.vcf", expression, true);

		// Check that it satisfies the condition
		System.out.println("Expression: '" + expression + "'");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
	}

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

	public void test_46() {
		String fileName = "./test/test46.vcf";
		String args[] = { "-f", fileName, "exists dbNSFP_SIFT_pred" };

		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> ves = snpSiftFilter.run(true);

		Assert.assertEquals(1, ves.size());
	}

	public void test_47() {
		String fileName = "./test/test46.vcf";
		String args[] = { "-f", fileName, "dbNSFP_SIFT_pred != 'D'" };

		SnpSiftCmdFilter snpSiftFilter = new SnpSiftCmdFilter(args);
		List<VcfEntry> ves = snpSiftFilter.run(true);

		Assert.assertEquals(1, ves.size());
		String field = ves.get(0).getInfo("dbNSFP_SIFT_pred");
		Assert.assertEquals("T", field);
	}

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
