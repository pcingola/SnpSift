package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdPrivate;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
		String vcfFile = "test/test.private.01.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, false);
	}

	/**
	 * Non private
	 */
	public void test_02() {
		String vcfFile = "test/test.private.02.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, false);
	}

	/**
	 * Private variant
	 */
	public void test_03() {
		String vcfFile = "test/test.private.03.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, true);
	}

	/**
	 * Private variant
	 */
	public void test_04() {
		String vcfFile = "test/test.private.04.vcf";
		String tfamFile = "test/test.private.01.tfam";
		checkPrivate(vcfFile, tfamFile, true);
	}

	public void test_05() {
		checkPrivate("test/test.private.05.vcf", "test/test.private.05.tfam", false);
	}

	public void test_06() {
		checkPrivate("test/test.private.06.vcf", "test/test.private.05.tfam", true);
	}

	public void test_07() {
		checkPrivate("test/test.private.07.vcf", "test/test.private.05.tfam", true);
	}

	public void test_08() {
		checkPrivate("test/test.private.08.vcf", "test/test.private.05.tfam", false);
	}

}
