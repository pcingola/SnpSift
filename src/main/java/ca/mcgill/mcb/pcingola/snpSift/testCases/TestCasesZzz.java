package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdDbNsfp;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = true;
	public static boolean verbose = false || debug;

	protected String[] defaultExtraArgs;

	public TestCasesZzz() {
	}

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

}
