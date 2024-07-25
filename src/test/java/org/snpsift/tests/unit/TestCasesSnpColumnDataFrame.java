package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class TestCasesSnpColumnDataFrame {

    VariantTypeCounter variantTypeCounter(String[] fields, int size, int memSize) {
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        for( String field: fields) fields2type.put(field, VcfInfoType.String);
        fields2type.put("field_bool", VcfInfoType.Flag);
        fields2type.put("field_char", VcfInfoType.Character);
        fields2type.put("field_int", VcfInfoType.Integer);
        fields2type.put("field_float", VcfInfoType.Float);

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
        var varCounter = variantTypeCounter(new String[]{"field_string", "field_string_2"}, 10, 100);
        var dataFrame = new DataFrameSnp(varCounter, VariantCategory.SNP_A);
        String expectedAlt = "A";
        String actualAlt = dataFrame.getAlt();
        Assertions.assertEquals(expectedAlt, actualAlt);
    }

    @Test
    public void testDataFrame01() {
        var varCounter = variantTypeCounter(new String[]{"field_string", "field_string_2"}, 10, 100);
        var dataFrame = new DataFrameSnp(varCounter, VariantCategory.SNP_A);

        String columnName = "field_string";
        String value = "Value";
        int pos = 5;
        String ref = "N";
        String alt = "A";

        dataFrame.setData(columnName, value, pos, ref, alt);
        dataFrame.check();
        Object actualValue = dataFrame.getData(columnName, pos, ref, alt);
        Assertions.assertEquals(value, actualValue);

        Assertions.assertThrows(RuntimeException.class, () -> {
            dataFrame.setData(columnName, value, pos, ref, "C");
        });

    }

    @Test
    public void testDataFrame02() {
        var size = 100;
        var varCounter = variantTypeCounter(new String[]{"field_string", "field_string_2"}, size, size * 20);
        var dataFrame = new DataFrameSnp(varCounter, VariantCategory.SNP_A);
        // Create random data, 100 points
        // Initialize random seed
        var pos = 0;
        var alt = "A";
        RandomUtil randUtil = new RandomUtil();
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            var ref = randUtil.randAcgt(); // Reference: A random value from 'A', 'C', 'G', 'T'
            // Values
            var ri = randUtil.randIntOrNull();
            var rf = randUtil.randDoubleOrNull();
            var rc = randUtil.randCharOrNull();
            var rb = randUtil.randBoolOrNull();
            var rs = randUtil.randStringOrNull();
            var rs2 = randUtil.randStringOrNull();
            // Set data
            dataFrame.setData("field_int", ri, pos, ref, alt);
            dataFrame.setData("field_float", rf, pos, ref, alt);
            dataFrame.setData("field_char", rc, pos, ref, alt);
            dataFrame.setData("field_bool", rb, pos, ref, alt);
            dataFrame.setData("field_string", rs, pos, ref, alt);
            dataFrame.setData("field_string_2", rs2, pos, ref, alt);
        }

        randUtil.reset();
        pos = 0;
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            var ref = randUtil.randAcgt(); // Reference: A random value from 'A', 'C', 'G', 'T'
            // Values
            var ri = randUtil.randIntOrNull();
            var rf = randUtil.randDoubleOrNull();
            var rc = randUtil.randCharOrNull();
            var rb = randUtil.randBoolOrNull();
            var rs = randUtil.randStringOrNull();
            var rs2 = randUtil.randStringOrNull();
            System.out.println("pos: " + pos + "\tref: " + ref + "\talt: " + alt + "\tri: " + ri + "\trf: " + rf + "\trc: " + rc + "\trb: " + rb + "\trs: " + rs + "\trs2: " + rs2);
            // Check data
            assertEquals(ri, dataFrame.getData("field_int", pos, ref, alt));
            assertEquals(rf, dataFrame.getData("field_float", pos, ref, alt));
            assertEquals(rc, dataFrame.getData("field_char", pos, ref, alt));
            assertEquals(rb, dataFrame.getData("field_bool", pos, ref, alt));
            assertEquals(rs, dataFrame.getData("field_string", pos, ref, alt));
            assertEquals(rs2, dataFrame.getData("field_string_2", pos, ref, alt));
        }
    }

    //     Object value = "Value42";
    //     int pos = 5;
    //     String ref = "N";
    //     String alt = "A";

    //     Assertions.assertThrows(RuntimeException.class, () -> {
    //         dataFrame.setData(columnName, value, pos, ref, "C");
    //     });

    //     dataFrame.setData(columnName, value, pos, ref, alt);
    //     Object actualValue = dataFrame.getData(columnName, pos, ref, alt);
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
    //         dataFrame.setData(columnName, value, pos, ref, "C");
    //     });

    //     dataFrame.setData(columnName, value, pos, ref, alt);
    //     Object actualValue = dataFrame.getData(columnName, pos, ref, alt);
    //     Assertions.assertEquals(value, actualValue);
    // }
}