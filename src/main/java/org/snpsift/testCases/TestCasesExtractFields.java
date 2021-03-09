package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEffect;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdExtractFields;

import junit.framework.Assert;
import junit.framework.TestCase;

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
		String args[] = { "extractFields", vcfFileName, fieldExpression };
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdExtractFields ssef = (SnpSiftCmdExtractFields) snpSift.cmd();

		List<String> linesList = ssef.run(true);

		if (debug) {
			for (String line : linesList)
				Log.debug(line);
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
	 * Extract fields form a file and check that the line matches
	 */
	void extractAndCheck(String vcfFileName, String fieldExpression, String expected[]) {
		List<String> linesList = extract(vcfFileName, fieldExpression);
		Assert.assertEquals("Results length does not match", expected.length, linesList.size());

		int i = 0;
		for (String line : linesList) {
			Assert.assertEquals("Result numnber " + i + " does not match (expression: '" + fieldExpression + "').", expected[i], line);
			i++;
		}
	}

	/**
	 * Check headers vs map2num
	 */
	public void test_00() {
		Log.debug("Test");
		VcfHeader vcfHeader = new VcfHeader();

		// Make sure all map2num are in the INFO field
		if (debug) System.out.println("ANN:");
		for (String annField : VcfEffect.ANN_FIELD_NAMES) {
			annField = "ANN." + annField;
			VcfHeaderInfo vi = vcfHeader.getVcfHeaderInfo(annField);
			if (debug) System.out.println("\t" + annField + "\t" + vi);
			Assert.assertTrue("Cannot find INFO header for field '" + annField + "'", vi != null);
		}

		// Make sure all map2num are in the INFO field
		if (debug) System.out.println("EFF:");
		for (String effField : VcfEffect.EFF_FIELD_NAMES) {
			effField = "EFF." + effField;
			VcfHeaderInfo vi = vcfHeader.getVcfHeaderInfo(effField);
			if (debug) System.out.println("\t" + effField + "\t" + vi);
			Assert.assertTrue("Cannot find INFO header for field '" + effField + "'", vi != null);
		}
	}

	/**
	 * Extract fields
	 */
	public void test_01() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "CHROM", "1");
	}

	public void test_02() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "POS", "902133");
	}

	public void test_03() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "ID", "id_1_902133");
	}

	public void test_04() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "REF", "C");
	}

	public void test_05() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "ALT", "T");
	}

	public void test_06() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "FILTER", "PASS");
	}

	public void test_07() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "AF", "0.001");
	}

	public void test_08() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EFFECT", "STOP_GAINED");
	}

	public void test_09() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].IMPACT", "HIGH");
	}

	public void test_10() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].FUNCLASS", "NONSENSE");
	}

	public void test_11() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODON", "Cga/Tga");
	}

	public void test_12() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA", "R45*");
	}

	public void test_13() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].AA_LEN", "611");
	}

	public void test_14() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].GENE", "PLEKHN1");
	}

	public void test_15() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].BIOTYPE", "protein_coding");
	}

	public void test_16() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].CODING", "CODING");
	}

	public void test_17() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].TRID", "ENST00000379410");
	}

	public void test_18() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "EFF[*].EXID", "1");
	}

	public void test_19() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENE", "PLEKHN1");
	}

	public void test_20() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "LOF[*].GENEID", "ENSG00000187583");
	}

	public void test_23() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENE", "PLEKHN1");
	}

	public void test_24() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].GENEID", "ENSG00000187583");
	}

	public void test_25() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].NUMTR", "5");
	}

	public void test_26() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_01.eff.vcf", "NMD[*].PERC", "0.6");
	}

	public void test_27() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD[0]", "16");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD[1]", "2");
		extractAndCheck("test/extractFields_27.vcf", "GEN[0].AD", "16,2");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_28() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00097].GT", "1|0");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_29() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP[0]", "0.005");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP[1]", "0.095");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00102].AP", "0.005,0.095");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_30() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_28.vcf", "GEN[HG00101].AP[1]", "0.123");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_31() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_31.vcf", "EFF[*].AA", "c.*568C>A");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_31_ann() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_31.ann.vcf", "ANN[*].CODON", "c.*568C>A");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_32() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].AA", "p.Glu15Gly");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].HGVS", "p.Glu15Gly");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].HGVS_P", "p.Glu15Gly");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].HGVS_PROT", "p.Glu15Gly");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_33() {
		Log.debug("Test");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].CODON", "c.44A>G");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].HGVS_DNA", "c.44A>G");
		extractAndCheck("test/extractFields_32.vcf", "ANN[*].HGVS_C", "c.44A>G");
	}

	/**
	 * Extract fields using sample names
	 */
	public void test_34() {
		Log.debug("Test");

		// CDS
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].CDS_POS", "755");
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].CDS_LEN", "1188");

		// cDNA
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].CDNA_POS", "769");
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].CDNA_LEN", "2134");

		// AA
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].AA_POS", "252");
		extractAndCheck("test/extractFields_34.vcf", "ANN[*].AA_LEN", "395");
	}

	public void test_35() {
		Log.debug("Test");

		String field1[] = { "true", "false" };
		String field2[] = { "false", "true" };
		extractAndCheck("test/extractFields_35.vcf", "Field1", field1);
		extractAndCheck("test/extractFields_35.vcf", "Field2", field2);
	}

}
