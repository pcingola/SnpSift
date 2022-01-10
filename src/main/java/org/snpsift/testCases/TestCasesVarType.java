package org.snpsift.testCases;

import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSiftCmdVarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * VarType test cases
 *
 * @author pcingola
 */
public class TestCasesVarType {

    public static boolean verbose = false;

    /**
     * Check that all variants in a file belong to a given type
     */
    void checkAllVarType(String file, String varTypeExpected) {
        SnpSiftCmdVarType varType = new SnpSiftCmdVarType(null);

        VcfFileIterator vcf = new VcfFileIterator(file);
        for (VcfEntry ve : vcf) {
            // Annotate
            varType.annotate(ve);

            // Check that all variants are the ones expected
            if (verbose) Log.info(ve //
                    + "\n\tvarTypeExpected: " + varTypeExpected //
                    + "\n\tINFO flag      : " + ve.getInfoFlag(varTypeExpected) //
            );
            if (!ve.getInfoFlag(varTypeExpected)) System.err.println("Eror in file '" + file + "':\n" + ve);
			assertTrue(ve.getInfoFlag(varTypeExpected));
        }
    }

    void checkVarTypeField(String file, String varTypeExpected) {
        SnpSiftCmdVarType varType = new SnpSiftCmdVarType(null);

        VcfFileIterator vcf = new VcfFileIterator(file);
        for (VcfEntry ve : vcf) {
            // Annotate
            varType.annotate(ve);

            // Check that all variants are the ones expected
            String varTypeAnnotated = ve.getInfo(SnpSiftCmdVarType.VARTYPE);
            assertEquals(varTypeExpected, varTypeAnnotated);
        }
    }

    @Test
    public void test_01_SNP() {
        Log.debug("Test");
        checkAllVarType("test/varType_snp.vcf", "SNP");
    }

    @Test
    public void test_02_MNP() {
        Log.debug("Test");
        checkAllVarType("test/varType_mnp.vcf", "MNP");
    }

    @Test
    public void test_03_INS() {
        Log.debug("Test");
        checkAllVarType("test/varType_ins.vcf", "INS");
    }

    @Test
    public void test_04_DEL() {
        Log.debug("Test");
        checkAllVarType("test/varType_del.vcf", "DEL");
    }

    @Test
    public void test_05_multiple() {
        Log.debug("Test");
        checkVarTypeField("test/varType_multiple.vcf", "SNP,INS");
    }

}
