package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdAnnotateSorted;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 * 
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean verbose = false;
	public static boolean debug = false;

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
