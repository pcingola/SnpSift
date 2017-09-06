package org.snpsift.testCases;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdAnnotate;

import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;
	protected String[] defaultExtraArgs;
	protected boolean deleteIndexFile;

	public TestCasesZzz() {
		String[] memExtraArgs = { "-sorted" };
		defaultExtraArgs = memExtraArgs;
	}

	/**
	 * Annotate
	 */
	public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		if (deleteIndexFile) deleteIndexFile(dbFileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);

		// Create command
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdAnnotate snpSiftAnnotate = (SnpSiftCmdAnnotate) snpSift.cmd();

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

		if (deleteIndexFile) deleteIndexFile(dbFileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);

		// Create command
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdAnnotate snpSiftAnnotate = (SnpSiftCmdAnnotate) snpSift.cmd();

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
				expectedIds = expectedIds.replace(',', ';');

				if (expectedIds.equals(".")) expectedIds = "";

				// Compare
				Assert.assertEquals(expectedIds, idstr);
			} else fail("EXP_IDS (expected ids) INFO field missing in " + fileName + ", entry:\n" + vcf);
		}
	}

	protected String[] argsList(String dbFileName, String fileName, String[] extraArgs) {
		ArrayList<String> argsList = new ArrayList<>();

		argsList.add("annotate");

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

	void deleteIndexFile(String dbFileName) {
		String indexFile = dbFileName + ".sidx";
		File f = new File(indexFile);
		if (f.delete()) {
			if (verbose) Gpr.debug("Index file '" + indexFile + "' deleted before annotation test");
		}
	}

	/**
	 * Annotate GnomAd with multiple entries
	 */
	public void test_44() {
		Gpr.debug("Test");
		String dbFileName = "./test/ann/gnomad_db_multiple_entries.vcf";
		String fileName = "./test/ann/annotate_44.vcf";
		String extraArgs[] = { "-info", "AF_AFR" };
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		// Get first entry
		VcfEntry ve = results.get(0);
		if (verbose) System.out.println(ve);
		String infoStr = ve.getInfoStr();

		// Check that annotation is added
		Assert.assertTrue("Missing AF_AFR annotation", infoStr.indexOf("AF_AFR=") >= 0);

		// Compare against expected output
		// Note: We don't care about annotation order in this case
		String expectedAfAfr = "6.60044e-03";
		String afAfr = ve.getInfo("AF_AFR");
		Assert.assertEquals("Expecting empty field", expectedAfAfr, afAfr);
	}

}
