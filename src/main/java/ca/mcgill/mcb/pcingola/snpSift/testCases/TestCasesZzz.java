package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.fileIterator.DbNsfp;
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

	protected boolean removeDataTypesCache;
	protected String[] defaultExtraArgs;

	public TestCasesZzz() {
		removeDataTypesCache = false;
	}

	public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
		if (verbose) System.out.println("Annotate: " + dbFileName + "\t" + fileName);

		removeDataTypesCache(dbFileName);

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

		removeDataTypesCache(dbFileName);

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

	void removeDataTypesCache(String dbFileName) {
		if (removeDataTypesCache) {
			String dtcFileName = dbFileName + DbNsfp.DATA_TYPES_CACHE_EXT;
			File dtc = new File(dtcFileName);
			if (dtc.delete()) {
				if (verbose) Gpr.debug("Removing data types cache file: " + dtcFileName);
			}
		}
	}

	/**
	 * Test dbnsfp having multiple lines per variant
	 */
	public void test_02() {
		Gpr.debug("Test");
		String vcfFileName = "test/test_dbnsfp_multiple.vcf";
		String dbFileName = "test/test_dbnsfp_multiple_lines.txt.gz";
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

}
