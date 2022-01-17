package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpsift.SnpSift;

/**
 * VarType test cases
 *
 * @author pcingola
 */
public class TestCasesVcfCheck {

    public static boolean verbose = false;

    @Test
    public void test_01() {
        Log.debug("Test");
        String vcfFile = "test/test_vcfCheck_01.vcf";
        String[] args = {"vcfCheck", vcfFile};
        SnpSift snpSift = new SnpSift(args);
        snpSift.run();
    }

}
