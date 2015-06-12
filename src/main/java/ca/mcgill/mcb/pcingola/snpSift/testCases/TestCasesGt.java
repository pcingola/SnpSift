package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdGt;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * SnpSift 'gt' test cases
 *
 * @author pcingola
 */
public class TestCasesGt extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false;

	/**
	 */
	public void test_01() {
		Gpr.debug("Test");

		String file = "test/gt_test.01.vcf";
		String fileGt = "test/gt_test.01.gt.vcf";

		//---
		// Run compress command
		//---
		String args[] = { file };
		SnpSiftCmdGt cmd = new SnpSiftCmdGt(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);
		cmd.setSaveOutput(true);
		cmd.run();

		// Save output to tmp file
		String outputGt = cmd.getOutput();
		Gpr.toFile(fileGt, outputGt);

		//---
		// Run uncompress command
		//---
		String argsUn[] = { "-u", fileGt };
		SnpSiftCmdGt cmdUn = new SnpSiftCmdGt(argsUn);
		cmdUn.setVerbose(verbose);
		cmdUn.setDebug(debug);
		cmdUn.setSaveOutput(true);
		cmdUn.run();
		String outputUn = cmdUn.getOutput();

		//---
		// Compare original and 'uncompressed'
		//---

		// Read all non-header lines form original file
		ArrayList<String> ori = new ArrayList<String>();
		for (String line : Gpr.readFile(file).split("\n"))
			if (!line.startsWith("#")) ori.add(line);

		// Get all non-header lines form uncompressed file
		ArrayList<String> un = new ArrayList<String>();
		for (String line : outputUn.split("\n"))
			if (!line.startsWith("#")) un.add(line);

		// Compare original to uncompressed
		boolean diff = false;
		for (int i = 0; i < ori.size(); i++) {
			if (!ori.get(i).equals(un.get(i))) {
				System.err.println("Line " + i + " differs:\n\t'" + ori.get(i) + "'\n\t'" + un.get(i) + "'");
				diff = true;
				Assert.assertEquals(false, diff); // We expect no differences.
			}
		}
	}
}
