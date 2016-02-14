package org.snpsift.testCases;

import org.snpsift.VcfLd;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Linkage disequilibrium test cases
 * 
 * @author pcingola
 */
public class TestCasesLd extends TestCase {

	public static boolean verbose = false;

	/**
	 * Calculate LD parameters
	 * Example from "Principles of population genetics", pages 79,80
	 */
	public void test_01() {
		VcfLd vcfLd = new VcfLd();

		int countAB = 474;
		int countAb = 611;
		int countaB = 142;
		int countab = 773;

		int countA = 1085;
		int counta = 915;
		int countB = 616;
		int countb = 1384;

		// Calculate
		vcfLd.setCount(countAB, countAb, countaB, countab, countA, counta, countB, countb);
		vcfLd.ld();

		// Compare results
		double epsilon = 0.00001;
		Assert.assertEquals(0.06991, vcfLd.getD(), epsilon);
		Assert.assertEquals(0.14091, vcfLd.getDmax(), epsilon);
		Assert.assertEquals(0.4961322830175289, vcfLd.getDprime(), epsilon);
		Assert.assertEquals(0.09239127328988425, vcfLd.getrSquare(), epsilon);
	}
}
