package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdExtractFields;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	protected String[] defaultExtraArgs = null;

	/**
	 * Extract fields and return the output lines
	 */
	List<String> extract(String vcfFileName, String fieldExpression) {
		String args[] = { vcfFileName, fieldExpression };
		SnpSiftCmdExtractFields ssef = new SnpSiftCmdExtractFields(args);

		List<String> linesList = ssef.run(true);

		if (debug) {
			for (String line : linesList)
				Gpr.debug(line);
		}

		return linesList;
	}

	/**
	 * Extract fields form a file and check that the line matches (only one line expected from the file)
	 */
	void extractAndCheck(String vcfFileName, String fieldExpression, String expected) {
		List<String> linesList = extract(vcfFileName, fieldExpression);
		if (linesList.size() != 1) throw new RuntimeException("Only one line expected");
		Assert.assertEquals(expected, linesList.get(0));
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_31() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_31.vcf", "EFF[*].AA", "c.*568C>A");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_31_ann() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_31.ann.vcf", "ANN[*].CODON", "c.*568C>A");
	}

}
