package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotate;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false || debug;

	protected String[] defaultExtraArgs;

	public TestCasesZzz() {
		String[] memExtraArgs = { "-sorted" };
		defaultExtraArgs = memExtraArgs;
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

	/**
	 * Annotate issues with discovered info fields (when annotating ALL info fields)
	 */
	public void test_39() {
		Gpr.debug("Test");
		String dbFileName = "./test/db_test_39.vcf";
		String fileName = "./test/annotate_39.vcf";
		String extraArgs[] = {};
		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

		VcfEntry ve = results.get(0);
		if (verbose) System.out.println(ve);
		String infoStr = ve.getInfoStr();

		// Check that trailing CAF annotation is added
		Assert.assertTrue("Missing CAF annotation", infoStr.indexOf("CAF=0.251,0.749") >= 0);
	}

	//	/**
	//	 * Index a VCF file and query all entries
	//	 */
	//	public void test_01() {
	//		Gpr.debug("Test");
	//		String dbFileName = "./test/db_test_index_01.vcf";
	//
	//		// Make sure index file is deleted
	//		String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
	//		(new File(indexFileName)).delete();
	//
	//		// Index VCF file
	//		VcfIndex vcfIndex = new VcfIndex(dbFileName);
	//		vcfIndex.setVerbose(verbose);
	//		vcfIndex.index();
	//		vcfIndex.open();
	//
	//		// Check that all entries can be found & retrieved
	//		if (verbose) Gpr.debug("Checking");
	//		VcfFileIterator vcf = new VcfFileIterator(dbFileName);
	//		for (VcfEntry ve : vcf) {
	//			if (verbose) System.out.println(ve.toStr());
	//
	//			// Query database
	//			Markers results = vcfIndex.query(ve);
	//
	//			// We should find at least one result
	//			Assert.assertTrue("No results found for entry:\n\t" + ve, results.size() > 0);
	//
	//			// Check each result
	//			for (Marker res : results) {
	//				MarkerFile resmf = (MarkerFile) res;
	//				VcfEntry veIdx = vcfIndex.read(resmf);
	//				if (verbose) System.out.println("\t" + res + "\t" + veIdx);
	//
	//				// Check that result does intersect query
	//				Assert.assertTrue("Selected interval does not intersect marker form file!" //
	//						+ "\n\tVcfEntry: " + ve //
	//						+ "\n\tResult: " + res //
	//						+ "\n\tVcfEntry from result:" + veIdx//
	//						, ve.intersects(veIdx) //
	//				);
	//			}
	//		}
	//
	//		vcfIndex.close();
	//	}
	//
	//	/**
	//	 * Index a VCF file and query all entries
	//	 */
	//	public void test_02() {
	//		Gpr.debug("Test");
	//		String dbFileName = "./test/db_test_index_02.vcf";
	//
	//		// Index VCF file
	//		String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
	//		(new File(indexFileName)).delete();
	//
	//		// Create index file
	//		VcfIndex vcfIndex = new VcfIndex(dbFileName);
	//		vcfIndex.setVerbose(verbose);
	//		vcfIndex.index();
	//
	//		// Make sure index file was created
	//		Assert.assertTrue("Index file '" + indexFileName + "' does not exist", Gpr.exists(indexFileName));
	//
	//		// Restart so we force to read from index file
	//		vcfIndex = new VcfIndex(dbFileName);
	//		vcfIndex.setVerbose(verbose);
	//		vcfIndex.index();
	//		vcfIndex.open();
	//
	//		// Check that all entries can be found & retrieved
	//		if (verbose) Gpr.debug("Checking");
	//		VcfFileIterator vcf = new VcfFileIterator(dbFileName);
	//		for (VcfEntry ve : vcf) {
	//			if (verbose) System.out.println(ve.toStr());
	//
	//			// Query database
	//			Markers results = vcfIndex.query(ve);
	//
	//			// We should find at least one result
	//			Assert.assertTrue("No results found for entry:\n\t" + ve, results.size() > 0);
	//
	//			// Check each result
	//			for (Marker res : results) {
	//				MarkerFile resmf = (MarkerFile) res;
	//				VcfEntry veIdx = vcfIndex.read(resmf);
	//				if (verbose) System.out.println("\t" + res + "\t" + veIdx);
	//
	//				// Check that result does intersect query
	//				Assert.assertTrue("Selected interval does not intersect marker form file!" //
	//						+ "\n\tVcfEntry: " + ve //
	//						+ "\n\tResult: " + res //
	//						+ "\n\tVcfEntry from result:" + veIdx//
	//						, ve.intersects(veIdx) //
	//				);
	//			}
	//		}
	//
	//		vcfIndex.close();
	//	}

}