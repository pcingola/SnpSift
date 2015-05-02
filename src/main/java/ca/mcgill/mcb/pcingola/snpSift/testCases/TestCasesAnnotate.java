package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotate;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesAnnotate extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	protected String[] defaultExtraArgs = null;

	public TestCasesAnnotate() {
	}

	/**
	 * Annotate
	 */
	public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);

		// Iterate over VCF entries
		SnpSiftCmdAnnotate snpSiftAnnotate = new SnpSiftCmdAnnotate(args);
		snpSiftAnnotate.setDebug(debug);
		snpSiftAnnotate.setVerbose(verbose);
		snpSiftAnnotate.setSuppressOutput(!verbose);
		List<VcfEntry> results = snpSiftAnnotate.run(true);

		// Check
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);
		return results;
	}

	/**
	 * Annotate and return STDOUT as a string
	 */
	public String annotateOut(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);

		// Iterate over VCF entries
		SnpSiftCmdAnnotate snpSift = new SnpSiftCmdAnnotate(args);
		snpSift.setDebug(debug);
		snpSift.setVerbose(verbose);
		snpSift.setSaveOutput(true);
		snpSift.run();

		// Check
		return snpSift.getOutput();
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

	protected String[] argsList(String dbFileName, String fileName, String[] extraArgs) {
		ArrayList<String> argsList = new ArrayList<String>();

		if (defaultExtraArgs != null) {
			for (String arg : defaultExtraArgs)
				argsList.add(arg);
		}

		if (extraArgs != null) {
			for (String arg : extraArgs)
				argsList.add(arg);
		}

		argsList.add(dbFileName);
		argsList.add(fileName);
		return argsList.toArray(new String[0]);
	}

	public void test_01() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_1.vcf";
		String fileName = "./test/annotate_1.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_02() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_10.vcf";
		String fileName = "./test/annotate_10.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_03() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_2.vcf";
		String fileName = "./test/annotate_2.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_04() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_large.vcf";
		String fileName = "./test/annotate_large.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Chromosomes in VCF file are called 'chr22' instead of '22'.
	 * This should work OK as well.
	 */
	public void test_05() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_chr22.vcf";
		String fileName = "./test/test_chr22.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Annotate info fields
	 */
	public void test_06() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;ABZ=47.762;AF=0.002;AN=488;AOI=-410.122;AOZ=-399.575;IOD=0.000;OBS=4,1,1636,2011,3,1,6780,9441;RSPOS=16346045", results.get(0).getInfoStr());
	}

	/**
	 * Annotate only some info fields
	 */
	public void test_07() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		String extraArgs[] = { "-info", "AF,AN,ABE" };
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;AF=0.002;AN=488", results.get(0).getInfoStr());
	}

	/**
	 * Do not annotate ID column
	 */
	public void test_08() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		String extraArgs[] = { "-noId" };
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);
		Assert.assertEquals("OLD_ID", results.get(0).getId());
	}

	/**
	 * Annotate only some info fields
	 */
	public void test_09() {
		Gpr.debug("Test");
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
	public void test_11() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_11.vcf";
		String fileName = "./test/annotate_11.vcf";
		String extraArgs[] = { "-info", "GMAF,AC" };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "GMAF" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		if (verbose) System.out.println(out);
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
	public void test_12() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_12.vcf";
		String fileName = "./test/annotate_12.vcf";
		String extraArgs[] = { "-noAlt" };

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
	public void test_13() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_13.vcf";
		String fileName = "./test/annotate_13.vcf";
		String extraArgs[] = { "-info", "AA" };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "AA" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		if (verbose) System.out.println(out);
		int hasAa = 0;
		for (String line : out.split("\n"))
			if (line.startsWith("##INFO=<ID=AA")) hasAa++;

		Assert.assertEquals(1, hasAa);
	}

	public void test_14() {
		Gpr.debug("Test");
		String dbFileName = "./test/annotate_multiple_allele.db.vcf";
		String fileName = "./test/annotate_multiple_allele.1.vcf";

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check results
		VcfEntry ve = results.get(0);
		if (verbose) System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("2", allNum);
	}

	public void test_15() {
		Gpr.debug("Test");
		String dbFileName = "./test/annotate_multiple_allele_R.db.vcf";
		String fileName = "./test/annotate_multiple_allele.2.vcf";

		// Annotate
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check results
		VcfEntry ve = results.get(0);
		if (verbose) System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("value_REF,value_C", allNum);
	}

	public void test_16() {
		Gpr.debug("Test");
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
		Gpr.debug("Test");
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

			if (verbose) System.out.println(ve.toStr() + "\t" + type + "\t" + ann);
			Assert.assertEquals(types.get(type), ann);
		}
	}

	public void test_18() {
		Gpr.debug("Test");
		String dbFileName = "./test/test_annotate_18_db.vcf";
		String fileName = "./test/test_annotate_18.vcf";

		List<VcfEntry> results = annotate(dbFileName, fileName, null);
		VcfEntry ve = results.get(0);
		String ukac = ve.getInfo("UK10KWES_AC");
		if (verbose) System.out.println("Annotated value: " + ukac);
		Assert.assertEquals(".,49,44,.,.,.,.,.,.,.,.", ukac);
	}

	public void test_19() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_multiline.vcf";
		String fileName = "./test/annotate_multiline.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Annotate info fields
	 */
	public void test_20() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_20.vcf";
		String fileName = "./test/annotate_20.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check
		Assert.assertEquals("44,49", results.get(0).getInfo("ANNOTATE_ONCE"));
	}

	/**
	 * Annotate two consecutive variants in the same position
	 */
	public void test_21() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_21.vcf";
		String fileName = "./test/annotate_21.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Third entry is the one not being annotated

		// Check third entry
		VcfEntry ve = results.get(2);
		String ann = ve.getInfo("clinvar_db");
		if (debug) Gpr.debug("Annotation: '" + ann + "'");
		Assert.assertNotNull(ann);

		// Check second entry
		ve = results.get(1);
		ann = ve.getInfo("clinvar_db");
		if (debug) Gpr.debug("Annotation: '" + ann + "'");
		Assert.assertNotNull(ann);

	}

	/**
	 * Annotate first base in a chromosome
	 */
	public void test_22() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_2.vcf";
		String fileName = "./test/annotate_22.vcf";
		annotate(dbFileName, fileName, null);
		// We simply check that no exception was thrown
	}

	/**
	 * Database has one entry and VCF has multiple ALTs
	 */
	public void test_23_allele_specific_annotation_missing_R() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_23.vcf";
		String fileName = "./test/annotate_23.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check results
		VcfEntry ve = results.get(0);
		String caf = ve.getInfo("CAF");
		if (verbose) System.out.println(ve + "\n\tCAF: " + caf);
		Assert.assertEquals("0.9642,.,0.03581", caf);

	}

	/**
	 * Annotate using "-name" to prepend a name to VCF fields (e.g. "AA" -> "PREPEND_AA")
	 * Header should be added changing the ID accordingly
	 */
	public void test_24() {
		Gpr.debug("Test");

		String dbFileName = "./test/db_test_24.vcf";
		String fileName = "./test/annotate_24.vcf";
		String infoName = "PREPEND_";
		String extraArgs[] = { "-name", infoName };

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "PREPEND_AA"  and "PREPEND_BB" are present ONLY ONCE.
		if (verbose) System.out.println(out);
		int hasAa = 0, hasBb = 0, hasCc = 0;
		for (String line : out.split("\n")) {
			if (line.startsWith("##INFO=<ID=PREPEND_AA")) hasAa++;
			if (line.startsWith("##INFO=<ID=PREPEND_BB")) hasBb++;
			if (line.startsWith("##INFO=<ID=PREPEND_CC")) hasCc++;
		}

		Assert.assertEquals(1, hasAa);
		Assert.assertEquals(1, hasBb);
		Assert.assertEquals(1, hasCc);
	}

	/**
	 * Annotate using "-name" to prepend a name to VCF fields (e.g. "AA" -> "PREPEND_AA")
	 * Header should be added changing the ID accordingly
	 */
	public void test_25() {
		Gpr.debug("Test");

		String dbFileName = "./test/db_test_24.vcf";
		String fileName = "./test/annotate_24.vcf";
		String infoName = "PREPEND_";
		String extraArgs[] = { "-name", infoName, "-info", "AA,BB" }; // Note: We don't include 'CC' annotation

		// Annotate
		String out = annotateOut(dbFileName, fileName, extraArgs);

		// Make sure output header for "PREPEND_AA"  and "PREPEND_BB" are present ONLY ONCE.
		if (verbose) System.out.println(out);
		int hasAa = 0, hasBb = 0, hasCc = 0;
		for (String line : out.split("\n")) {
			if (line.startsWith("##INFO=<ID=PREPEND_AA")) hasAa++;
			if (line.startsWith("##INFO=<ID=PREPEND_BB")) hasBb++;
			if (line.startsWith("##INFO=<ID=PREPEND_CC")) hasCc++;
		}

		Assert.assertEquals(1, hasAa);
		Assert.assertEquals(1, hasBb);
		Assert.assertEquals(0, hasCc); // This onw should NOT be present
	}

	/**
	 * Annotate ID using entries that are duplicated in db.vcf
	 */
	public void test_26_repeat_db_entry() {
		Gpr.debug("Test");

		String dbFileName = "./test/db_test_26.vcf";
		String fileName = "./test/annotate_26.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Annotate INFO fields using entries that are duplicated in db.vcf
	 */
	public void test_27_repeat_db_entry() {
		Gpr.debug("Test");

		String dbFileName = "./test/db_test_27.vcf";
		String fileName = "./test/annotate_27.vcf";
		List<VcfEntry> res = annotate(dbFileName, fileName, null);
		for (VcfEntry ve : res) {
			if (verbose) System.out.println(ve);
			Assert.assertEquals("121964859,45578238", ve.getInfo("RS"));
		}
	}

	/**
	 * Annotate if a VCF entry exists in the database file
	 */
	public void test_28_exists() {
		Gpr.debug("Test");

		String dbFileName = "./test/db_test_28.vcf";
		String fileName = "./test/annotate_28.vcf";
		String args[] = { "-exists", "EXISTS" };

		List<VcfEntry> res = annotate(dbFileName, fileName, args);
		for (VcfEntry ve : res) {
			if (verbose) System.out.println(ve);

			// Check
			if (ve.getStart() == 201331098) Assert.assertTrue("Existing VCF entry has not been annotated", ve.hasInfo("EXISTS"));
			else Assert.assertFalse("Non-existing VCF entry has been annotated", ve.hasInfo("EXISTS"));
		}
	}

}
