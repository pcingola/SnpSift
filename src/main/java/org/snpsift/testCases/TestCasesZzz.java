package org.snpsift.testCases;

import java.util.List;

import org.junit.Assert;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdCaseControl;

import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	public TestCasesZzz() {
	}

	void checkCaseControlString(String vcfFile, String geoupStr, String casesStr, String controlStr) {
		String args[] = { geoupStr, vcfFile };
		SnpSiftCmdCaseControl cmd = new SnpSiftCmdCaseControl(args);

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve);
			Assert.assertEquals(casesStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CASE));
			Assert.assertEquals(controlStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CONTROL));
		}
	}

	void checkCaseControlTfam(String vcfFile, String tfamFile, String casesStr, String controlStr) {
		String args[] = { "caseControl", "-tfam", tfamFile, vcfFile };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdCaseControl cmd = (SnpSiftCmdCaseControl) snpSift.cmd();

		List<VcfEntry> vcfEntries = cmd.run(true);
		for (VcfEntry ve : vcfEntries) {
			if (verbose) System.out.println(ve);
			Assert.assertEquals(casesStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CASE));
			Assert.assertEquals(controlStr, ve.getInfo(SnpSiftCmdCaseControl.VCF_INFO_CONTROL));
		}
	}

	public void test_01() {
		Gpr.debug("Test");
		checkCaseControlTfam("test/test.private.01.vcf", "test/test.private.01.tfam", "0,0,0", "0,0,0");
	}

}
