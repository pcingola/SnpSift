package ca.mcgill.mcb.pcingola.snpSift.testCases;

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
	 * Re-annotate a file and check that the new annotation matches the previous one
	 */
	public void annotateTest(String dbFileName, String fileName) {
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { fileName };

		// Iterate over VCF entries
		SnpSiftCmdAnnotate vcfAnnotate = new SnpSiftCmdAnnotate(args);
		vcfAnnotate.setDbFileName(dbFileName);
		vcfAnnotate.setDebug(debug);
		vcfAnnotate.setVerbose(verbose);
		List<VcfEntry> results = vcfAnnotate.run(true);

		// Check
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);

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

	public void test_02() {
		String dbFileName = "./test/db_test_10.vcf";
		String fileName = "./test/annotate_10.vcf";
		annotateTest(dbFileName, fileName);
	}

}
