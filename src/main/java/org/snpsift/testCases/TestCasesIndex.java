package org.snpsift.testCases;

import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Marker;
import org.snpeff.interval.Markers;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.VcfIndex;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesIndex {

    public static boolean debug = false;
    public static boolean verbose = false || debug;

    /**
     * Index a VCF file and query all entries
     */
    @Test
    public void test_01() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_index_01.vcf";

        // Make sure index file is deleted
        String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
        (new File(indexFileName)).delete();

        // Index VCF file
        VcfIndex vcfIndex = new VcfIndex(dbFileName);
        vcfIndex.setVerbose(verbose);
        vcfIndex.open();
        vcfIndex.index();

        // Check that all entries can be found & retrieved
        if (verbose) Log.debug("Checking");
        VcfFileIterator vcf = new VcfFileIterator(dbFileName);
        for (VcfEntry ve : vcf) {
            if (verbose) Log.info(ve.toStr());

            // Query database
            for (Variant var : ve.variants()) {
                Markers results = vcfIndex.query(var);

                // We should find at least one result
                assertTrue(results.size() > 0, "No results found for entry:\n\t" + ve);

                // Check each result
                for (Marker res : results) {
                    VcfEntry veIdx = (VcfEntry) res;
                    if (verbose) Log.info("\t" + res + "\t" + veIdx);

                    // Check that result does intersect query
                    assertTrue(ve.intersects(veIdx)//
                            , "Selected interval does not intersect marker form file!" //
                                    + "\n\tVcfEntry            : " + ve //
                                    + "\n\tVariant             : " + var //
                                    + "\n\tResult              : " + res //
                                    + "\n\tVcfEntry from result:" + veIdx //
                    );
                }
            }
        }

        vcfIndex.close();

    }

    /**
     * Index a VCF file and query all entries
     */
    @Test
    public void test_02() {
        Log.debug("Test");
        String dbFileName = "./test/db_test_index_02.vcf";

        // Index VCF file
        String indexFileName = dbFileName + "." + VcfIndex.INDEX_EXT;
        (new File(indexFileName)).delete();

        // Create index file
        VcfIndex vcfIndex = new VcfIndex(dbFileName);
        vcfIndex.setVerbose(verbose);
        vcfIndex.index();

        // Make sure index file was created
        assertTrue(Gpr.exists(indexFileName), "Index file '" + indexFileName + "' does not exist");

        // Restart so we force to read from index file
        vcfIndex = new VcfIndex(dbFileName);
        vcfIndex.setVerbose(verbose);
        vcfIndex.open();
        vcfIndex.index();

        // Check that all entries can be found & retrieved
        if (verbose) Log.debug("Checking");
        VcfFileIterator vcf = new VcfFileIterator(dbFileName);
        for (VcfEntry ve : vcf) {
            if (verbose) Log.info(ve.toStr());

            // Query database
            for (Variant var : ve.variants()) {
                Markers results = vcfIndex.query(var);

                // We should find at least one result
                assertTrue(results.size() > 0, "No results found for entry:\n\t" + ve);

                // Check each result
                for (Marker res : results) {
                    VcfEntry veIdx = (VcfEntry) res;
                    if (verbose) Log.info("\t" + res + "\t" + veIdx);

                    // Check that result does intersect query
                    assertTrue(ve.intersects(veIdx)//
                            , "Selected interval does not intersect marker form file!" //
                                    + "\n\tVcfEntry            : " + ve //
                                    + "\n\tVariant             : " + var //
                                    + "\n\tResult              : " + res //
                                    + "\n\tVcfEntry from result:" + veIdx //
                    );
                }
            }
        }

        vcfIndex.close();
    }

}
