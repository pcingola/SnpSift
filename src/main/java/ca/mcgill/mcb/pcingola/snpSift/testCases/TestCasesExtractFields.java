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
	 * @param vcfFileName
	 * @param fieldExpression
	 * @return
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
	 * @param vcfFileName
	 * @param fieldExpression
	 * @param expected
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
		extractAndCheck("test/extractFields_01.eff.vcf", "CHROM", "1");
	}

	public void test_02() {
		extractAndCheck("test/extractFields_01.eff.vcf", "POS", "902133");
	}

	public void test_03() {
		extractAndCheck("test/extractFields_01.eff.vcf", "ID", "id_1_902133");
	}

	public void test_04() {
		extractAndCheck("test/extractFields_01.eff.vcf", "REF", "C");
	}

	public void test_05() {
		extractAndCheck("test/extractFields_01.eff.vcf", "ALT", "T");
	}

	public void test_06() {
		extractAndCheck("test/extractFields_01.eff.vcf", "FILTER", "PASS");
	}

	public void test_07() {
		extractAndCheck("test/extractFields_01.eff.vcf", "AF", "0.001");
	}

	public void test_08() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EFFECT", "STOP_GAINED");
	}

	public void test_09() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].IMPACT", "HIGH");
	}

	public void test_10() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].FUNCLASS", "NONSENSE");
	}

	public void test_11() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODON", "Cga/Tga");
	}

	public void test_12() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA", "R45*");
	}

	public void test_13() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA_LEN", "611");
	}

	public void test_14() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].GENE", "PLEKHN1");
	}

	public void test_15() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].BIOTYPE", "protein_coding");
	}

	public void test_16() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODING", "CODING");
	}

	public void test_17() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].TRID", "ENST00000379410");
	}

	public void test_18() {
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EXID", "1");
	}

	public void test_19() {
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENE", "PLEKHN1");
	}

	public void test_20() {
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENEID", "ENSG00000187583");
	}

	public void test_23() {
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENE", "PLEKHN1");
	}

	public void test_24() {
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENEID", "ENSG00000187583");
	}

	public void test_25() {
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].NUMTR", "5");
	}

	public void test_26() {
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].PERC", "0.60");
	}

}
