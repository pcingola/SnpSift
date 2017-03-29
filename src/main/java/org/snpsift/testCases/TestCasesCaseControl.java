package org.snpsift.testCases;

import java.util.List;

import org.junit.Assert;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.caseControl.SnpSiftCmdCaseControl;

import junit.framework.TestCase;

/**
 * Case control test cases
 *
 * @author pcingola
 */
public class TestCasesCaseControl extends TestCase {

	public static boolean verbose = false;
	public static boolean debug = false;

	void checkCaseControlString(String vcfFile, String geoupStr, String casesStr, String controlStr) {
		String args[] = { "caseControl", geoupStr, vcfFile };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdCaseControl cmd = (SnpSiftCmdCaseControl) snpSift.cmd();

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve);
			Assert.assertEquals(casesStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CASE));
			Assert.assertEquals(controlStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CONTROL));
		}
	}

	void checkCaseControlTfam(String vcfFile, String tfamFile, String casesStr, String controlStr) {
		String args[] = { "caseControl", "-tfam", tfamFile, vcfFile };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdCaseControl cmd = (SnpSiftCmdCaseControl) snpSift.cmd();

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve);
			Assert.assertEquals(casesStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CASE));
			Assert.assertEquals(controlStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CONTROL));
		}
	}

	public void test_01() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.01.vcf", "test/test.private.01.tfam", "0,0,0", "0,0,0");
	}

	public void test_01_Str() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.01.vcf", "--", "0,0,0", "0,0,0");
	}

	public void test_02() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.02.vcf", "test/test.private.01.tfam", "0,0,0", "2,0,4");
	}

	public void test_02_Str() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.02.vcf", "--", "0,0,0", "2,0,4");
	}

	public void test_02_Str2() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.02.vcf", "-+", "1,0,2", "1,0,2");
	}

	public void test_03() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.03.vcf", "test/test.private.01.tfam", "0,0,0", "0,1,1");
	}

	public void test_03_Str() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.03.vcf", "--", "0,0,0", "0,1,1");
	}

	public void test_03_Str2() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.03.vcf", "+-", "0,1,1", "0,0,0");
	}

	public void test_03_Str3() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.03.vcf", "0+", "0,0,0", "0,0,0");
	}

	public void test_04() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.04.vcf", "test/test.private.01.tfam", "0,0,0", "1,0,2");
	}

	public void test_04_Str() {
		Gpr.debug("Test");
		checkCaseControlString("test/test.private.04.vcf", "--", "0,0,0", "1,0,2");
	}

	public void test_05() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.05.vcf", "test/test.private.05.tfam", "0,0,0", "0,0,0");
	}

	public void test_06() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.06.vcf", "test/test.private.05.tfam", "0,1,1", "0,0,0");
	}

	public void test_07() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.07.vcf", "test/test.private.05.tfam", "0,1,1", "0,1,1");
	}

	public void test_08() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.08.vcf", "test/test.private.05.tfam", "0,2,2", "0,1,1");
	}

	/**
	 * Compare to results from PLINK using ChiSquare approximation
	 */
	public void test_09() {
		Gpr.debug("Test");
		String vcfFile = "test/caseContorlStudies.vcf";
		String tfamFile = "test/caseContorlStudies.tfam";
		double maxDiff = 0.01;

		String args[] = { "caseControl", "-chi2", "-tfam", tfamFile, vcfFile };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdCaseControl cmd = (SnpSiftCmdCaseControl) snpSift.cmd();

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve.toStringNoGt());

			// PLINK's value
			double pDom = ve.getInfoFloat("P_DOM");
			double pAllelic = ve.getInfoFloat("P_ALLELIC");
			double pRec = ve.getInfoFloat("P_REC");
			double pTrend = ve.getInfoFloat("P_TREND");
			double pGeno = ve.getInfoFloat("P_GENO");

			// Calculated values
			double pDomCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_DOM);
			double pAllelicCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_ALL);
			double pRecCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_REC);
			double pTrendCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_TREND);
			double pGenoCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_GENO);

			if (pAllelic > 0) {
				double ratio = pAllelic / pAllelicCc;
				if (verbose) System.out.println("\tAllelic \tRatio: " + ratio + "\tPLINK: " + pAllelic + "\tCalculated: " + pAllelicCc + "\t");
				Assert.assertEquals(1.0, ratio, maxDiff);
			}

			if (pDom > 0) {
				double ratio = pDom / pDomCc;
				if (verbose) System.out.println("\tDominant\tRatio: " + ratio + "\tPLINK: " + pDom + "\tCalculated: " + pDomCc + "\t");
				Assert.assertEquals(1.0, ratio, maxDiff);
			}

			if (pRec > 0) {
				double ratio = pRec / pRecCc;
				if (verbose) System.out.println("\tRecessive\tRatio: " + ratio + "\tPLINK: " + pRec + "\tCalculated: " + pRecCc + "\t");
				Assert.assertEquals(1.0, ratio, maxDiff);
			}

			if (pTrend > 0) {
				double ratio = pTrend / pTrendCc;
				if (verbose) System.out.println("\tTrend    \tRatio: " + ratio + "\tPLINK: " + pTrend + "\tCalculated: " + pTrendCc + "\t");
				Assert.assertEquals(1.0, ratio, maxDiff);
			}

			if (pGeno > 0) {
				double ratio = pGeno / pGenoCc;
				if (verbose) System.out.println("\tGenotypic\tRatio: " + ratio + "\tPLINK: " + pGeno + "\tCalculated: " + pGenoCc + "\t");
				Assert.assertEquals(1.0, ratio, maxDiff);
			}

			if (verbose) System.out.println("");
		}
	}

	/**
	 * Compare to results from PLINK using Fisher exact test
	 *
	 * Note: I use the rule of the thumb that Fisher exact
	 * 		 test should be below 2 times Chi^2 estimation.
	 * 		 This rule seems to work, at least for p-values in
	 *       this example, which are between 10^-15 and 0.9
	 *
	 */
	public void test_10() {
		Gpr.debug("Test");
		String vcfFile = "test/caseContorlStudies.vcf";
		String tfamFile = "test/caseContorlStudies.tfam";

		String args[] = { "caseControl", "-tfam", tfamFile, vcfFile };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdCaseControl cmd = (SnpSiftCmdCaseControl) snpSift.cmd();

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve.toStringNoGt());

			// PLINK's value
			double pDom = ve.getInfoFloat("P_DOM");
			double pAllelic = ve.getInfoFloat("P_ALLELIC");
			double pRec = ve.getInfoFloat("P_REC");

			// Calculated values
			double pDomCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_DOM);
			double pAllelicCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_ALL);
			double pRecCc = ve.getInfoFloat(SnpSiftCmdCaseControl.VCF_INFO_CC_REC);

			if (pAllelic > 0) {
				double ratio = pAllelic / pAllelicCc;
				if (verbose) System.out.println("\tAllelic \tRatio: " + ratio + "\tPLINK: " + pAllelic + "\tCalculated: " + pAllelicCc + "\t");
				Assert.assertTrue(ratio < 2.0);
				Assert.assertTrue(ratio >= 1.0);
			}

			if (pDom > 0) {
				double ratio = pDom / pDomCc;
				if (verbose) System.out.println("\tDominant\tRatio: " + ratio + "\tPLINK: " + pDom + "\tCalculated: " + pDomCc + "\t");
				Assert.assertTrue(ratio < 2.0);
				Assert.assertTrue(ratio >= 1.0);
			}

			if (pRec > 0) {
				double ratio = pRec / pRecCc;
				if (verbose) System.out.println("\tRecessive\tRatio: " + ratio + "\tPLINK: " + pRec + "\tCalculated: " + pRecCc + "\t");
				Assert.assertTrue(ratio < 2.0);
				Assert.assertTrue(ratio >= 1.0);
			}

			if (verbose) System.out.println("");
		}
	}
}
