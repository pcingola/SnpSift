package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.epistasis.SnpSiftCmdEpistasis;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * SnpSift 'gt' test cases
 * 
 * @author pcingola
 */
public class TestCasesEpistasis extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true;

	public void test_01() {
		String tfam = "test/epistasis_01.tfam";
		String vcf = "test/epistasis_01.vcf";
		String args[] = { "-model", "LD", "-minAc", "0", tfam, vcf };
		SnpSiftCmdEpistasis snpSiftCmdEpistasis = new SnpSiftCmdEpistasis(args);
		snpSiftCmdEpistasis.setVerbose(verbose);
		snpSiftCmdEpistasis.load();

		long dist = snpSiftCmdEpistasis.distance(0, 1);
		Assert.assertEquals(1000, dist);

		dist = snpSiftCmdEpistasis.distance(1, 2);
		Assert.assertEquals(Long.MAX_VALUE, dist);

	}

	public void test_02() {
		String tfam = "test/epistasis_01.tfam";
		String vcf = "test/epistasis_02.vcf";
		String args[] = { "-model", "LD", "-minAc", "0", "-maxP", "1.0", tfam, vcf };
		SnpSiftCmdEpistasis snpSiftCmdEpistasis = new SnpSiftCmdEpistasis(args);
		snpSiftCmdEpistasis.setVerbose(verbose);
		snpSiftCmdEpistasis.load();
		snpSiftCmdEpistasis.setDebug(debug);

		// Expect high p-value here
		double pvalue = snpSiftCmdEpistasis.pValueLd(0, 1);
		Assert.assertTrue(pvalue > 0.5);
	}

	public void test_03() {
		String tfam = "test/epistasis_01.tfam";
		String vcf = "test/epistasis_03.vcf";
		String args[] = { "-model", "LD", "-minAc", "0", "-maxP", "1.0", tfam, vcf };
		SnpSiftCmdEpistasis snpSiftCmdEpistasis = new SnpSiftCmdEpistasis(args);
		snpSiftCmdEpistasis.setVerbose(verbose);
		snpSiftCmdEpistasis.load();
		snpSiftCmdEpistasis.setDebug(debug);

		// Expect small p-value here
		double pvalue = snpSiftCmdEpistasis.pValueLd(0, 1);
		Gpr.debug("pvalue: " + pvalue);
		Assert.assertTrue(pvalue < 1e-22);
	}

}
