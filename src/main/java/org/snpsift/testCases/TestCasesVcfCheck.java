package org.snpsift.testCases;

import org.snpeff.util.Gpr;
import org.snpsift.SnpSift;

import junit.framework.TestCase;

/**
 * VarType test cases
 * 
 * @author pcingola
 */
public class TestCasesVcfCheck extends TestCase {

	public static boolean verbose = false;

	public void test_01() {
		Gpr.debug("Test");
		String vcfFile = "test/test_vcfCheck_01.vcf";
		String args[] = { "vcfCheck", vcfFile };
		SnpSift snpSift = new SnpSift(args);
		snpSift.run();
	}

}
