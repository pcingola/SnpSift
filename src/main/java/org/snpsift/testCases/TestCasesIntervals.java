package org.snpsift.testCases;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdIntervals;

import java.util.List;

/**
 * SnpSIft intervals
 *
 * @author pcingola
 */
public class TestCasesIntervals {

    public static boolean verbose = false;

    /**
     * Filter VCF entries
     */
    @Test
    public void test_01() {
        Log.debug("Test");

        // Run command
        String[] args = { //
                "intervals" //
                , "-i", "test/annotate_5.vcf" // This file has a few VCF entries before 1,000,000 and a few after
                , "test/interval.bed" // BED file intervals cover chr1:1-1,000,000
        };
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdIntervals cmd = (SnpSiftCmdIntervals) snpSift.cmd();
        cmd.setVerbose(verbose);
        cmd.setSuppressOutput(!verbose);
        List<VcfEntry> results = cmd.run(true);

        // Check results
        for (VcfEntry ve : results)
            if (ve.getStart() > 1000000) throw new RuntimeException("This entry should be filtered out!\n\t" + ve);

    }

    /**
     * Filter VCF entries
     */
    @Test
    public void test_02() {
        Log.debug("Test");

        // Run command
        String[] args = { //
                "intervals"//
                , "-x" // Exclude entries in the BED file intervals
                , "-i", "test/annotate_5.vcf" // This file has a few VCF entries before 1,000,000 and a few after
                , "test/interval.bed" // BED file intervals cover chr1:1-1,000,000
        };
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdIntervals cmd = (SnpSiftCmdIntervals) snpSift.cmd();
        cmd.setVerbose(verbose);
        cmd.setSuppressOutput(!verbose);
        List<VcfEntry> results = cmd.run(true);

        // Check results
        for (VcfEntry ve : results)
            if (ve.getStart() < 1000000) throw new RuntimeException("This entry should be filtered out!\n\t" + ve);

    }
}
