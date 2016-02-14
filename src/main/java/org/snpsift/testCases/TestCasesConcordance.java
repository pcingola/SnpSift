package org.snpsift.testCases;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.snpeff.stats.CountByType;
import org.snpeff.util.Gpr;
import org.snpsift.SnpSiftCmdConcordance;

/**
 * Concordance test cases
 *
 * @author pcingola
 */
public class TestCasesConcordance extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	SnpSiftCmdConcordance checkConcordance(String refVcfFile, String vcfFile) {
		if (verbose) System.err.println("\n\nConcordance between: " + refVcfFile + "\t" + vcfFile);
		String args[] = { refVcfFile, vcfFile };

		SnpSiftCmdConcordance ssconc = new SnpSiftCmdConcordance(args);
		ssconc.setVerbose(verbose);
		ssconc.setSuppressOutput(!verbose);
		ssconc.setDebug(debug);
		ssconc.setWriteBySampleFile(false);
		ssconc.setWriteSummaryFile(false);
		ssconc.run();

		CountByType concordance = ssconc.getConcordance();
		if (verbose) System.out.println("\n\nConcordance:\n" + concordance);

		return ssconc;
	}

	/**
	 * Check that a all values match
	 */
	void checkConcordance(String refVcfFile, String vcfFile, CountByType count) {
		SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
		CountByType concordance = ssconc.getConcordance();

		for (String key : count.keysSorted()) {
			if (verbose) System.out.println("Checking\t'" + key + "'\tExpected: " + count.get(key) + "\tActual: " + concordance.get(key));
			Assert.assertEquals(count.get(key), concordance.get(key));
		}
	}

	/**
	 * Check that a single value matches
	 */
	void checkConcordance(String refVcfFile, String vcfFile, String key, int value) {
		SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
		CountByType concordance = ssconc.getConcordance();

		if (verbose) System.out.println("Checking\t'" + key + "'\tExpected: " + value + "\tActual: " + concordance.get(key));
		Assert.assertEquals(value, concordance.get(key));
	}

	public void test_01() {
		Gpr.debug("Test");
		checkConcordance("test/concordance_ref_01.vcf", "test/concordance_test_01.vcf", "ALT_2/ALT_2", 1);
	}

	public void test_02() {
		Gpr.debug("Test");
		checkConcordance("test/concordance_ref_02.vcf", "test/concordance_test_02.vcf", "ALT_2/ALT_2", 2);
	}

	public void test_03() {
		Gpr.debug("Test");
		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 1);
		count.inc("ALT_2/REF", 1);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_03", 1);

		checkConcordance("test/concordance_ref_03.vcf", "test/concordance_test_03.vcf", count);
	}

	public void test_04() {
		Gpr.debug("Test");
		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 1);
		count.inc("ALT_2/REF", 1);
		count.inc("ALT_2/MISSING_GT_concordance_test_04", 1);

		checkConcordance("test/concordance_ref_04.vcf", "test/concordance_test_04.vcf", count);
	}

	public void test_05() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 3);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_05", 1);

		checkConcordance("test/concordance_ref_05.vcf", "test/concordance_test_05.vcf", count);
	}

	public void test_06() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 5);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_06", 1);
		count.inc("MISSING_ENTRY_concordance_ref_06/ALT_2", 1);

		checkConcordance("test/concordance_ref_06.vcf", "test/concordance_test_06.vcf", count);
	}

	public void test_07() {
		Gpr.debug("Test");
		checkConcordance("test/concordance_ref_07.vcf", "test/concordance_test_07.vcf", "ALT_2/ALT_2", 1);
	}

	public void test_08() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 10);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_08", 10);

		checkConcordance("test/concordance_ref_08.vcf", "test/concordance_test_08.vcf", count);
	}

	public void test_09() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 15);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_09", 15);

		checkConcordance("test/concordance_ref_09.vcf", "test/concordance_test_09.vcf", count);
	}

	public void test_10() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 15);
		count.inc("MISSING_ENTRY_concordance_ref_10/ALT_2", 15);

		checkConcordance("test/concordance_ref_10.vcf", "test/concordance_test_10.vcf", count);
	}

	public void test_11() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ALT_1/ALT_1", 4);
		count.inc("ALT_1/MISSING_ENTRY_concordance_test_11", 4);
		count.inc("ALT_2/ALT_2", 7);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_11", 5);
		count.inc("ALT_2/MISSING_GT_concordance_test_11", 1);
		count.inc("MISSING_ENTRY_concordance_ref_11/ALT_2", 1);
		count.inc("MISSING_ENTRY_concordance_ref_11/MISSING_GT_concordance_test_11", 39);
		count.inc("MISSING_ENTRY_concordance_ref_11/REF", 1086);

		checkConcordance("test/concordance_ref_11.vcf", "test/concordance_test_11.vcf", count);
	}

	public void test_12() {
		Gpr.debug("Test");

		CountByType count = new CountByType();
		count.inc("ERROR", 1);

		checkConcordance("test/concordance_ref_12.vcf", "test/concordance_test_12.vcf", count);
	}
}
