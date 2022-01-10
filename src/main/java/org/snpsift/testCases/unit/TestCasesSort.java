package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdSort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * SnpSift 'split' test cases
 *
 * @author pcingola
 */
public class TestCasesSort {

    public static boolean debug = false;
    public static boolean verbose = false;

    /**
     * Vcf Header bug: FORMAT and INFO having the same ID
     */
    @Test
    public void test_01() {
        Log.debug("Test");

        String[] args = {"sort", "test/test_sort_01_1.vcf", "test/test_sort_01_2.vcf"};
        SnpSift ss = new SnpSift(args);
        SnpSiftCmdSort ssSort = (SnpSiftCmdSort) ss.cmd();
        ssSort.setQuiet(!verbose);

        // Sort
        List<VcfEntry> ves = ssSort.run(true);

        // Get header
        VcfEntry ve0 = ves.get(0);
        VcfHeader vh = ve0.getVcfFileIterator().getVcfHeader();

        // Make sure the header has both 'DP': INFO and FORMAT (and that neither are 'implicit' headers)
        assertNotNull(vh.getVcfHeaderInfo("DP"));
        assertNotNull(vh.getVcfHeaderFormat("DP"));
        assertFalse(vh.getVcfHeaderInfo("DP").isImplicit());
        assertFalse(vh.getVcfHeaderFormat("DP").isImplicit());
    }

}
