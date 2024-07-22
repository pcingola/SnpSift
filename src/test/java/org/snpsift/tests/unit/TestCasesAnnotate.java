package org.snpsift.tests.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdAnnotate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesAnnotate {

    public static boolean debug = false;
    public static boolean verbose = false || debug;

    protected String[] defaultExtraArgs;
    protected boolean deleteIndexFile;

    public TestCasesAnnotate() {
        String[] memExtraArgs = {"-sorted"};
        defaultExtraArgs = memExtraArgs;
    }

    /**
     * Annotate
     */
    public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
        if (verbose) Log.info("Annotate: " + dbFileName + "\t" + fileName);

        if (deleteIndexFile) deleteIndexFile(dbFileName);

        // Create command line
        String[] args = argsList(dbFileName, fileName, extraArgs);

        // Create command
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdAnnotate snpSiftAnnotate = (SnpSiftCmdAnnotate) snpSift.cmd();

        snpSiftAnnotate.setDebug(debug);
        snpSiftAnnotate.setVerbose(verbose);
        snpSiftAnnotate.setSuppressOutput(!verbose);
        List<VcfEntry> results = snpSiftAnnotate.run(true);

        // Check
        assertNotNull(results);
        assertTrue(results.size() > 0);
        return results;
    }

    /**
     * Annotate and return STDOUT as a string
     */
    public String annotateOut(String dbFileName, String fileName, String[] extraArgs) {
        if (verbose) Log.info("Annotate: " + dbFileName + "\t" + fileName);

        if (deleteIndexFile) deleteIndexFile(dbFileName);

        // Create command line
        String[] args = argsList(dbFileName, fileName, extraArgs);

        // Create command
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdAnnotate snpSiftAnnotate = (SnpSiftCmdAnnotate) snpSift.cmd();

        snpSiftAnnotate.setDebug(debug);
        snpSiftAnnotate.setVerbose(verbose);
        snpSiftAnnotate.setSaveOutput(true);
        snpSiftAnnotate.run();

        // Check
        return snpSiftAnnotate.getOutput();
    }

    public void annotateTest(String dbFileName, String fileName) {
        annotateTest(dbFileName, fileName, null);
    }

    /**
     * Annotate a file and check that the new annotation matches the expected one
     */
    public void annotateTest(String dbFileName, String fileName, String[] extraArgs) {
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Check each entry
        for (VcfEntry vcf : results) {
            // We expect the same annotation twice
            String idstr = vcf.getId();

            // Get expected IDs
            String expectedIds = vcf.getInfo("EXP_IDS");
            if (expectedIds != null) {
                expectedIds = expectedIds.replace('|', ';');
                expectedIds = expectedIds.replace(',', ';');

                if (expectedIds.equals(".")) expectedIds = "";

                // Compare
                assertEquals(expectedIds, idstr);
            } else fail("EXP_IDS (expected ids) INFO field missing in " + fileName + ", entry:\n" + vcf);
        }
    }

    protected String[] argsList(String dbFileName, String fileName, String[] extraArgs) {
        ArrayList<String> argsList = new ArrayList<>();

        argsList.add("annotate");

        if (defaultExtraArgs != null) {
            for (String arg : defaultExtraArgs)
                argsList.add(arg);
        }

        if (extraArgs != null) {
            for (String arg : extraArgs)
                argsList.add(arg);
        }

        argsList.add(dbFileName);
        argsList.add(fileName);
        return argsList.toArray(new String[0]);
    }

    void deleteIndexFile(String dbFileName) {
        String indexFile = dbFileName + ".sidx";
        File f = new File(indexFile);
        if (f.delete()) {
            if (verbose) Log.debug("Index file '" + indexFile + "' deleted before annotation test");
        }
    }

    @Test
    public void test_01() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_1.vcf";
        String fileName = "./test/annotate_1.vcf";
        annotateTest(dbFileName, fileName);
    }

    @Test
    public void test_02() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_10.vcf";
        String fileName = "./test/annotate_10.vcf";
        annotateTest(dbFileName, fileName);
    }

    @Test
    public void test_03() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_2.vcf";
        String fileName = "./test/annotate_2.vcf";
        annotateTest(dbFileName, fileName);
    }

    @Test
    public void test_04() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_large.vcf";
        String fileName = "./test/annotate_large.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Chromosomes in VCF file are called 'chr22' instead of '22'.
     * This should work OK as well.
     */
    @Test
    public void test_05() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_chr22.vcf";
        String fileName = "./test/test_chr22.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Annotate info fields
     */
    @Test
    public void test_06() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_06.vcf";
        String fileName = "./test/annotate_06.vcf";
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check
        assertEquals("PREVIOUS=annotation;TEST=yes" //
                        + ";ABE=0.678" //
                        + ";ABZ=47.762" //
                        + ";AF=0.002" //
                        + ";AN=488" //
                        + ";AOI=-410.122" //
                        + ";AOZ=-399.575" //
                        + ";IOD=0.000" //
                        + ";OBS=4,1,1636,2011,3,1,6780,9441" //
                        + ";RSPOS=16346045" //
                , results.get(0).getInfoStr() //
        );
    }

    /**
     * Annotate only some info fields
     */
    @Test
    public void test_07() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_06.vcf";
        String fileName = "./test/annotate_06.vcf";
        String extraArgs[] = {"-info", "AF,AN,ABE"};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Check
        assertEquals("PREVIOUS=annotation;TEST=yes;ABE=0.678;AF=0.002;AN=488", results.get(0).getInfoStr());
    }

    /**
     * Do not annotate ID column
     */
    @Test
    public void test_08() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_06.vcf";
        String fileName = "./test/annotate_06.vcf";
        String extraArgs[] = {"-noId"};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);
        assertEquals("OLD_ID", results.get(0).getId());
    }

    /**
     * Annotate only some info fields
     */
    @Test
    public void test_09() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_09.vcf";
        String fileName = "./test/annotate_09.vcf";
        String extraArgs[] = {"-info", "GMAF,AC"};

        // Annotate
        String out = annotateOut(dbFileName, fileName, extraArgs);

        // Make sure output header for "GMAF" is present.
        // Also make sure implicit headers are not (e.g. AC)
        boolean hasGmaf = false;
        boolean hasAc = false;
        for (String line : out.split("\n")) {
            hasGmaf |= line.startsWith("##INFO=<ID=GMAF");
            hasAc |= line.startsWith("##INFO=<ID=AC");
        }

        assertEquals(true, hasGmaf);
        assertEquals(false, hasAc);
    }

    /**
     * Annotate only some info fields
     *
     * @throws IOException
     */
    @Test
    public void test_11() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_11.vcf";
        String fileName = "./test/annotate_11.vcf";
        String extraArgs[] = {"-info", "GMAF,AC"};

        // Annotate
        String out = annotateOut(dbFileName, fileName, extraArgs);

        // Make sure output header for "GMAF" is present ONLY ONCE.
        // Also make sure implicit headers are not (e.g. AC)
        if (verbose) Log.info(out);
        int hasGmaf = 0;
        int hasAc = 0;
        for (String line : out.split("\n")) {
            if (line.startsWith("##INFO=<ID=GMAF")) hasGmaf++;
            if (line.startsWith("##INFO=<ID=AC")) hasAc++;
        }

        assertEquals(1, hasGmaf);
        assertEquals(0, hasAc);
    }

    /**
     * Annotate without REF/ALT fields
     */
    @Test
    public void test_12() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_12.vcf";
        String fileName = "./test/annotate_12.vcf";
        String extraArgs[] = {"-noAlt"};

        // Annotate
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Check that new ID was NOT added
        assertEquals("NEW_ID", results.get(0).getId());
    }

    /**
     * Annotate using "reserved" VCF fields (e.g. "AA" )
     * Header should be added if it doesn't exits.
     *
     * @throws IOException
     */
    @Test
    public void test_13() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_13.vcf";
        String fileName = "./test/annotate_13.vcf";
        String extraArgs[] = {"-info", "AA"};

        // Annotate
        String out = annotateOut(dbFileName, fileName, extraArgs);

        // Make sure output header for "AA" is present ONLY ONCE.
        // Also make sure implicit headers are not (e.g. AC)
        if (verbose) Log.info(out);
        int hasAa = 0;
        for (String line : out.split("\n"))
            if (line.startsWith("##INFO=<ID=AA")) hasAa++;

        assertEquals(1, hasAa);
    }

    @Test
    public void test_14() {
        Log.debug("Test");
        String dbFileName = "./test/annotate_multiple_allele.db.vcf";
        String fileName = "./test/annotate_multiple_allele.1.vcf";

        // Annotate
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check results
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String allNum = ve.getInfo("ALL_NUM");
        assertEquals("2", allNum);
    }

    @Test
    public void test_15() {
        Log.debug("Test");
        String dbFileName = "./test/annotate_multiple_allele_R.db.vcf";
        String fileName = "./test/annotate_multiple_allele.2.vcf";

        // Annotate
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check results
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String allNum = ve.getInfo("ALL_NUM");
        assertEquals("value_REF,value_C", allNum);
    }

    @Test
    public void test_16() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_16.vcf";
        String fileName = "./test/annotate_16.vcf";
        String infoName = "PREPEND_";
        String extraArgs[] = {"-name", infoName};

        // Annotate
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        VcfEntry ve = results.get(0);
        String aa = ve.getInfo(infoName + "AA");
        String bb = ve.getInfo(infoName + "BB");
        assertEquals("Field_Value", aa);
        assertEquals("AnotherValue", bb);
    }

    @Test
    public void test_17() {
        Log.debug("Test");
        String dbFileName = "./test/annotate_oder_db.vcf";
        String fileName = "./test/annotate_oder_snp.vcf";

        // Fake annotations (in files)
        HashMap<String, String> types = new HashMap<>();
        types.put("0", "zero");
        types.put("1", "first");
        types.put("2", "second");

        // Annotate
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check that our "fake annotations" are matched as expected
        for (VcfEntry ve : results) {
            String type = ve.getInfo("TYPE");
            String ann = ve.getInfo("ANN");

            if (verbose) Log.info(ve.toStr() + "\t" + type + "\t" + ann);
            assertEquals(types.get(type), ann);
        }
    }

    @Test
    public void test_18() {
        Log.debug("Test");
        String dbFileName = "./test/test_annotate_18_db.vcf";
        String fileName = "./test/test_annotate_18.vcf";

        List<VcfEntry> results = annotate(dbFileName, fileName, null);
        VcfEntry ve = results.get(0);
        String ukac = ve.getInfo("UK10KWES_AC");
        if (verbose) Log.info("Annotated value: " + ukac);
        assertEquals(".,49,44,.,.,.,.,.,.,.,.", ukac);
    }

    @Test
    public void test_19() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_multiline.vcf";
        String fileName = "./test/annotate_multiline.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Annotate info fields
     */
    @Test
    public void test_20() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_20.vcf";
        String fileName = "./test/annotate_20.vcf";
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check
        assertEquals("44,49", results.get(0).getInfo("ANNOTATE_ONCE"));
    }

    /**
     * Annotate two consecutive variants in the same position
     */
    @Test
    public void test_21() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_21.vcf";
        String fileName = "./test/annotate_21.vcf";
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Third entry is the one not being annotated

        // Check third entry
        VcfEntry ve = results.get(2);
        String ann = ve.getInfo("clinvar_db");
        if (debug) Log.debug("Annotation: '" + ann + "'");
        assertNotNull(ann);

        // Check second entry
        ve = results.get(1);
        ann = ve.getInfo("clinvar_db");
        if (debug) Log.debug("Annotation: '" + ann + "'");
        assertNotNull(ann);

    }

    /**
     * Annotate first base in a chromosome
     */
    @Test
    public void test_22() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_2.vcf";
        String fileName = "./test/annotate_22.vcf";
        annotate(dbFileName, fileName, null);
        // We simply check that no exception was thrown
    }

    /**
     * Database has one entry and VCF has multiple ALTs
     */
    @Test
    public void test_23_allele_specific_annotation_missing_R() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_23.vcf";
        String fileName = "./test/annotate_23.vcf";
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check results
        VcfEntry ve = results.get(0);
        String caf = ve.getInfo("CAF");
        if (verbose) Log.info(ve + "\n\tCAF: " + caf);
        assertEquals("0.9642,.,0.03581", caf);
    }

    /**
     * Annotate using "-name" to prepend a name to VCF fields (e.g. "AA" -> "PREPEND_AA")
     * Header should be added changing the ID accordingly
     */
    @Test
    public void test_24() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_24.vcf";
        String fileName = "./test/annotate_24.vcf";
        String infoName = "PREPEND_";
        String extraArgs[] = {"-name", infoName};

        // Annotate
        String out = annotateOut(dbFileName, fileName, extraArgs);

        // Make sure output header for "PREPEND_AA"  and "PREPEND_BB" are present ONLY ONCE.
        if (verbose) Log.info(out);
        int hasAa = 0, hasBb = 0, hasCc = 0;
        for (String line : out.split("\n")) {
            if (line.startsWith("##INFO=<ID=PREPEND_AA")) hasAa++;
            if (line.startsWith("##INFO=<ID=PREPEND_BB")) hasBb++;
            if (line.startsWith("##INFO=<ID=PREPEND_CC")) hasCc++;
        }

        assertEquals(1, hasAa);
        assertEquals(1, hasBb);
        assertEquals(1, hasCc);
    }

    /**
     * Annotate using "-name" to prepend a name to VCF fields (e.g. "AA" -> "PREPEND_AA")
     * Header should be added changing the ID accordingly
     */
    @Test
    public void test_25() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_24.vcf";
        String fileName = "./test/annotate_24.vcf";
        String infoName = "PREPEND_";
        String extraArgs[] = {"-name", infoName, "-info", "AA,BB"}; // Note: We don't include 'CC' annotation

        // Annotate
        String out = annotateOut(dbFileName, fileName, extraArgs);

        // Make sure output header for "PREPEND_AA"  and "PREPEND_BB" are present ONLY ONCE.
        if (verbose) Log.info(out);
        int hasAa = 0, hasBb = 0, hasCc = 0;
        for (String line : out.split("\n")) {
            if (line.startsWith("##INFO=<ID=PREPEND_AA")) hasAa++;
            if (line.startsWith("##INFO=<ID=PREPEND_BB")) hasBb++;
            if (line.startsWith("##INFO=<ID=PREPEND_CC")) hasCc++;
        }

        assertEquals(1, hasAa);
        assertEquals(1, hasBb);
        assertEquals(0, hasCc); // This onw should NOT be present
    }

    /**
     * Annotate ID using entries that are duplicated in db.vcf
     */
    @Test
    public void test_26_repeat_db_entry() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_26.vcf";
        String fileName = "./test/annotate_26.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Annotate INFO fields using entries that are duplicated in db.vcf
     */
    @Test
    public void test_27_repeat_db_entry() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_27.vcf";
        String fileName = "./test/annotate_27.vcf";
        List<VcfEntry> res = annotate(dbFileName, fileName, null);
        for (VcfEntry ve : res) {
            if (verbose) Log.info(ve);
            assertEquals("121964859,45578238", ve.getInfo("RS"));
        }
    }

    /**
     * Annotate if a VCF entry exists in the database file
     */
    @Test
    public void test_28_exists() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_28.vcf";
        String fileName = "./test/annotate_28.vcf";
        String args[] = {"-exists", "EXISTS"};

        List<VcfEntry> res = annotate(dbFileName, fileName, args);
        for (VcfEntry ve : res) {
            if (verbose) Log.info(ve);

            // Check
            if (ve.getStart() == 201331098)
                assertTrue(ve.hasInfo("EXISTS"), "Existing VCF entry has not been annotated");
            else assertFalse(ve.hasInfo("EXISTS"), "Non-existing VCF entry has been annotated");
        }
    }

    /**
     * Annotate if a VCF entry's ID might have multiple repeated entries
     */
    @Test
    public void test_29_repeated_IDs() {
        Log.debug("Test");

        String dbFileName = "./test/db_test_29.vcf";
        String fileName = "./test/annotate_29.vcf";
        String args[] = {"-exists", "EXISTS"};

        List<VcfEntry> res = annotate(dbFileName, fileName, args);
        for (VcfEntry ve : res) {
            if (verbose) Log.info(ve);

            // Check
            if (ve.getStart() == 838418) assertEquals("rs1130678", ve.getId());
            else if (ve.getStart() == 49545) assertEquals("rs62075716", ve.getId());
            else if (ve.getStart() == 109567) assertEquals("rs62076738", ve.getId());
            else throw new RuntimeException("Position not found: " + ve.getStart());
        }
    }

    /**
     * Issue when database has REF several variants which have to
     * be converted into minimal representation
     */
    @Test
    public void test_31_annotate_minimal_representation_db() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_31.vcf";
        String fileName = "./test/annotate_31.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Issue when query has REF several variants which have to
     * be converted into minimal representation
     */
    @Test
    public void test_32_annotate_minimal_representation_input() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_32.vcf";
        String fileName = "./test/annotate_32.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Empty database
     */
    @Test
    public void test_33_empty_db() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_33.vcf";
        String fileName = "./test/annotate_33.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Database has one entry and VCF has multiple ALTs
     */
    @Test
    public void test_34_dbStartsOnDifferentChromo() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_34.vcf";
        String fileName = "./test/annotate_34.vcf";
        List<VcfEntry> results = annotate(dbFileName, fileName, null);

        // Check results
        VcfEntry ve = results.get(0);
        String rs = ve.getInfo("RS");
        if (verbose) Log.info(ve + "\n\tRS: " + rs);
        assertEquals("207477890", rs);
    }

    /**
     * Annotate flags without '=true'
     */
    @Test
    public void test_35() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_35.vcf";
        String fileName = "./test/annotate_35.vcf";
        String extraArgs[] = {"-noId"};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        VcfEntry ve = results.get(0);
        String infoStr = ve.getInfoStr();

        // Check that trailing '=true' is not added
        assertEquals("FLAG_ADD", infoStr);
    }

    /**
     * Annotate multi-allelic
     */
    @Test
    public void test_36() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_36.vcf";
        String fileName = "./test/annotate_36.vcf";
        String extraArgs[] = {};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that trailing '=true' is not added
        assertEquals("GENE=NPHP4", infoStr);
    }

    /**
     * Input VCf triggers a seek to a position after chromosome sections
     * ends in database.
     * Previously a bug it forced an infinite loop
     */
    @Test
    public void test_38() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_38.vcf";
        String fileName = "./test/annotate_38.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Annotate issues with discovered info fields (when annotating ALL info fields)
     */
    @Test
    public void test_39() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_39.vcf";
        String fileName = "./test/annotate_39.vcf";
        String extraArgs[] = {};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that CAF annotation is added
        assertTrue(infoStr.indexOf("CAF=0.251,0.749") >= 0, "Missing CAF annotation");
    }

    /**
     * Problems when tabix database file has 'chr' in chromosome names and VCF file does not
     */
    @Test
    public void test_40() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_40.vcf";
        String fileName = "./test/annotate_40.vcf";
        annotateTest(dbFileName, fileName);
    }

    @Test
    public void test_41() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_41.vcf";
        String fileName = "./test/annotate_41.vcf";
        annotateTest(dbFileName, fileName);
    }

    /**
     * Test multiple CAF annotations
     */
    @Test
    public void test_42() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_42.vcf";
        String fileName = "./test/annotate_42.vcf";
        String extraArgs[] = {};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Get first entry
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that CAF annotation is added
        assertTrue(infoStr.indexOf("CAF=") >= 0, "Missing CAF annotation");

        // Compare against expected output
        // Note: We don't care about annotation order in this case
        String expectedCaf[] = "0.4908,0.5066,0.002596,.,0.4908,0.5066,0.002596,0.4908,0.5066".split(",");
        String caf[] = ve.getInfo("CAF").split(",");
        Arrays.sort(expectedCaf);
        Arrays.sort(caf);
        assertArrayEquals(expectedCaf, caf, "Number of CAF annotations differ");
    }

    /**
     * Annotate empty
     */
    @Test
    public void test_43() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_43.vcf";
        String fileName = "./test/annotate_43.vcf";
        String extraArgs[] = {"-a"};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Get first entry
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that CAF annotation is added
        assertTrue(infoStr.indexOf("CAF=") >= 0, "Missing CAF annotation");

        // Compare against expected output
        // Note: We don't care about annotation order in this case
        String expectedCaf = ".";
        String caf = ve.getInfo("CAF");
        assertEquals(expectedCaf, caf);
    }

    /**
     * Annotate GnomAd with multiple entries
     */
    @Test
    public void test_44() {
        Log.debug("Test");
        String dbFileName = "./test/ann/gnomad_db_multiple_entries.vcf";
        String fileName = "./test/ann/annotate_44.vcf";
        String extraArgs[] = {"-info", "AF_AFR"};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Get first entry
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that annotation is added
        assertTrue(infoStr.indexOf("AF_AFR=") >= 0, "Missing AF_AFR annotation");

        // Compare against expected output
        // Note: We don't care about annotation order in this case
        String expectedAfAfr = "6.60044e-03";
        String afAfr = ve.getInfo("AF_AFR");
        assertEquals(expectedAfAfr, afAfr);
    }

    /**
     * Annotate GnomAd with multiple entries
     */
    @Test
    public void test_45() {
        Log.debug("Test");
        String dbFileName = "./test/ann/gnomad_db_multiple_entries.vcf";
        String fileName = "./test/ann/annotate_45.vcf";
        String fieldName = "AC_AMR";
        String extraArgs[] = {"-info", fieldName};
        List<VcfEntry> results = annotate(dbFileName, fileName, extraArgs);

        // Get first entry
        VcfEntry ve = results.get(0);
        if (verbose) Log.info(ve);
        String infoStr = ve.getInfoStr();

        // Check that annotation is added
        assertTrue(infoStr.contains(fieldName + "="), "Missing " + fieldName + " annotation");

        // Compare against expected output
        // Note: We don't care about annotation order in this case
        String expectedValue = "2";
        String value = ve.getInfo(fieldName);
        assertEquals(expectedValue, value);
    }

}
