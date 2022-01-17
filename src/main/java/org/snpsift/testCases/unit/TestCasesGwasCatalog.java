package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdGwasCatalog;
import org.snpsift.gwasCatalog.GwasCatalog;
import org.snpsift.gwasCatalog.GwasCatalogEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test GWAS catalog classes
 *
 * @author pcingola
 */
public class TestCasesGwasCatalog {

    public static boolean verbose = false;

    List<VcfEntry> snpSiftCmdGwasCatalog(String[] args) {
        SnpSift snpSift = new SnpSift(args);
        SnpSiftCmdGwasCatalog snpSiftGwasCat = (SnpSiftCmdGwasCatalog) snpSift.cmd();
        snpSiftGwasCat.setVerbose(verbose);
        snpSiftGwasCat.setSuppressOutput(!verbose);
        return snpSiftGwasCat.run(true);
    }

    @Test
    public void test_01() {
        Log.debug("Test");

        // Load catalog
        GwasCatalog gwasCatalog = new GwasCatalog("test/gwasCatalog/gwascatalog.txt.gz");

        // Search by chr:pos
        List<GwasCatalogEntry> list = gwasCatalog.get("20", 1759590 - 1);
        assertEquals(list.get(0).snps, "rs6080550");

        // Search by RS number
        list = gwasCatalog.getByRs("rs6080550");
        assertEquals(list.get(0).snps, "rs6080550");
    }

    @Test
    public void test_02() {
        Log.debug("Test");

        String[] args = {"gwasCat" //
                , "-db" //
                , "test/gwasCatalog/gwascatalog.20130907.tsv" //
                , "test/test_gwascat_02.vcf" //
        };

        List<VcfEntry> vcfEntries = snpSiftCmdGwasCatalog(args);

        int countOk = 0;
        for (VcfEntry ve : vcfEntries) {
            if (verbose) Log.debug(ve);

            if (ve.getInfo("GWC").equals("Y") // We added this INFO fields for VCF entries in the test case that matching GWACAT database
                    && ve.getInfo("GWASCAT_TRAIT") != null) {
                countOk++;
            }
        }

        assertEquals(2, countOk, "Two VCF entries should have been annotated");
    }

}
