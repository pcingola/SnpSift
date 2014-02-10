package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdDbNsfp;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Test GWAS catalog classes
 * 
 * @author pcingola
 */
public class TestCasesDbNsfp extends TestCase {

	public static boolean verbose = false;
	public static boolean debug = false;

	public void test_01() {
		String vcfFileName = "test/test_dbNSFP_chr1_69134.vcf";
		String args[] = { "-f", "GERP++_RS,GERP++_NR,ESP6500_AA_AF,29way_logOdds,Polyphen2_HVAR_pred,SIFT_score,Uniprot_acc,Ensembl_transcriptid", "test/dbNSFP2.0b3.chr1_69134.txt", vcfFileName };
		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);
		cmd.setTabixCheck(false);

		try {
			cmd.initAnnotate();

			// Get entry.
			// Note: There is only one entry to annotate (the VCF file has one line)
			VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
			VcfEntry vcfEntry = vcfFile.next();

			cmd.annotate(vcfEntry);
			if (debug) Gpr.debug(vcfEntry);

			// Check all values
			Assert.assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "GERP++_RS"));
			Assert.assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "GERP++_NR"));
			Assert.assertEquals("0.004785", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "ESP6500_AA_AF"));
			Assert.assertEquals("8.5094", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "29way_logOdds"));
			Assert.assertEquals("B", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Polyphen2_HVAR_pred"));
			Assert.assertEquals("0.090000", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "SIFT_score"));
			Assert.assertEquals("Q8NH21", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Uniprot_acc"));
			Assert.assertEquals("ENST00000534990|ENST00000335137", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_transcriptid"));

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Test dbnsfp having multiple lines per variant
	 */
	public void test_02() {
		String vcfFileName = "test/test_dbnsfp_multiple.vcf";
		String fields = "genename,Ensembl_geneid,Ensembl_transcriptid,aaref,aaalt";
		String args[] = { "-f", fields, "test/test_dbnsfp_multiple_lines.txt", vcfFileName };

		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);
		cmd.setTabixCheck(false);

		try {
			cmd.initAnnotate();

			// Get entry.
			// Note: There is only one entry to annotate (the VCF file has one line)
			VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
			VcfEntry vcfEntry = vcfFile.next();

			cmd.annotate(vcfEntry);
			if (debug) Gpr.debug(vcfEntry);

			// Check all values
			Assert.assertEquals("ENST00000368485,ENST00000515190", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_transcriptid"));
			Assert.assertEquals("IL6R", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "genename"));
			Assert.assertEquals("ENSG00000160712", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_geneid"));
			Assert.assertEquals("A,L", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "aaalt"));
			Assert.assertEquals("D,I", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "aaref"));

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Test dbnsfp having multiple lines per variant, without collapsing
	 */
	public void test_03() {
		String vcfFileName = "test/test_dbnsfp_multiple_noCollapse.vcf";
		String fields = "aaalt,Ensembl_transcriptid,Polyphen2_HDIV_score,Polyphen2_HVAR_pred";
		String args[] = { "-nocollapse", "-f", fields, "test/test_dbnsfp_multiple_noCollapse.txt", vcfFileName };

		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setVerbose(verbose);
		cmd.setDebug(debug);
		cmd.setTabixCheck(false);

		try {
			cmd.initAnnotate();

			// Get entry.
			// Note: There is only one entry to annotate (the VCF file has one line)
			VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
			VcfEntry vcfEntry = vcfFile.next();

			cmd.annotate(vcfEntry);
			if (debug) Gpr.debug(vcfEntry);

			// Check all values
			// dbNSFP_Ensembl_transcriptid=ENST00000347404,ENST00000537835,ENST00000378535,ENST00000228280;dbNSFP_Polyphen2_HDIV_score=0.449,.,0.192;dbNSFP_Polyphen2_HVAR_pred=B,.,B;dbNSFP_aaalt=A,R,T
			Assert.assertEquals("ENST00000347404,ENST00000537835,ENST00000378535|ENST00000228280", vcfEntry.getInfo("dbNSFP_Ensembl_transcriptid"));
			Assert.assertEquals("0.449,.,0.192", vcfEntry.getInfo("dbNSFP_Polyphen2_HDIV_score"));
			Assert.assertEquals("B,.,B", vcfEntry.getInfo("dbNSFP_Polyphen2_HVAR_pred"));
			Assert.assertEquals("A,R,T", vcfEntry.getInfo("dbNSFP_aaalt"));

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void test_04() {
		// We annotate something trivial: position
		String vcfFileName = "test/test_dbNSFP_04.vcf";
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
				Assert.assertEquals("" + pos, posDb); // Compare
			}

			cmd.endAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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
