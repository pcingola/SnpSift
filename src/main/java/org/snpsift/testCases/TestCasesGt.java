package org.snpsift.testCases;

import java.util.ArrayList;

import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdGt;

import junit.framework.Assert;
import junit.framework.TestCase;

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
		Log.debug("Test");

		String file = "test/gt_test.01.vcf";
		String fileGt = "test/gt_test.01.gt.vcf";

		//---
		// Run compress command
		//---
		String args[] = { "gt", file };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdGt cmd = (SnpSiftCmdGt) snpSift.cmd();
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
		String argsUn[] = { "gt", "-u", fileGt };
		snpSift = new SnpSift(argsUn);
		SnpSiftCmdGt cmdUn = (SnpSiftCmdGt) snpSift.cmd();
		cmdUn.setVerbose(verbose);
		cmdUn.setDebug(debug);
		cmdUn.setSaveOutput(true);
		cmdUn.run();
		String outputUn = cmdUn.getOutput();

		// Read all non-header lines form original file
		ArrayList<String> ori = new ArrayList<>();
		for (String line : Gpr.readFile(file).split("\n"))
			if (!line.startsWith("#")) ori.add(line);

		// Get all non-header lines form uncompressed file
		ArrayList<String> un = new ArrayList<>();
		for (String line : outputUn.split("\n"))
			if (!line.startsWith("#")) un.add(line);

		// Compare original to uncompressed
		for (int i = 0; i < ori.size(); i++) {
			// We expect no differences.
			Assert.assertTrue("Line " + i + " differs:\n\t'" + ori.get(i) + "'\n\t'" + un.get(i) + "'", ori.get(i).equals(un.get(i)));
		}
	}
}
