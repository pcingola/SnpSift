package org.snpsift.testCases;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.snpeff.snpEffect.commandLine.CommandLine;

import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	protected String[] defaultExtraArgs;
	protected boolean deleteIndexFile;
	public final int BUFFER_SIZE = 10 * 1024 * 1024;

	public TestCasesZzz() {
		String[] memExtraArgs = { "-sorted" };
		defaultExtraArgs = memExtraArgs;
	}

	public String command(CommandLine command) {
		PrintStream oldOut = System.out;
		String standardOutput = "";
		ByteArrayOutputStream output = new ByteArrayOutputStream(BUFFER_SIZE);
		try {
			// Capture STDOUT
			System.setOut(new PrintStream(output));

			// Run command
			command.run();

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			// Get output
			standardOutput = output.toString();

			// Restore old output
			System.setOut(oldOut);
		}

		return standardOutput;
	}

	//	/**
	//	 * If header is shown when input file is empty
	//	 */
	//	public void test_56_empty_vcf() {
	//		Gpr.debug("Test");
	//
	//		// Capture STDOUT to check if header is present
	//		PrintStream oldOut = System.out;
	//		String standardOutput = "";
	//		ByteArrayOutputStream output = new ByteArrayOutputStream(BUFFER_SIZE);
	//		try {
	//			// Capture STDOUT
	//			System.setOut(new PrintStream(output));
	//
	//			// Run command
	//			SnpSiftCmdFilter snpsiftFilter = new SnpSiftCmdFilter();
	//			String expression = "TYPE = 'SNP'"; // Expression doesn't matter here since the input file is empty
	//			snpsiftFilter.filter("test/empty_with_header.vcf", expression, false);
	//
	//		} catch (Throwable t) {
	//			t.printStackTrace();
	//		} finally {
	//			// Get output
	//			standardOutput = output.toString();
	//
	//			// Restore old output
	//			System.setOut(oldOut);
	//		}
	//
	//		// Is headeer shown?
	//		if (verbose) System.out.println("STDOUT:\n----------\n" + standardOutput + "\n----------");
	//		Assert.assertNotNull(standardOutput);
	//		Assert.assertFalse(standardOutput.isEmpty());
	//		Assert.assertTrue(standardOutput.contains("#CHROM\tPOS\tID\tREF\tALT"));
	//	}

}
