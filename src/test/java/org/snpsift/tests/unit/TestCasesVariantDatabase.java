package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfInfoType;
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
        // // Check that the dataframe was created
        // VariantDataFrame df = variantDatabase.get("1");
        // assertNotNull(df);
        // // Check that the dataframe was added to the database
        // var dfSnpT = df.getDataFrameByCategory(VariantCategory.SNP_T);
        // assertNotNull(dfSnpT);
        // // Check that we can retrieve the row
        // DataFrameRow dfrow = dfSnpT.getRow(1000, "A", "T");
        // assertNotNull(dfrow);
        // // Check that the value is correct
        // assertEquals("Value1", dfrow.getDataFrameValue("FIELD_STRING"));
    }

    // @Test
    // public void testCount02Snps() {
    //     // Create some VCF lines, create a string buffer reader and a VcfFileIterator
    //     var vcfLines = "" //
    //         + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
    //         + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
    //         + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
    //         + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
    //         + "1\t1001\t.\tA\tC\t.\t.\tFIELD_STRING=Value02;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
    //         + "1\t1002\t.\tA\tC\t.\t.\tFIELD_STRING=Value03;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
    //         ;
    //     VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);
    // }

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