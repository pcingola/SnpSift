package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotateSorted;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate test case
 * 
 * @author pcingola
 */
public class TestCasesAnnotate extends TestCase {

	public static boolean verbose = false;

	/**
	 * Re-annotate a file and check that the new annotation matches the previous one
	 */
	public void annotateTest(String dbFileName, String fileName) {
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { dbFileName, fileName };

		// Iterate over VCF entries
		try {
			SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
			vcfAnnotate.setSuppressOutput(true);
			vcfAnnotate.initAnnotate();

			VcfFileIterator vcfFile = new VcfFileIterator(fileName);
			for (VcfEntry vcf : vcfFile) {
				vcfAnnotate.annotate(vcf);

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

			vcfAnnotate.endAnnotate();
		} catch (IOException e) {
			throw new RuntimeException(e);
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
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entrie and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcf = vcfFile.next();
		vcfAnnotate.annotate(vcf);

		// Check
		//		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;RSPOS=16346045;AF=0.002;OBS=4,1,1636,2011,3,1,6780,9441;IOD=0.000;AOI=-410.122;AOZ=-399.575;ABE=0.678;ABZ=47.762;AN=488", vcf.getInfoStr());
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;ABZ=47.762;AF=0.002;AN=488;AOI=-410.122;AOZ=-399.575;IOD=0.000;OBS=4,1,1636,2011,3,1,6780,9441;RSPOS=16346045", vcf.getInfoStr());
	}

	/**
	 * Annotate only some info fields
	 * @throws IOException 
	 */
	public void test_07() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-info", "AF,AN,ABE", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entrie and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcf = vcfFile.next();
		vcfAnnotate.annotate(vcf);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;AF=0.002;AN=488;ABE=0.678", vcf.getInfoStr());
	}

	/**
	 * Do not annotate ID column
	 * @throws IOException 
	 */
	public void test_08() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-noId", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entry and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcfEntry = vcfFile.next();
		vcfAnnotate.annotate(vcfEntry);

		// Check that new ID was NOT added
		Assert.assertEquals("OLD_ID", vcfEntry.getId());
	}

	/**
	 * Annotate only some info fields
	 * @throws IOException 
	 */
	public void test_09() throws IOException {
		String dbFileName = "./test/db_test_09.vcf";
		String fileName = "./test/annotate_09.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-info", "GMAF,AC", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.setSaveOutput(true);
		vcfAnnotate.run();

		// Make sure output header for "GMAF" is present.
		// Also make sure implicit headers are not (e.g. AC)
		String out = vcfAnnotate.getOutput();
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
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-info", "GMAF,AC", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.setSaveOutput(true);
		vcfAnnotate.run();

		// Make sure output header for "GMAF" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		String out = vcfAnnotate.getOutput();
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
	 * 
	 * @throws IOException 
	 */
	public void test_12() throws IOException {
		String dbFileName = "./test/db_test_12.vcf";
		String fileName = "./test/annotate_12.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-noAlt", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entry and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcfEntry = vcfFile.next();
		vcfAnnotate.annotate(vcfEntry);
		Gpr.debug(vcfEntry);

		// Check that new ID was NOT added
		Assert.assertEquals("NEW_ID", vcfEntry.getId());
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
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { "-info", "AA", dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.setSaveOutput(true);
		vcfAnnotate.run();

		// Make sure output header for "AA" is present ONLY ONCE.
		// Also make sure implicit headers are not (e.g. AC)
		String out = vcfAnnotate.getOutput();
		System.out.println(out);
		int hasAa = 0;
		for (String line : out.split("\n"))
			if (line.startsWith("##INFO=<ID=AA")) hasAa++;

		Assert.assertEquals(1, hasAa);
	}

	public void test_14() {
		String dbFileName = "./test/annotate_multiple_allele.db.vcf";
		String fileName = "./test/annotate_multiple_allele.1.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		List<VcfEntry> list = vcfAnnotate.run(true);

		// Check results
		VcfEntry ve = list.get(0);
		System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("2", allNum);
	}

	public void test_15() {
		String dbFileName = "./test/annotate_multiple_allele_R.db.vcf";
		String fileName = "./test/annotate_multiple_allele.2.vcf";
		System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = { dbFileName, fileName };

		// Get SnpSift ready
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		vcfAnnotate.setSuppressOutput(true);
		List<VcfEntry> list = vcfAnnotate.run(true);

		// Check results
		VcfEntry ve = list.get(0);
		System.out.println(ve);
		String allNum = ve.getInfo("ALL_NUM");
		Assert.assertEquals("value_C", allNum);
	}

	public void test_16() {
		String dbFileName = "./test/db_test_16.vcf";
		String fileName = "./test/annotate_16.vcf";
		String infoName = "PREPEND_";
		String args[] = { "-name", infoName, dbFileName, fileName };

		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		List<VcfEntry> ves = vcfAnnotate.run(true);

		VcfEntry ve = ves.get(0);
		String aa = ve.getInfo(infoName + "AA");
		String bb = ve.getInfo(infoName + "BB");
		Assert.assertEquals("Field_Value", aa);
		Assert.assertEquals("AnotherValue", bb);
	}

}
