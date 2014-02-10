package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdDbNsfp;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Try test cases in this class before adding them to long test cases
 * 
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean verbose = false;
	public static boolean debug = false;

	public void test_05() {
		// We annotate something trivial: position
		String vcfFileName = "test/test_dbNSFP_05.vcf";
		String args[] = { "-f", "pos(1-coor)", "test/dbNSFP2.3.test.txt.gz", vcfFileName };

		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);

		try {
			cmd.initAnnotate();

			// Get entry.
			// Note: There is only one entry to annotate (the VCF file has one line)
			VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
			for (VcfEntry vcfEntry : vcfFile) {
				// Annotate vcf entry
				cmd.annotate(vcfEntry);

				// Check that position (annotated from dbNSFP) actually matches
				String posDb = vcfEntry.getInfo("dbNSFP_pos(1-coor)"); // Get INFO field annotated from dbNSFP
				int pos = vcfEntry.getStart() + 1; // Get position 
				if (debug) Gpr.debug(vcfEntry.getChromosomeName() + ":" + pos + "\t" + posDb);

				// Check
				if (pos != 249213000) Assert.assertEquals("" + pos, posDb);
				else Assert.assertEquals(null, posDb); // There is no dbNSFP for this entry. It should be 'null'
			}

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
