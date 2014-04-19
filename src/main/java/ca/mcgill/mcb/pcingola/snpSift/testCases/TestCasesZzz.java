package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.HashMap;
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

	public void test_17() {
		String dbFileName = "./test/annotate_oder_db.vcf";
		String fileName = "./test/annotate_oder_snp.vcf";
		String args[] = { dbFileName, fileName };

		// Fake annotations (in files)
		HashMap<String, String> types = new HashMap<String, String>();
		types.put("0", "zero");
		types.put("1", "first");
		types.put("2", "second");

		// Annotate
		SnpSiftCmdAnnotateSorted vcfAnnotate = new SnpSiftCmdAnnotateSorted(args);
		List<VcfEntry> ves = vcfAnnotate.run(true);

		// Check that our "fake annotations" are matched as expected
		for (VcfEntry ve : ves) {
			String type = ve.getInfo("TYPE");
			String ann = ve.getInfo("ANN");

			System.out.println(ve.toStr() + "\t" + type + "\t" + ann);
			Assert.assertEquals(types.get(type), ann);
		}
	}

}
