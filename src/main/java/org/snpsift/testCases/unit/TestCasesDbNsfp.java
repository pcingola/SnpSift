package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdDbNsfp;
import org.snpsift.fileIterator.DbNsfp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for dbNSFP database annotations
 * Note: This class tries to use 'data type cache' files
 *
 * @author pcingola
 */
public class TestCasesDbNsfp {

    public static boolean verbose = false;
    public static boolean debug = false;

    protected String[] defaultExtraArgs = null;
    protected boolean removeDataTypesCache;

    public TestCasesDbNsfp() {
        removeDataTypesCache = false;
    }

    public List<VcfEntry> annotate(String dbFileName, String fileName, String[] extraArgs) {
        if (verbose) Log.info("Annotate: " + dbFileName + "\t" + fileName);

        removeDataTypesCache(dbFileName);

        // Create command line
        String[] args = argsList(dbFileName, fileName, extraArgs);
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdDbNsfp cmd = (SnpSiftCmdDbNsfp) snpSift.cmd();

        cmd.setDbFileName(dbFileName);
        cmd.setVerbose(verbose);
        cmd.setSuppressOutput(!verbose);
        cmd.setDebug(debug);
        cmd.setTabixCheck(false);

        List<VcfEntry> results = cmd.run(true);

        assertNotNull(results);
        assertTrue(results.size() > 0);

        return results;
    }

    public Map<String, String> annotateGetFiledTypes(String dbFileName, String fileName, String[] extraArgs) {
        if (verbose) Log.info("Annotate: " + dbFileName + "\t" + fileName);

        removeDataTypesCache(dbFileName);

        // Create command line
        String[] args = argsList(dbFileName, fileName, extraArgs);
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdDbNsfp cmd = (SnpSiftCmdDbNsfp) snpSift.cmd();
        cmd.setDbFileName(dbFileName);
        cmd.setVerbose(verbose);
        cmd.setSuppressOutput(!verbose);
        cmd.setDebug(debug);
        cmd.setTabixCheck(false);

        List<VcfEntry> results = cmd.run(true);

        assertNotNull(results);
        assertTrue(results.size() > 0);

        return cmd.getFieldsType();
    }

    protected String[] argsList(String dbFileName, String fileName, String[] extraArgs) {
        ArrayList<String> argsList = new ArrayList<>();

        argsList.add("dbNsfp");

        if (defaultExtraArgs != null) {
            for (String arg : defaultExtraArgs)
                argsList.add(arg);
        }

        if (extraArgs != null) {
            for (String arg : extraArgs)
                argsList.add(arg);
        }

        argsList.add(fileName);
        return argsList.toArray(new String[0]);
    }

    void removeDataTypesCache(String dbFileName) {
        if (removeDataTypesCache) {
            String dtcFileName = dbFileName + DbNsfp.DATA_TYPES_CACHE_EXT;
            File dtc = new File(dtcFileName);
            if (dtc.delete()) {
                if (verbose) Log.debug("Removing data types cache file: " + dtcFileName);
            }
        }
    }

