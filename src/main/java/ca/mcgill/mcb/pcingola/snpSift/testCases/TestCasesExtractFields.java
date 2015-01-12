package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import ca.mcgill.mcb.pcingola.snpSift.SnpSiftCmdExtractFields;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * Extract fields test cases
 *
 * @author pcingola
 */
public class TestCasesExtractFields extends TestCase {

	public static boolean debug = false;

	/**
	 * Extract fields and return the output lines
	 */
	List<String> extract(String vcfFileName, String fieldExpression) {
		String args[] = { vcfFileName, fieldExpression };
		SnpSiftCmdExtractFields ssef = new SnpSiftCmdExtractFields(args);

		List<String> linesList = ssef.run(true);

		if (debug) {
			for (String line : linesList)
				Gpr.debug(line);
		}

		return linesList;
	}

	/**
	 * Extract fields form a file and check that the line matches (only one line expected from the file)
	 */
	void extractAndCheck(String vcfFileName, String fieldExpression, String expected) {
		List<String> linesList = extract(vcfFileName, fieldExpression);
		if (linesList.size() != 1) throw new RuntimeException("Only one line expected");
		Assert.assertEquals(expected, linesList.get(0));
	}

	/**
	 * Extract fields
	 */
	public void test_01() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "CHROM", "1");
	}

	public void test_02() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "POS", "902133");
	}

	public void test_03() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "ID", "id_1_902133");
	}

	public void test_04() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "REF", "C");
	}

	public void test_05() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "ALT", "T");
	}

	public void test_06() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "FILTER", "PASS");
	}

	public void test_07() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "AF", "0.001");
	}

	public void test_08() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EFFECT", "STOP_GAINED");
	}

	public void test_09() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].IMPACT", "HIGH");
	}

	public void test_10() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].FUNCLASS", "NONSENSE");
	}

	public void test_11() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODON", "Cga/Tga");
	}

	public void test_12() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA", "R45*");
	}

	public void test_13() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA_LEN", "611");
	}

	public void test_14() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].GENE", "PLEKHN1");
	}

	public void test_15() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].BIOTYPE", "protein_coding");
	}

	public void test_16() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODING", "CODING");
	}

	public void test_17() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].TRID", "ENST00000379410");
	}

	public void test_18() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EXID", "1");
	}

	public void test_19() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENE", "PLEKHN1");
	}

	public void test_20() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENEID", "ENSG00000187583");
	}

	public void test_23() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENE", "PLEKHN1");
	}

	public void test_24() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENEID", "ENSG00000187583");
	}

	public void test_25() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].NUMTR", "5");
	}

	public void test_26() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].PERC", "0.6");
	}

	public void test_27() {
		Gpr.debug("Test");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD", "16,2");
	}

}
