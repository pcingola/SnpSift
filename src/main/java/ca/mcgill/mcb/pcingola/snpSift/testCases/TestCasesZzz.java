package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotate;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	/**
	 * Annotate
	 */
	public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		ArrayList<String> argsList = new ArrayList<String>();
		if (extraArgs != null) {
			for (String arg : extraArgs)
				argsList.add(arg);
		}
		argsList.add(fileName);
		String args[] = argsList.toArray(new String[0]);

		// Iterate over VCF entries
		SnpSiftCmdAnnotate snpSiftAnnotate = new SnpSiftCmdAnnotate(args);
		snpSiftAnnotate.setDbFileName(dbFileName);
		snpSiftAnnotate.setDebug(debug);
		snpSiftAnnotate.setVerbose(verbose);
		List<VcfEntry> results = snpSiftAnnotate.run(true);

		// Check
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);
		return results;
	}

	public void annotateTest(String dbFileName, String fileName) {
		annotateTest(dbFileName, fileName, null);
	}

	/**
	 * Annotate a file and check that the new annotation matches the expected one
	 */
	public void annotateTest(String dbFileName, String fileName, String[] extraArgs) {
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		// Check each entry
		for (VcfEntry vcf : results) {
			// We expect the same annotation twice
			String idstr = vcf.getId();

			// Get expected IDs
			String expectedIds = vcf.getInfo("EXP_IDS");
			if (expectedIds != null) {
				expectedIds = expectedIds.replace('|', ';');
				if (expectedIds.equals(".")) expectedIds = "";

				// Compare
				Assert.assertEquals(expectedIds, idstr);
			} else fail("EXP_IDS (expected ids) INFO field missing in " + fileName + ", entry:\n" + vcf);
		}
	}

	public void test_16() {
		String dbFileName = "./test/db_test_16.vcf";
		String fileName = "./test/annotate_16.vcf";
		String infoName = "PREPEND_";
		String extraArgs[] = { "-name", infoName };

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		VcfEntry ve = results.get(0);
		String aa = ve.getInfo(infoName + "AA");
		String bb = ve.getInfo(infoName + "BB");
		Assert.assertEquals("Field_Value", aa);
		Assert.assertEquals("AnotherValue", bb);
	}

}
