package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdConcordance;
import ca.mcgill.mcb.pcingola.stats.CountByType;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

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

}
