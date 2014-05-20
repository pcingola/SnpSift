package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdDbNsfp;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	public void test_08() {
		String vcfFileName = "test/test_dbNSFP_8.vcf";
		String args[] = { "-collapse", "-a", "-f", "Polyphen2_HDIV_score", "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz", vcfFileName };
		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);

		try {
			cmd.initAnnotate();

			// Get entry.
			// Note: There is only one entry to annotate (the VCF file has one line)
			VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
			VcfEntry vcfEntry = vcfFile.next();

			cmd.annotate(vcfEntry);
			if (verbose) System.out.println(vcfEntry);

			// Check all values
			Assert.assertEquals(".,1.0,1.0", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Polyphen2_HDIV_score"));

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
