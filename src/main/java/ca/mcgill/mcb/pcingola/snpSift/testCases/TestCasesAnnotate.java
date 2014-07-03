package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotate;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesAnnotate extends TestCase {

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

	public String annotateOut(String dbFileName, String fileName, String[] extraArgs) {
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
		snpSiftAnnotate.setSaveOutput(true);
		snpSiftAnnotate.run();

		// Check
		return snpSiftAnnotate.getOutput();
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

	public void test_01() {
		String dbFileName = "./test/db_test_1.vcf";
		String fileName = "./test/annotate_1.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_02() {
		String dbFileName = "./test/db_test_10.vcf";
		String fileName = "./test/annotate_10.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_03() {
		String dbFileName = "./test/db_test_2.vcf";
		String fileName = "./test/annotate_2.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_04() {
		String dbFileName = "./test/db_test_large.vcf";
		String fileName = "./test/annotate_large.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Chromosomes in VCF file are called 'chr22' instead of '22'.
	 * This should work OK as well.
	 */
	public void test_05() {
		String dbFileName = "./test/db_test_chr22.vcf";
		String fileName = "./test/test_chr22.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Annotate info fields
	 * @throws IOException
	 */
	public void test_06() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;ABZ=47.762;AF=0.002;AN=488;AOI=-410.122;AOZ=-399.575;IOD=0.000;OBS=4,1,1636,2011,3,1,6780,9441;RSPOS=16346045", results.get(0).getInfoStr());
	}

	/**
	 * Annotate only some info fields
	 */
	public void test_07() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		String extraArgs[] = { "-info", "AF,AN,ABE" };
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;AF=0.002;AN=488;ABE=0.678", results.get(0).getInfoStr());
	}

	/**
	 * Do not annotate ID column
	 */
	public void test_08() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		String extraArgs[] = { "-noId" };
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);
		Assert.assertEquals("OLD_ID", results.get(0).getId());
	}

	/**
	 * Annotate only some info fields
	 */
	public void test_09() throws IOException {
		String dbFileName = "./test/db_test_09.vcf";
		String fileName = "./test/annotate_09.vcf";
		String extraArgs[] = { "-info", "GMAF,AC" };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "GMAF" is present.
		// Also make sure implicit headers are not (e.g. AC)
		boolean hasGmaf = false;
		boolean hasAc = false;
		for (String line : out.split("\n")) {
			hasGmaf |= line.startsWith("##INFO=<ID=GMAF");
			hasAc |= line.startsWith("##INFO=<ID=AC");
		}

		Assert.assertEquals(true, hasGmaf);
		Assert.assertEquals(false, hasAc);
	}

	/**
	 * Annotate only some info fields
	 * @throws IOException
	 */
	public void test_11() throws IOException {
		String dbFileName = "./test/db_test_11.vcf";
		String fileName = "./test/annotate_11.vcf";
		String extraArgs[] = { "-info", "GMAF,AC" };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "GMAF" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		System.out.println(out);
		int hasGmaf = 0;
		int hasAc = 0;
		for (String line : out.split("\n")) {
			if (line.startsWith("##INFO=<ID=GMAF")) hasGmaf++;
			if (line.startsWith("##INFO=<ID=AC")) hasAc++;
		}

		Assert.assertEquals(1, hasGmaf);
		Assert.assertEquals(0, hasAc);
	}

	/**
	 * Annotate without REF/ALT fields
	 */
	public void test_12() throws IOException {
		String dbFileName = "./test/db_test_12.vcf";
		String fileName = "./test/annotate_12.vcf";
		String extraArgs[] = { "-noAlt", dbFileName, fileName };

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		// Check that new ID was NOT added
		Assert.assertEquals("NEW_ID", results.get(0).getId());
	}

	/**
	 * Annotate using "reserved" VCF fields (e.g. "AA" )
	 * Header should be added if it doesn't exits.
	 *
	 * @throws IOException
	 */
	public void test_13() throws IOException {
		String dbFileName = "./test/db_test_13.vcf";
		String fileName = "./test/annotate_13.vcf";
		String extraArgs[] = { "-info", "AA", dbFileName, fileName };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "AA" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		System.out.println(out);
		int hasAa = 0;
		for (String line : out.split("\n"))
			if (line.startsWith("##INFO=<ID=AA")) hasAa++;

		Assert.assertEquals(1, hasAa);
	}

	public void test_14() {
		String dbFileName = "./test/annotate_multiple_allele.db.vcf";
		String fileName = "./test/annotate_multiple_allele.1.vcf";

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check results
		VcfEntry ve = results.get(0);
		System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("2", allNum);
	}

	public void test_15() {
		String dbFileName = "./test/annotate_multiple_allele_R.db.vcf";
		String fileName = "./test/annotate_multiple_allele.2.vcf";

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check results
		VcfEntry ve = results.get(0);
		System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("value_C", allNum);
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

	public void test_17() {
		String dbFileName = "./test/annotate_oder_db.vcf";
		String fileName = "./test/annotate_oder_snp.vcf";

		// Fake annotations (in files)
		HashMap<String, String> types = new HashMap<String, String>();
		types.put("0", "zero");
		types.put("1", "first");
		types.put("2", "second");

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check that our "fake annotations" are matched as expected
		for (VcfEntry ve : results) {
			String type = ve.getInfo("TYPE");
			String ann = ve.getInfo("ANN");

			System.out.println(ve.toStr() + "\t" + type + "\t" + ann);
			Assert.assertEquals(types.get(type), ann);
		}
	}

}
