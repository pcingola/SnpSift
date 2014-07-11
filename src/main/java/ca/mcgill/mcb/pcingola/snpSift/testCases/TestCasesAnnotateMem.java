package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesAnnotateMem extends TestCasesAnnotate {

	public TestCasesAnnotateMem() {
		String[] memExtraArgs = { "-mem" };
		defaultExtraArgs = memExtraArgs;
	}

	public void test_101() {
		String dbFileName = "./test/db_test_1.vcf";
		String fileName = "./test/annotate_1.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_102() {
		String dbFileName = "./test/db_test_10.vcf";
		String fileName = "./test/annotate_10.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_103() {
		String dbFileName = "./test/db_test_2.vcf";
		String fileName = "./test/annotate_2.vcf";
		annotateTest(dbFileName, fileName);
	}

	public void test_104() {
		String dbFileName = "./test/db_test_large.vcf";
		String fileName = "./test/annotate_large.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Chromosomes in VCF file are called 'chr22' instead of '22'.
	 * This should work OK as well.
	 */
	public void test_105() {
		String dbFileName = "./test/db_test_chr22.vcf";
		String fileName = "./test/test_chr22.vcf";
		annotateTest(dbFileName, fileName);
	}

	/**
	 * Annotate info fields
	 */
	public void test_106() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		List<VcfEntry> results = annotate(dbFileName, fileName, null);

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes" + ";ABE=0.678" + ";ABZ=47.762" + ";AF=0.002" + ";AN=488" + ";AOI=-410.122" + ";AOZ=-399.575" + ";IOD=0.000" + ";OBS=4,1,1636,2011,3,1,6780,9441" + ";RSPOS=16346045", results.get(0).getInfoStr());
	}

	/**
	 * Annotate only some info fields
	 */
	public void test_107() throws IOException {
		String dbFileName = "./test/db_test_06.vcf";
		String fileName = "./test/annotate_06.vcf";
		String extraArgs[] = { "-info", "AF,AN,ABE" };

		List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs); //

		// Check
		Assert.assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;AF=0.002;AN=488", results.get(0).getInfoStr());
	}

}
