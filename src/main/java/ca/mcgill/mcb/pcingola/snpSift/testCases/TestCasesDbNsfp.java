package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

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

	protected String[] defaultExtraArgs = null;

	public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);
		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setDbFileName(dbFileName);
		cmd.setVerbose(verbose);
		cmd.setSuppressOutput(!verbose);
		cmd.setDebug(debug);
		cmd.setTabixCheck(false);

		List<VcfEntry> results = cmd.run(true);

		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);

		return results;
	}

	public Map<String, String> annotateGetFiledTypes(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		// Create command line
		String args[] = argsList(dbFileName, fileName, extraArgs);
		SnpSiftCmdDbNsfp cmd = new SnpSiftCmdDbNsfp(args);
		cmd.setDbFileName(dbFileName);
		cmd.setVerbose(verbose);
		cmd.setSuppressOutput(!verbose);
		cmd.setDebug(debug);
		cmd.setTabixCheck(false);

		List<VcfEntry> results = cmd.run(true);

		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() > 0);

		return cmd.getFieldsType();
	}

	protected String[] argsList(String dbFileName, String fileName, String[] extraArgs) {
		ArrayList<String> argsList = new ArrayList<String>();

		if (defaultExtraArgs != null) {
			for (String arg : defaultExtraArgs)
				argsList.add(arg);
		}

		if (extraArgs != null) {
			for (String arg : extraArgs)
				argsList.add(arg);
		}

		argsList.add(fileName);
		return argsList.toArray(new String[0]);
	}

	public void test_01() {
		Gpr.debug("Test");
		String vcfFileName = "test/test_dbNSFP_chr1_69134.vcf";
		String dbFileName = "test/dbNSFP2.0b3.chr1_69134.txt";
		String args[] = { "-collapse", "-f", "GERP++_RS,GERP++_NR,ESP6500_AA_AF,29way_logOdds,Polyphen2_HVAR_pred,SIFT_score,Uniprot_acc,Ensembl_transcriptid" };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
		VcfEntry vcfEntry = results.get(0);

		// Check all values
		Assert.assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "GERP++_RS"));
		Assert.assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "GERP++_NR"));
		Assert.assertEquals("0.004785", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "ESP6500_AA_AF"));
		Assert.assertEquals("8.5094", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "29way_logOdds"));
		Assert.assertEquals("B", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Polyphen2_HVAR_pred"));
		Assert.assertEquals("0.090000", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "SIFT_score"));
		Assert.assertEquals("Q8NH21", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Uniprot_acc"));
		Assert.assertEquals("ENST00000534990,ENST00000335137", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_transcriptid"));

	}

	/**
	 * Test dbnsfp having multiple lines per variant
	 */
	public void test_02() {
		Gpr.debug("Test");
		String vcfFileName = "test/test_dbnsfp_multiple.vcf";
		String dbFileName = "test/test_dbnsfp_multiple_lines.txt";
		String fields = "genename,Ensembl_geneid,Ensembl_transcriptid,aaref,aaalt";
		String args[] = { "-collapse", "-f", fields };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
		VcfEntry vcfEntry = results.get(0);

		// Check all values
		Assert.assertEquals("ENST00000368485,ENST00000515190", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_transcriptid"));
		Assert.assertEquals("IL6R", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "genename"));
		Assert.assertEquals("ENSG00000160712", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Ensembl_geneid"));
		Assert.assertEquals("A,L", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "aaalt"));
		Assert.assertEquals("D,I", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "aaref"));
	}

	/**
	 * Test dbnsfp having multiple lines per variant, without collapsing
	 */
	public void test_03() {
		Gpr.debug("Test");
		String vcfFileName = "test/test_dbnsfp_multiple_noCollapse.vcf";
		String dbFileName = "test/test_dbnsfp_multiple_noCollapse.txt";
		String fields = "aaalt,Ensembl_transcriptid,Polyphen2_HDIV_score,Polyphen2_HVAR_pred";
		String args[] = { "-nocollapse", "-f", fields };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
		VcfEntry vcfEntry = results.get(0);

		// Check all values
		// dbNSFP_Ensembl_transcriptid=ENST00000347404,ENST00000537835,ENST00000378535,ENST00000228280;dbNSFP_Polyphen2_HDIV_score=0.449,.,0.192;dbNSFP_Polyphen2_HVAR_pred=B,.,B;dbNSFP_aaalt=A,R,T
		Assert.assertEquals("ENST00000347404,ENST00000537835,ENST00000378535,ENST00000228280", vcfEntry.getInfo("dbNSFP_Ensembl_transcriptid"));
		Assert.assertEquals("0.449,.,0.192", vcfEntry.getInfo("dbNSFP_Polyphen2_HDIV_score"));
		Assert.assertEquals("B,.,B", vcfEntry.getInfo("dbNSFP_Polyphen2_HVAR_pred"));
		Assert.assertEquals("A,R,T", vcfEntry.getInfo("dbNSFP_aaalt"));
	}

	public void test_04() {
		Gpr.debug("Test");
		// We annotate something trivial: position
		String vcfFileName = "test/test_dbNSFP_04.vcf";
		String dbFileName = "test/dbNSFP2.3.test.txt.gz";
		String args[] = { "-collapse", "-f", "pos(1-coor)" };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);

		// Get entry.
		// Note: There is only one entry to annotate (the VCF file has one line)
		for (VcfEntry vcfEntry : results) {

			// Check that position (annotated from dbNSFP) actually matches
			String posDb = vcfEntry.getInfo("dbNSFP_pos(1-coor)"); // Get INFO field annotated from dbNSFP
			int pos = vcfEntry.getStart() + 1; // Get position
			Assert.assertEquals("" + pos, posDb); // Compare
		}
	}

	public void test_05() {
		Gpr.debug("Test");
		// We annotate something trivial: position
		String vcfFileName = "test/test_dbNSFP_05.vcf";
		String dbFileName = "test/dbNSFP2.3.test.txt.gz";
		String args[] = { "-f", "pos(1-coor)" };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);

		for (VcfEntry vcfEntry : results) {
			// Check that position (annotated from dbNSFP) actually matches
			String posDb = vcfEntry.getInfo("dbNSFP_pos(1-coor)"); // Get INFO field annotated from dbNSFP
			int pos = vcfEntry.getStart() + 1; // Get position
			if (debug) Gpr.debug(vcfEntry.getChromosomeName() + ":" + pos + "\t" + posDb);

			// Check
			if (pos != 249213000) Assert.assertEquals("" + pos, posDb);
			else Assert.assertEquals(null, posDb); // There is no dbNSFP for this entry. It should be 'null'
		}

	}

	public void test_06() {
		Gpr.debug("Test");
		// We annotate something trivial: position
		String vcfFileName = "test/test_dbNSFP_06.vcf";
		String dbFileName = "test/dbNSFP2.3.test.txt.gz";
		String args[] = { "-f", "pos(1-coor)" };

		annotate(dbFileName, vcfFileName, args);
	}

	/**
	 * Check header values are correctly inferred.
	 *
	 * E.g. If the first value in the table is '0', we may infer
	 * 		that data type is INT, but if another value '0.5'
	 * 		appears, we change it to FLOAT
	 *
	 */
	public void test_07() {
		// We annotate something trivial: position
		String vcfFileName = "test/test.dbnsfp.07.vcf";
		String dbFileName = "test/dbNSFP2.4.head_100.txt.gz";
		String args[] = { "-f", "SIFT_score" };

		// Check field type
		Map<String, String> fieldTypes = annotateGetFiledTypes(dbFileName, vcfFileName, args);
		Assert.assertEquals("Float", fieldTypes.get("SIFT_score"));
	}

	/**
	 * Missing annotations
	 */
	public void test_08() {
		String vcfFileName = "test/test_dbNSFP_8.vcf";
		String dbFileName = "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz";
		String args[] = { "-collapse", "-a", "-f", "Polyphen2_HDIV_score" };

		List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
		VcfEntry vcfEntry = results.get(0);

		// Check all values
		Assert.assertEquals(".,1.0,1.0", vcfEntry.getInfo(SnpSiftCmdDbNsfp.VCF_INFO_PREFIX + "Polyphen2_HDIV_score"));
	}

}
