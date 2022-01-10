package org.snpsift.testCases;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdGeneSets;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SnpSift 'gt' test cases
 *
 * @author pcingola
 */
public class TestCasesGeneSets {

    public static boolean debug = false;
    public static boolean verbose = false;

    @Test
    public void test_01() {
        Log.debug("Test");

        String msigDb = "test/c1.all.v4.0.symbols.gmt.gz";
        String vcf = "test/test_geneSets.vcf";
        String[] args = {"geneSets", msigDb, vcf};

        // Run command
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdGeneSets gs = (SnpSiftCmdGeneSets) snpSift.cmd();

        gs.setVerbose(verbose);
        gs.setSuppressOutput(!verbose);
        gs.setDebug(debug);
        List<VcfEntry> results = gs.run(true);

        // Check
        for (VcfEntry ve : results) {
            if (verbose) Log.info(ve.toStr() + "\t" + ve.getInfo("MSigDb"));
            assertEquals(ve.getInfo("MSigDb"), "chr1p36");
        }
    }
}
