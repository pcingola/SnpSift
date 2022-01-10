package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.stats.CountByType;
import org.snpeff.util.Log;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdConcordance;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Concordance test cases
 *
 * @author pcingola
 */
public class TestCasesConcordance {

    public static boolean debug = false;
    public static boolean verbose = false || debug;

    SnpSiftCmdConcordance checkConcordance(String refVcfFile, String vcfFile) {
        if (verbose) Log.info("\n\nConcordance between: " + refVcfFile + "\t" + vcfFile);
        String[] args = {"concordance", refVcfFile, vcfFile};

        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdConcordance ssconc = (SnpSiftCmdConcordance) snpSift.cmd();

        ssconc.setVerbose(verbose);
        ssconc.setSuppressOutput(!verbose);
        ssconc.setDebug(debug);
        ssconc.setWriteBySampleFile(false);
        ssconc.setWriteSummaryFile(false);
        ssconc.run();

        CountByType concordance = ssconc.getConcordance();
        if (verbose) Log.info("\n\nConcordance:\n" + concordance);

        return ssconc;
    }

    /**
     * Check that a all values match
     */
    void checkConcordance(String refVcfFile, String vcfFile, CountByType count) {
        SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
        CountByType concordance = ssconc.getConcordance();

        for (String key : count.keysSorted()) {
            if (verbose)
                Log.info("Checking\t'" + key + "'\tExpected: " + count.get(key) + "\tActual: " + concordance.get(key));
            assertEquals(count.get(key), concordance.get(key));
        }
    }

    /**
     * Check that a single value matches
     */
    void checkConcordance(String refVcfFile, String vcfFile, String key, int value) {
        SnpSiftCmdConcordance ssconc = checkConcordance(refVcfFile, vcfFile);
        CountByType concordance = ssconc.getConcordance();

        if (verbose) Log.info("Checking\t'" + key + "'\tExpected: " + value + "\tActual: " + concordance.get(key));
        assertEquals(value, concordance.get(key));
    }

    @Test
    public void test_01() {
        Log.debug("Test");
        checkConcordance("test/concordance_ref_01.vcf", "test/concordance_test_01.vcf", "ALT_2/ALT_2", 1);
    }

    @Test
    public void test_02() {
        Log.debug("Test");
        checkConcordance("test/concordance_ref_02.vcf", "test/concordance_test_02.vcf", "ALT_2/ALT_2", 2);
    }

    @Test
    public void test_03() {
        Log.debug("Test");
        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 1);
        count.inc("ALT_2/REF", 1);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_03", 1);

        checkConcordance("test/concordance_ref_03.vcf", "test/concordance_test_03.vcf", count);
    }

    @Test
    public void test_04() {
        Log.debug("Test");
        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 1);
        count.inc("ALT_2/REF", 1);
        count.inc("ALT_2/MISSING_GT_concordance_test_04", 1);

        checkConcordance("test/concordance_ref_04.vcf", "test/concordance_test_04.vcf", count);
    }

    @Test
    public void test_05() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 3);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_05", 1);

        checkConcordance("test/concordance_ref_05.vcf", "test/concordance_test_05.vcf", count);
    }

    @Test
    public void test_06() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 5);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_06", 1);
        count.inc("MISSING_ENTRY_concordance_ref_06/ALT_2", 1);

        checkConcordance("test/concordance_ref_06.vcf", "test/concordance_test_06.vcf", count);
    }

    @Test
    public void test_07() {
        Log.debug("Test");
        checkConcordance("test/concordance_ref_07.vcf", "test/concordance_test_07.vcf", "ALT_2/ALT_2", 1);
    }

    @Test
    public void test_08() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 10);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_08", 10);

        checkConcordance("test/concordance_ref_08.vcf", "test/concordance_test_08.vcf", count);
    }

    @Test
    public void test_09() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 15);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_09", 15);

        checkConcordance("test/concordance_ref_09.vcf", "test/concordance_test_09.vcf", count);
    }

    @Test
    public void test_10() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_2/ALT_2", 15);
        count.inc("MISSING_ENTRY_concordance_ref_10/ALT_2", 15);

        checkConcordance("test/concordance_ref_10.vcf", "test/concordance_test_10.vcf", count);
    }

    @Test
    public void test_11() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ALT_1/ALT_1", 4);
        count.inc("ALT_1/MISSING_ENTRY_concordance_test_11", 4);
        count.inc("ALT_2/ALT_2", 7);
        count.inc("ALT_2/MISSING_ENTRY_concordance_test_11", 5);
        count.inc("ALT_2/MISSING_GT_concordance_test_11", 1);
        count.inc("MISSING_ENTRY_concordance_ref_11/ALT_2", 1);
        count.inc("MISSING_ENTRY_concordance_ref_11/MISSING_GT_concordance_test_11", 39);
        count.inc("MISSING_ENTRY_concordance_ref_11/REF", 1086);

        checkConcordance("test/concordance_ref_11.vcf", "test/concordance_test_11.vcf", count);
    }

    @Test
    public void test_12() {
        Log.debug("Test");

        CountByType count = new CountByType();
        count.inc("ERROR", 1);

        checkConcordance("test/concordance_ref_12.vcf", "test/concordance_test_12.vcf", count);
    }
}
