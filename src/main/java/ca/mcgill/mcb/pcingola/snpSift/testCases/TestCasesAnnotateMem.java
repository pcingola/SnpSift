package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotateMem;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate test case
 * 
 * @author pcingola
 */
public class TestCasesAnnotateMem extends TestCase {

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
			SnpSiftCmdAnnotateMem vcfAnnotate = new SnpSiftCmdAnnotateMem(args);
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
		SnpSiftCmdAnnotateMem vcfAnnotate = new SnpSiftCmdAnnotateMem(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entrie and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcf = vcfFile.next();
		vcfAnnotate.annotate(vcf);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;RSPOS=16346045;AF=0.002;OBS=4,1,1636,2011,3,1,6780,9441;IOD=0.000;AOI=-410.122;AOZ=-399.575;ABE=0.678;ABZ=47.762;AN=488", vcf.getInfoStr());
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
		SnpSiftCmdAnnotateMem vcfAnnotate = new SnpSiftCmdAnnotateMem(args);
		vcfAnnotate.setSuppressOutput(true);
		vcfAnnotate.initAnnotate();

		// Get first VCF entrie and annotate it
		VcfFileIterator vcfFile = new VcfFileIterator(fileName);
		VcfEntry vcf = vcfFile.next();
		vcfAnnotate.annotate(vcf);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;AF=0.002;AN=488;ABE=0.678", vcf.getInfoStr());
	}

}
