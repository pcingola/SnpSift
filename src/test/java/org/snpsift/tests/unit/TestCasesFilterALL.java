package org.snpsift.tests.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSiftCmdFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Filter test cases for 'ALL' index
 *
 * @author pcingola
 */
public class TestCasesFilterALL {

    public static boolean verbose = false;

    /**
     * Filter by EFF[ALL].EFFECT
     */
    @Test
    public void test_34() {
        // Filter data
        SnpSiftCmdFilter vcfFilter = new SnpSiftCmdFilter();
        String expression = "(EFF[ALL].EFFECT = 'DOWNSTREAM')";
        List<VcfEntry> list = vcfFilter.filter("test/downstream.vcf", expression, true);

        // Check that it satisfies the condition
        if (verbose) Log.info("Expression: '" + expression + "'");
        for (VcfEntry vcfEntry : list) {
            if (verbose) Log.info("\t" + vcfEntry);

            boolean all = true;
            String effStr = vcfEntry.getInfo("EFF");
            for (String eff : effStr.split(",")) {
                String e = eff.split("\\(")[0];
                all &= e.equals("DOWNSTREAM");
            }

            if (!all) Log.debug("Error: " + effStr);
			assertTrue(all);
        }

        assertEquals(163, list.size());

    }

}
