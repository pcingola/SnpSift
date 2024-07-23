package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

import java.util.HashMap;
import java.util.Map;

public class TestCasesSnpColumnDataFrame {

    VariantTypeCounter variantTypeCounter(String[] fields, int size, int memSize) {
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        for( String field: fields) fields2type.put(field, VcfInfoType.String);

        VariantTypeCounter variantTypeCounter = new VariantTypeCounter(fields2type);

        // Set size and memory size for each category
        for(VariantCategory category: VariantCategory.values()) {
            variantTypeCounter.getCountByCategory()[category.ordinal()] = size;
            for(String field: fields) {
                variantTypeCounter.getSizesByField().get(field)[category.ordinal()] = memSize;
            }
        }

        return variantTypeCounter;
    }

    @Test
    public void testGetAlt() {
        var varCounter = variantTypeCounter(new String[]{"field1", "field2"}, 10, 100);
        var dataSet = new DataFrameSnp(varCounter, VariantCategory.SNP_A);
        String expectedAlt = "A";
        String actualAlt = dataSet.getAlt();
        Assertions.assertEquals(expectedAlt, actualAlt);
    }

    @Test
    public void testDataFrame01() {
        var varCounter = variantTypeCounter(new String[]{"field1", "field2"}, 10, 100);
        var dataSet = new DataFrameSnp(varCounter, VariantCategory.SNP_A);

        String columnName = "field1";
        String value = "Value";
        int pos = 5;
        String ref = "N";
        String alt = "A";

        dataSet.setData(columnName, value, pos, ref, alt);
        dataSet.check();
        Object actualValue = dataSet.getData(columnName, pos, ref, alt);
        Assertions.assertEquals(value, actualValue);

        Assertions.assertThrows(RuntimeException.class, () -> {
            dataSet.setData(columnName, value, pos, ref, "C");
        });

    }

    // @Test
    // public void testDataFrame02() {
    //     String columnName = "column1";
    //     Object value = "Value42";
    //     int pos = 5;
    //     String ref = "N";
    //     String alt = "A";

    //     Assertions.assertThrows(RuntimeException.class, () -> {
    //         dataSet.setData(columnName, value, pos, ref, "C");
    //     });

    //     dataSet.setData(columnName, value, pos, ref, alt);
    //     Object actualValue = dataSet.getData(columnName, pos, ref, alt);
    //     Assertions.assertEquals(value, actualValue);
    // }

    // @Test
    // public void testDataFrame03() {
    //     String columnName = "column1";
    //     Object value = "Value42";
    //     int pos = 5;
    //     String ref = "N";
    //     String alt = "A";

    //     Assertions.assertThrows(RuntimeException.class, () -> {
    //         dataSet.setData(columnName, value, pos, ref, "C");
    //     });

    //     dataSet.setData(columnName, value, pos, ref, alt);
    //     Object actualValue = dataSet.getData(columnName, pos, ref, alt);
    //     Assertions.assertEquals(value, actualValue);
    // }
}