package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdFilterChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Test cases for SnpSift filterChrPos
 *
 * @author pcingola
 */
public class TestCasesFilterChrPos extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	protected String[] defaultExtraArgs = null;

	protected String[] argsList(String chrPosFileName, String vcfFileName, String[] extraArgs) {
		ArrayList<String> argsList = new ArrayList<String>();

		if (defaultExtraArgs != null) {
			for (String arg : defaultExtraArgs)
				argsList.add(arg);
		}

		if (extraArgs != null) {
			for (String arg : extraArgs)
				argsList.add(arg);
		}

		argsList.add(chrPosFileName);
		argsList.add(vcfFileName);
		return argsList.toArray(new String[0]);
	}

	public void filter(String dbFileName, String fileName, int expectedCount) {
		filter(dbFileName, fileName, null, expectedCount);
	}

	/**
	 * Filter
	 */
	public List<VcfEntry> filter(String chrPosFileName, String vcfFileName, String[] extraArgs, int expectedCount) {
		System.out.println("Annotate: " + chrPosFileName + "\t" + vcfFileName);

		// Create command line
		String args[] = argsList(chrPosFileName, vcfFileName, extraArgs);

		// Iterate over VCF entries
		SnpSiftCmdFilterChrPos snpSiftCmd = new SnpSiftCmdFilterChrPos(args);
		snpSiftCmd.setDebug(debug);
		snpSiftCmd.setVerbose(verbose);
		List<VcfEntry> results = snpSiftCmd.run(true);

		// Check
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);
		return results;
	}

	/**
	 * Chromosomes in VCF file are called 'chr22' instead of '22'.
	 * This should work OK as well.
	 */
	public void test_01() {
		String chrPosFileName = "./test/filterChrPos_01.txt";
		String fileName = "./test/filterChrPos_01.vcf";
		int expectedCount = 10;
		filter(chrPosFileName, fileName, expectedCount);
	}

}
