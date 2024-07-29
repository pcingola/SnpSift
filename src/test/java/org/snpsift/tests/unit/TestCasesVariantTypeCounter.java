package org.snpsift.tests.unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TestCasesVariantTypeCounter {

    private VariantTypeCounter variantTypeCounter;

    @BeforeEach
    public void setup() {
    }

    /** Create a VcfFileIterator from a string containig VCF lines */
    VcfFileIterator lines2VcfFileIterator(String vcfLines) {
        try (var bais = new ByteArrayInputStream(vcfLines.getBytes("UTF-8"))) {
            InputStreamReader isr = new InputStreamReader(bais);
            BufferedReader br = new BufferedReader(isr);
            return new VcfFileIterator(br);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Count variants in a VCF lines (i.e. a VCF file contents in a string)
     */
    VariantTypeCounter countVariants(String vcfLines) {
        VcfHeader vcfHeader = lines2VcfFileIterator(vcfLines).readHeader(); // Skip header

        // Create a map of names to types
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        for(VcfHeaderInfo vi : vcfHeader.getVcfHeaderInfo()) {
            if( ! vi.isImplicit() ) fields2type.put(vi.getId(), vi.getVcfInfoType());
        }

        // Create a variant type counter and count variants
        variantTypeCounter = new VariantTypeCounter(fields2type);
        for(VcfEntry vcfEntry : lines2VcfFileIterator(vcfLines)) {
            variantTypeCounter.count(vcfEntry);
        }
        return variantTypeCounter;
    }

    @Test
    public void testCount01() {
        // // Create a VCF line, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = countVariants(vcfLines);

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
    public void testCount02() {
        // // Create a VCF line, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            + "1\t1001\t.\tA\tC\t.\t.\tFIELD_STRING=Value02;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            + "1\t1002\t.\tA\tC\t.\t.\tFIELD_STRING=Value03;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = countVariants(vcfLines);

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
    public void testCount03() {
        // // Create a VCF line, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tA\tAC\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = countVariants(vcfLines);

        // No counter for other categories
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.INS));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.INS, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.INS, "FIELD_FLOAT"));
        assertEquals(7,variantTypeCounter.getSize(VariantCategory.INS, "FIELD_STRING"));
        assertEquals(0, variantTypeCounter.getSize(VariantCategory.INS, VariantTypeCounter.REF));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.INS, VariantTypeCounter.ALT));
    }

    @Test
    public void testCount04() {
        // // Create a VCF line, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "1\t1000\t.\tAC\tA\t.\t.\tFIELD_STRING=Value01;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;
        VariantTypeCounter variantTypeCounter = countVariants(vcfLines);

        // No counter for other categories
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.DEL));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_INT"));
        assertEquals(-1, variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_FLOAT"));
        assertEquals(7,variantTypeCounter.getSize(VariantCategory.DEL, "FIELD_STRING"));
        assertEquals(1, variantTypeCounter.getSize(VariantCategory.DEL, VariantTypeCounter.REF));
        assertEquals(0, variantTypeCounter.getSize(VariantCategory.DEL, VariantTypeCounter.ALT));
    }

}