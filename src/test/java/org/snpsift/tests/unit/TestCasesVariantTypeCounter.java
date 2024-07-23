package org.snpsift.tests.unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCasesVariantTypeCounter {

    private VariantTypeCounter variantTypeCounter;

    @BeforeEach
    public void setup() {
        // Create a sample fields2type map
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        fields2type.put("FIELD1", VcfInfoType.String);
        fields2type.put("FIELD2", VcfInfoType.Integer);
        fields2type.put("FIELD3", VcfInfoType.Float);

        variantTypeCounter = new VariantTypeCounter(fields2type);
    }

    @Test
    public void testCount() {
        var vcfLine = "1\t1000\t.\tA\tT\t.\t.\t.\tFIELD1=Value1;FIELD2=123;FIELD3=3.14";
        VcfEntry vcfEntry = new VcfEntry(null, vcfLine, 1, true); 
        
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