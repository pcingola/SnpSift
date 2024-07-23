package org.snpsift.tests.unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

import htsjdk.samtools.util.BufferedLineReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        VcfHeader vcfHeader = lines2VcfFileIterator(vcfLines).getVcfHeader(); // Skip header
        for(VcfEntry vcfEntry : lines2VcfFileIterator(vcfLines)) {
            //
        }

        // Create a sample fields2type map
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        fields2type.put("FIELD1", VcfInfoType.String);
        fields2type.put("FIELD2", VcfInfoType.Integer);
        fields2type.put("FIELD3", VcfInfoType.Float);
        
        
        variantTypeCounter = new VariantTypeCounter(fields2type);
        for(VcfEntry vcfEntry : vcfFileIterator) {
            variantTypeCounter.count(vcfEntry);
        }

    }

    @Test
    public void testCount() {
        // Create a VCF line, create a string buffer reader and a VcfFileIterator
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\tFIELD1=Value1;FIELD2=123;FIELD3=3.14\n"
        ;

        // var stringBufferReader = new BufferedLineReader(null)
        // VcfFileIterator vcfFileIterator = new VcfFileIterator(vcfLine);
        // VcfEntry vcfEntry = new VcfEntry(null, vcfLine, 1, true); 
        
        variantTypeCounter.count(vcfEntry);

        assertEquals(1, variantTypeCounter.getCount(VariantCategory.SNP_T));
        // No counter for other categories
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_A));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_C));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.SNP_G));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.INS));
        assertEquals(0, variantTypeCounter.getCount(VariantCategory.DEL));
        assertEquals(7, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD1"));
        assertEquals(2, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD2"));
        assertEquals(0, variantTypeCounter.getSize(VariantCategory.SNP_T, "FIELD3"));
    }

    @Test
    public void testToString() {
        // Add your assertions here
        assertEquals(1, variantTypeCounter.getCount(VariantCategory.INS));
    }
}