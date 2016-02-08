package org.snpsift.snpSift.testCases;

import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.snpSift.SnpSiftCmdPrivate;

import junit.framework.TestCase;

/**
 * SnpSift 'private' test cases
 *
 * @author pcingola
 */
public class TestCasesPrivate extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false;

	void checkPrivate(String vcfFile, String tfamFile, boolean isPrivate) {
		String args[] = { tfamFile, vcfFile };
		SnpSiftCmdPrivate cmd = new SnpSiftCmdPrivate(args);

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve);

			if (!isPrivate && (ve.getInfo(VcfEntry.VCF_INFO_PRIVATE) != null)) throw new RuntimeException("This should not be a 'private' variant!");
			if (isPrivate && (ve.getInfo(VcfEntry.VCF_INFO_PRIVATE) == null)) throw new RuntimeException("This should be a 'private' variant!");
		}
	}

	/**
	 * Non private
	 */
	public void test_01() {
		Gpr.debug("Test");
		String vcfFile = "test/test.private.01.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, false);
	}

	/**
	 * Non private
	 */
	public void test_02() {
		Gpr.debug("Test");
		String vcfFile = "test/test.private.02.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, false);
	}

	/**
	 * Private variant
	 */
	public void test_03() {
		Gpr.debug("Test");
		String vcfFile = "test/test.private.03.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, true);
	}

	/**
	 * Private variant
	 */
	public void test_04() {
		Gpr.debug("Test");
		String vcfFile = "test/test.private.04.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, true);
	}

	public void test_05() {
		Gpr.debug("Test");
		checkPrivate("test/test.private.05.vcf", "test/test.private.05.tfam", false);
	}

	public void test_06() {
		Gpr.debug("Test");
		checkPrivate("test/test.private.06.vcf", "test/test.private.05.tfam", true);
	}

	public void test_07() {
		Gpr.debug("Test");
		checkPrivate("test/test.private.07.vcf", "test/test.private.05.tfam", true);
	}

	public void test_08() {
		Gpr.debug("Test");
		checkPrivate("test/test.private.08.vcf", "test/test.private.05.tfam", false);
	}

}
