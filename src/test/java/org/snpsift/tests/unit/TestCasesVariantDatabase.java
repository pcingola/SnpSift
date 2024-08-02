package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Genome;
import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.database.VariantDataFrame;
import org.snpsift.annotate.mem.database.VariantDatabase;

public class TestCasesVariantDatabase {

    @Test
    public void testCount01Snp() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=1,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;

        // Create a database
        String[] fields = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        VariantDatabase variantDatabase = new VariantDatabase(fields);
        variantDatabase.create(vcfLines);

        // Check the fields' types
        var fields2type = variantDatabase.getFields2type();
        System.out.println("Fields2type: " + fields2type);
        assertEquals(VcfInfoType.String, fields2type.get("FIELD_STRING"));
        assertEquals(VcfInfoType.Integer, fields2type.get("FIELD_INT"));
        assertEquals(VcfInfoType.Float, fields2type.get("FIELD_FLOAT"));
        assertEquals(VcfInfoType.Flag, fields2type.get("FIELD_FLAG"));
        // Check that the dataframe was created
        VariantDataFrame df = variantDatabase.get("1");
        assertNotNull(df);
        // Check that the dataframe was added to the database
        var dfSnpT = df.getDataFrameByCategory(VariantCategory.SNP_T);
        assertNotNull(dfSnpT);
        // Check that we can retrieve the row
        DataFrameRow dfrow = dfSnpT.getRow(999, "A", "T"); // Position is 0-based
        assertNotNull(dfrow);
        // Check that the value is correct
        assertEquals("Value1", dfrow.getDataFrameValue("FIELD_STRING"));
    }

    @Test
    public void testCount02Snps() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=1,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t999\t.\tA\tAC\t.\t.\tFIELD_STRING=Value_INS_A;FIELD_INT=4;FIELD_FLOAT=4.4\n" //
            + "1\t999\t.\tAC\tA\t.\t.\tFIELD_STRING=Value_DEL_A;FIELD_INT=5;FIELD_FLOAT=5.5\n" //
            + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value_C;FIELD_INT=1;FIELD_FLOAT=1.1\n" //
            + "1\t1000\t.\tA\tG\t.\t.\tFIELD_STRING=Value_G;FIELD_INT=2;FIELD_FLOAT=2.2\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value_T;FIELD_INT=3;FIELD_FLOAT=3.3\n" //
            ;

        // Create a database
        String[] fields = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        VariantDatabase variantDatabase = new VariantDatabase(fields);
        variantDatabase.create(vcfLines);

        VariantDataFrame vdf = variantDatabase.get("1");
        assertNotNull(vdf);

        // Check that the dataframe was added to the database
        String[] refs = { "A", "A", "A", "", "C" };
        String[] alts = { "C", "G", "T", "C", "" };
        String[] fieldString = { "Value_C", "Value_G", "Value_T", "Value_INS_A", "Value_DEL_A" };
        int[] fieldInt = { 1, 2, 3, 4, 5 };
        double[] fieldFloat = { 1.1, 2.2, 3.3, 4.4, 5.5 };
        Chromosome chr1 = new Chromosome(null, 0, 0, "1"); 
        for (int i = 0; i < refs.length; i++) {
            Variant variant = new Variant(chr1, 999, refs[i], alts[i]);
            VariantCategory variantCategory = VariantCategory.of(variant);
            var df = vdf.getDataFrameByCategory(variantCategory);
            assertNotNull(df);
            System.out.println(df);
            DataFrameRow dfrow = df.getRow(999, refs[i], alts[i]);
            assertNotNull(dfrow);
            assertEquals(fieldString[i], dfrow.getDataFrameValue("FIELD_STRING"));
            assertEquals(fieldInt[i], dfrow.getDataFrameValue("FIELD_INT"));
            assertEquals(fieldFloat[i], dfrow.getDataFrameValue("FIELD_FLOAT"));
        }
    }

    // @Test
    // public void testCount03Ins() {
    //     // Create some VCF lines, create a string buffer reader and a VcfFileIterator
    //     var vcfLines = "" //
    //         + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
    //         + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
    //         + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
    //         + "1\t1000\t.\tA\tAC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
    //         ;
    //     VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);
    // }

    // @Test
    // public void testCount04Del() {
    //     // Create some VCF lines, create a string buffer reader and a VcfFileIterator
    //     var vcfLines = "" //
    //         + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
    //         + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
    //         + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
    //         + "1\t1000\t.\tAC\tA\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
    //         ;
    //     VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);
    // }

    // @Test
    // public void testCount05SnpsSamePos() {
    //     // Create some VCF lines, create a string buffer reader and a VcfFileIterator
    //     var vcfLines = "" //
    //         + "##INFO=<ID=FIELD_STRING,Number=A,Type=String,Description=\"Test string INFO field depending on the allele\">\n" //
    //         + "##INFO=<ID=FIELD_INT,Number=A,Type=Integer,Description=\"Test int INFO field depending on the allele\">\n" //
    //         + "##INFO=<ID=FIELD_FLOAT,Number=A,Type=Float,Description=\"Test float INFO field depending on the allele\">\n" //
    //         + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value_C;FIELD_INT=1;FIELD_FLOAT=1.1\n" //
    //         + "1\t1000\t.\tA\tG\t.\t.\tFIELD_STRING=Value_G;FIELD_INT=2;FIELD_FLOAT=2.2\n" //
    //         + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value_T;FIELD_INT=3;FIELD_FLOAT=3.3\n" //
    //         ;
    //     VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);
    // }

}