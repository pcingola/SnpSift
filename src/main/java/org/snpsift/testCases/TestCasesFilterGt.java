package org.snpsift.testCases;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.SnpSiftCmdFilterGt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Filter test cases for 'GT Filter'
 *
 * @author pcingola
 */
public class TestCasesFilterGt {

    public static boolean verbose = false;

    /**
     * Filter
     */
    @Test
    public void test_01() {
        Log.debug("Test");
        String expression = "(GQ < 50) | (DP < 20)";

        SnpSiftCmdFilterGt gtfilter = new SnpSiftCmdFilterGt();
        List<VcfEntry> list = gtfilter.filter("test/testGtFilter01.vcf", expression, true);

        for (VcfEntry ve : list) {
            if (verbose) Log.info(ve);

            for (VcfGenotype gt : ve.getVcfGenotypes()) {
                String gtStr = gt.get("GT");
                String gqStr = gt.get("GQ");
                String dpStr = gt.get("DP");

                int gq = Gpr.parseIntSafe(gqStr);
                int dp = Gpr.parseIntSafe(dpStr);

                String filtered = "";
                if ((gq < 50) | (dp < 20)) {
                    filtered = "FILTERED";
                    assertEquals("./.", gtStr);
                }

                if (verbose)
                    Log.info("\tGT: " + gtStr + " " + filtered + "\tGQ: " + gqStr + "\tDP: " + dpStr + "\t" + gt);
            }
        }
    }

    /**
     * Filter
     */
    @Test
    public void test_02() {
        Log.debug("Test");
        String expression = "(GQ < 50) | (DP < 20)";

        SnpSiftCmdFilterGt gtfilter = new SnpSiftCmdFilterGt();
        List<VcfEntry> list = gtfilter.filter("test/testGtFilter02.vcf", expression, true);

        for (VcfEntry ve : list) {
            if (verbose) Log.info(ve);

            for (VcfGenotype gt : ve.getVcfGenotypes()) {
                String gtStr = gt.get("GT");
                String gqStr = gt.get("GQ");
                String dpStr = gt.get("DP");

                int gq = Gpr.parseIntSafe(gqStr);
                int dp = Gpr.parseIntSafe(dpStr);

                String filtered = "";
                if ((gq < 50) | (dp < 20)) {
                    filtered = "FILTERED";
                    assertEquals("./././.", gtStr);
                }

                if (verbose)
                    Log.info("\tGT: " + gtStr + " " + filtered + "\tGQ: " + gqStr + "\tDP: " + dpStr + "\t" + gt);
            }
        }
    }

}