    @Test
    public void test_01() {
        Log.debug("Test");
        String vcfFileName = "test/test_dbNSFP_chr1_69134.vcf";
        String dbFileName = "test/dbNSFP2.0b3.chr1_69134.txt.gz";
        String[] args = {"-collapse", "-f", "GERP++_RS,GERP++_NR,ESP6500_AA_AF,29way_logOdds,Polyphen2_HVAR_pred,SIFT_score,Uniprot_acc,Ensembl_transcriptid"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "GERP___RS"));
        assertEquals("2.31", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "GERP___NR"));
        assertEquals("0.004785", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "ESP6500_AA_AF"));
        assertEquals("8.5094", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "29way_logOdds"));
        assertEquals("B", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Polyphen2_HVAR_pred"));
        assertEquals("0.090000", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "SIFT_score"));
        assertEquals("Q8NH21", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Uniprot_acc"));
        assertEquals("ENST00000534990,ENST00000335137", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Ensembl_transcriptid"));

    }

    /**
     * Test dbnsfp having multiple lines per variant
     */
    @Test
    public void test_02() {
        Log.debug("Test");
        String vcfFileName = "test/test_dbnsfp_multiple.vcf";
        String dbFileName = "test/test_dbnsfp_multiple_lines.txt.gz";
        String fields = "genename,Ensembl_geneid,Ensembl_transcriptid,aaref,aaalt";
        String[] args = {"-collapse", "-f", fields};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals("ENST00000368485,ENST00000515190", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Ensembl_transcriptid"));
        assertEquals("IL6R", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "genename"));
        assertEquals("ENSG00000160712", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Ensembl_geneid"));
        assertEquals("A,L", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "aaalt"));
        assertEquals("D,I", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "aaref"));
    }

    /**
     * Test dbnsfp having multiple lines per variant, without collapsing
     */
    @Test
    public void test_03() {
        Log.debug("Test");
        String vcfFileName = "test/test_dbnsfp_multiple_noCollapse.vcf";
        String dbFileName = "test/test_dbnsfp_multiple_noCollapse.txt.gz";
        String fields = "aaalt,Ensembl_transcriptid,Polyphen2_HDIV_score,Polyphen2_HVAR_pred";
        String[] args = {"-nocollapse", "-f", fields};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        // dbNSFP_Ensembl_transcriptid=ENST00000347404,ENST00000537835,ENST00000378535,ENST00000228280;dbNSFP_Polyphen2_HDIV_score=0.449,.,0.192;dbNSFP_Polyphen2_HVAR_pred=B,.,B;dbNSFP_aaalt=A,R,T
        assertEquals("ENST00000347404,ENST00000537835,ENST00000378535,ENST00000228280", vcfEntry.getInfo("dbNSFP_Ensembl_transcriptid"));
        assertEquals("0.449,.,0.192", vcfEntry.getInfo("dbNSFP_Polyphen2_HDIV_score"));
        assertEquals("B,.,B", vcfEntry.getInfo("dbNSFP_Polyphen2_HVAR_pred"));
        assertEquals("A,R,T", vcfEntry.getInfo("dbNSFP_aaalt"));
    }

    @Test
    public void test_04() {
        Log.debug("Test");
        // We annotate something trivial: position
        String vcfFileName = "test/test_dbNSFP_04.vcf";
        String dbFileName = "test/dbNSFP2.3.test.txt.gz";
        String[] args = {"-collapse", "-f", "pos(1-coor)"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);

        // Get entry.
        // Note: There is only one entry to annotate (the VCF file has one line)
        for (VcfEntry vcfEntry : results) {

            // Check that position (annotated from dbNSFP) actually matches
            String posDb = vcfEntry.getInfo("dbNSFP_pos_1_coor_"); // Get INFO field annotated from dbNSFP
            int pos = vcfEntry.getStart() + 1; // Get position
            assertEquals("" + pos, posDb); // Compare
        }
    }

    @Test
    public void test_05() {
        Log.debug("Test");
        // We annotate something trivial: position
        String vcfFileName = "test/test_dbNSFP_05.vcf";
        String dbFileName = "test/dbNSFP2.3.test.txt.gz";
        String[] args = {"-f", "pos(1-coor)"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);

        for (VcfEntry vcfEntry : results) {
            // Check that position (annotated from dbNSFP) actually matches
            String posDb = vcfEntry.getInfo("dbNSFP_pos_1_coor_"); // Get INFO field annotated from dbNSFP
            int pos = vcfEntry.getStart() + 1; // Get position
            if (debug) Log.debug(vcfEntry.getChromosomeName() + ":" + pos + "\t" + posDb);

            // Check
            if (pos != 249213000) assertEquals("" + pos, posDb);
            else assertNull(posDb); // There is no dbNSFP for this entry. It should be 'null'
        }

    }

    @Test
    public void test_06() {
        Log.debug("Test");
        // We annotate something trivial: position
        String vcfFileName = "test/test_dbNSFP_06.vcf";
        String dbFileName = "test/dbNSFP2.3.test.txt.gz";
        String[] args = {"-f", "pos(1-coor)"};

        annotate(dbFileName, vcfFileName, args);
    }

    /**
     * Check header values are correctly inferred.
     * <p>
     * E.g. If the first value in the table is '0', we may infer
     * that data type is INT, but if another value '0.5'
     * appears, we change it to FLOAT
     */
    @Test
    public void test_07() {
        // We annotate something trivial: position
        String vcfFileName = "test/test.dbnsfp.07.vcf";
        String dbFileName = "test/dbNSFP2.4.head_100.txt.gz";
        String[] args = {"-f", "SIFT_score"};

        // Check field type
        Map<String, String> fieldTypes = annotateGetFiledTypes(dbFileName, vcfFileName, args);
        assertEquals("Float", fieldTypes.get("SIFT_score"));
    }

    /**
     * Missing annotations
     */
    @Test
    public void test_08() {
        String vcfFileName = "test/test_dbNSFP_8.vcf";
        String dbFileName = "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz";
        String[] args = {"-collapse", "-a", "-f", "Polyphen2_HDIV_score"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals(".,1.0,1.0", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Polyphen2_HDIV_score"));
    }

    /**
     * Invalid INFO keys
     */
    @Test
    public void test_09() {
        String vcfFileName = "test/test_dbNSFP_8.vcf";
        String dbFileName = "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz";
        String[] args = {"-a", "-f", "GERP++_NR"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals("5.62,5.62,5.62", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "GERP___NR"));
    }

    /**
     * Missing database entries
     */
    @Test
    public void test_10() {
        String vcfFileName = "test/test_dbNSFP_10.vcf";
        String dbFileName = "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz";
        String[] args = {"-collapse", "-a", "-m", "-f", "Polyphen2_HDIV_score"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals(".", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Polyphen2_HDIV_score"));
    }

    /**
     * Missing database entries + non-missing entry
     */
    @Test
    public void test_11() {
        String vcfFileName = "test/test_dbNSFP_11.vcf";
        String dbFileName = "test/dbNSFP2.4.chr4_55946200_55946300.txt.gz";
        String[] args = {"-collapse", "-a", "-m", "-f", "Polyphen2_HDIV_score"};

        List<VcfEntry> results = annotate(dbFileName, vcfFileName, args);
        VcfEntry vcfEntry = results.get(0);

        // Check all values
        assertEquals(".,1.0", vcfEntry.getInfo(SnpSiftCmdDbNsfp.DBNSFP_VCF_INFO_PREFIX + "Polyphen2_HDIV_score"));
    }

}
