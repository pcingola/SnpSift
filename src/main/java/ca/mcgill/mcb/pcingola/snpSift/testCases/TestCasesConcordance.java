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

	void checkConcordance(String refVcfFile, String vcfFile, String key, int value) {
		SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
		CountByType concordance = ssconc.getConcordance();

		System.out.println("Checking\t'" + key + "' = " + value);
		Assert.assertEquals(value, concordance.get(key));
	}

	public void test_01() {
		checkConcordance("test/concordance_ref_01.vcf", "test/concordance_test_01.vcf", "ALT_2/ALT_2", 1);
	}

	public void test_02() {
		checkConcordance("test/concordance_ref_02.vcf", "test/concordance_test_02.vcf", "ALT_2/ALT_2", 2);
	}

}
