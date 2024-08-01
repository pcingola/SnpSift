package org.snpsift.tests.unit;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCasesVariantTypeCounter {

    @Test
    public void testCount01Snp() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);

        assertEquals(1, variantTypeCounter.getCount(VariantCategory.SNP_T));
        // No counter for other categories
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_A));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_C));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_G));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.INS));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.DEL));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD_FLOAT"));
        assertEquals(6, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD_STRING"));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.SNP_T, VariantTypeCounter.REF));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.SNP_T, VariantTypeCounter.ALT));
    }

    @Test
    public void testCount02Snps() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            + "1\t1001\t.\tA\tC\t.\t.\tFIELD_STRING=Value02;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            + "1\t1002\t.\tA\tC\t.\t.\tFIELD_STRING=Value03;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);

        assertEquals(3, variantTypeCounter.getCount(VariantCategory.SNP_C));
        // No counter for other categories
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_A));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_T));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_G));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.INS));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.DEL));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.SNP_C, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.SNP_C, "FIELD_FLOAT"));
        assertEquals(21,variantTypeCounter.getSize(VariantCategory.SNP_C, "FIELD_STRING"));
        assertEquals(3, variantTypeCounter.getSize(VariantCategory.SNP_C, VariantTypeCounter.REF));
        assertEquals(3, variantTypeCounter.getSize(VariantCategory.SNP_C, VariantTypeCounter.ALT));
    }

    @Test
    public void testCount03Ins() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tAC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);

        // No counter for other categories
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.INS));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.INS, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.INS, "FIELD_FLOAT"));
        assertEquals(7,variantTypeCounter.getSize(VariantCategory.INS, "FIELD_STRING"));
        assertEquals(0, variantTypeCounter.getSize(VariantCategory.INS, VariantTypeCounter.REF));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.INS, VariantTypeCounter.ALT));
    }

    @Test
    public void testCount04Del() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tAC\tA\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);

        // No counter for other categories
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.DEL));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_FLOAT"));
        assertEquals(7,variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_STRING"));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.DEL, VariantTypeCounter.REF));
        assertEquals(0, variantTypeCounter.getSize(VariantCategory.DEL, VariantTypeCounter.ALT));
    }

    @Test
    public void testCount05SnpsSamePos() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=A,Type=String,Description=\"Test string INFO field depending on the allele\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=A,Type=Integer,Description=\"Test int INFO field depending on the allele\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=A,Type=Float,Description=\"Test float INFO field depending on the allele\">\n" //
            + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value_C;FIELD_INT=1;FIELD_FLOAT=1.1\n" //
            + "1\t1000\t.\tA\tG\t.\t.\tFIELD_STRING=Value_G;FIELD_INT=2;FIELD_FLOAT=2.2\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value_T;FIELD_INT=3;FIELD_FLOAT=3.3\n" //
            ;
        VariantTypeCounter variantTypeCounter = VariantTypeCounter.countVariants(vcfLines);

        // No counter for other categories
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.SNP_C));
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.SNP_G));
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.SNP_T));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_FLOAT"));
        assertEquals(7,variantTypeCounter.getSize(VariantCategory.SNP_C, "FIELD_STRING"));
        assertEquals(7, variantTypeCounter.getSize(VariantCategory.SNP_G, "FIELD_STRING"));
        assertEquals(7, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD_STRING"));
    }

}