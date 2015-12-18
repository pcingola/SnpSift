package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdExtractFields;
import ca.mcgill.mcb.pcingola.util.Gpr;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = true;
	public static boolean verbose = true || debug;

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
	 * Extract fields form a file and check that the line matches
	 */
	void extractAndCheck(String vcfFileName, String fieldExpression, String expected[]) {
		List<String> linesList = extract(vcfFileName, fieldExpression);
		Assert.assertEquals("Results length does not match", expected.length, linesList.size());

		int i = 0;
		for (String line : linesList) {
			Assert.assertEquals("Result numnber " + i + " does not match (expression: '" + fieldExpression + "').", expected[i], line);
			i++;
		}
	}

	public void test_27() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD[0]", "16");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD[1]", "2");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD", "16,2");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_29() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP[0]", "0.005");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP[1]", "0.095");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP", "0.005,0.095");
	}

}
