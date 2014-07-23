package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdConcordance;
import ca.mcgill.mcb.pcingola.stats.CountByType;

/**
 * Concordance test cases
 *
 * @author pcingola
 */
public class TestCasesConcordance extends TestCase {

	public static boolean debug = true;
	public static boolean verbose = false || debug;

	SnpSiftCmdConcordance checkConcordance(String refVcfFile, String vcfFile) {
		String args[] = { refVcfFile, vcfFile };

		SnpSiftCmdConcordance ssconc = new SnpSiftCmdConcordance(args);
		ssconc.setVerbose(verbose);
		ssconc.setDebug(debug);
		ssconc.run();

		CountByType concordance = ssconc.getConcordance();
		if (debug) System.err.println("Concordance:\n" + concordance);

		return ssconc;
	}

	/**
	 * Check that a all values match
	 */
	void checkConcordance(String refVcfFile, String vcfFile, CountByType count) {
		SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
		CountByType concordance = ssconc.getConcordance();

		for (String key : count.keysSorted()) {
			System.out.println("Checking\t'" + key + "'\tExpected: " + count.get(key) + "\tActual: " + concordance.get(key));
			Assert.assertEquals(count.get(key), concordance.get(key));
		}
	}

	/**
	 * Check that a single value matches
	 */
	void checkConcordance(String refVcfFile, String vcfFile, String key, int value) {
		SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
		CountByType concordance = ssconc.getConcordance();

		System.out.println("Checking\t'" + key + "'\tExpected: " + value + "\tActual: " + concordance.get(key));
		Assert.assertEquals(value, concordance.get(key));
	}

	//	public void test_01() {
	//		checkConcordance("test/concordance_ref_01.vcf", "test/concordance_test_01.vcf", "ALT_2/ALT_2", 1);
	//	}
	//
	//	public void test_02() {
	//		checkConcordance("test/concordance_ref_02.vcf", "test/concordance_test_02.vcf", "ALT_2/ALT_2", 2);
	//	}

	//	public void test_03() {
	//		CountByType count = new CountByType();
	//		count.inc("ALT_2/ALT_2", 1);
	//		count.inc("ALT_2/REF", 1);
	//		count.inc("ALT_2/MISSING_ENTRY_concordance_test_03", 1);
	//
	//		checkConcordance("test/concordance_ref_03.vcf", "test/concordance_test_03.vcf", count);
	//	}

	//	public void test_04() {
	//		CountByType count = new CountByType();
	//		count.inc("ALT_2/ALT_2", 1);
	//		count.inc("ALT_2/REF", 1);
	//		count.inc("ALT_2/MISSING_ENTRY_concordance_test_04", 1);
	//		count.inc("ALT_2/MISSING_GT_concordance_test_04", 1);
	//
	//		checkConcordance("test/concordance_ref_04.vcf", "test/concordance_test_04.vcf", count);
	//	}

	//	public void test_05() {
	//		CountByType count = new CountByType();
	//		count.inc("ALT_2/ALT_2", 3);
	//		count.inc("ALT_2/MISSING_ENTRY_concordance_test_05", 1);
	//
	//		checkConcordance("test/concordance_ref_05.vcf", "test/concordance_test_05.vcf", count);
	//	}

	public void test_06() {
		CountByType count = new CountByType();
		count.inc("ALT_2/ALT_2", 5);
		count.inc("ALT_2/MISSING_ENTRY_concordance_test_06", 1);
		count.inc("MISSING_ENTRY_concordance_ref_06/ALT_2", 1);

		checkConcordance("test/concordance_ref_06.vcf", "test/concordance_test_06.vcf", count);
	}

}
