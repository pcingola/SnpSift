package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestCasesDataFrameSnp {

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

        int pos = 5;
        String ref = "N";
        String alt = "A";
        String columnName = "field_string";
        String value = "Value";

        // Create a row
        DataFrameRow dataFrameRow = new DataFrameRow(dataFrame, pos, ref, alt);
        dataFrameRow.set(columnName, value);

        // Add the row to the data frame
        dataFrame.addRow(dataFrameRow);
        dataFrame.check();

        // Retrieve the row and check the value
        var retrievedRow = dataFrame.getRow(pos, ref, alt);
        assertNotNull(retrievedRow);
        assertEquals(value, retrievedRow.getDataFrameValue(columnName));
    }

    @Test
    public void testDataFrame02() {
        var size = 100;
        var stringMaxLen = 100;
        var varCounter = variantTypeCounter(new String[]{"field_string", "field_string_2"}, size, size * stringMaxLen);
        var dataFrame = new DataFrameSnp(varCounter, VariantCategory.SNP_A);

        // Create random data, 'size' rows
        // Initialize random seed
        int pos = 0, maxPos = 0;
        var alt = "A";
        Set<Integer> positions = new HashSet<>();
        RandomUtil randUtil = new RandomUtil();
        randUtil.reset();
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            maxPos = pos;
            positions.add(pos);
            var ref = randUtil.randAcgt(); // Reference: A random value from 'A', 'C', 'G', 'T'
            // Values
            var ri = randUtil.randIntOrNull();
            var rf = randUtil.randDoubleOrNull();
            var rc = randUtil.randCharOrNull();
            var rb = randUtil.randBoolOrNull();
            var rs = randUtil.randStringOrNull(stringMaxLen);
            var rs2 = randUtil.randStringOrNull(stringMaxLen);

            // Create a dataframe row, and add the random data
            DataFrameRow dataFrameRow = new DataFrameRow(dataFrame, pos, ref, alt);
            dataFrameRow.set("field_int", ri);
            dataFrameRow.set("field_float", rf);
            dataFrameRow.set("field_char", rc);
            dataFrameRow.set("field_bool", rb);
            dataFrameRow.set("field_string", rs);
            dataFrameRow.set("field_string_2", rs2);

            // Add the row to the data frame
            dataFrame.addRow(dataFrameRow);
        }

        System.out.println("DataFrame : " + dataFrame);
        // Check data
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
            var rs = randUtil.randStringOrNull(stringMaxLen);
            var rs2 = randUtil.randStringOrNull(stringMaxLen);

            // Query row from the data frame
            var dataFrameRow = dataFrame.getRow(pos, ref, alt);
            assertNotNull(dataFrameRow);

            // Check data
            assertEquals(ri, dataFrameRow.getDataFrameValue("field_int"));
            assertEquals(rf, dataFrameRow.getDataFrameValue("field_float"));
            assertEquals(rc, dataFrameRow.getDataFrameValue("field_char"));
            assertEquals(rb, dataFrameRow.getDataFrameValue("field_bool"));
            assertEquals(rs, dataFrameRow.getDataFrameValue("field_string"));
            assertEquals(rs2, dataFrameRow.getDataFrameValue("field_string_2"));
        }

        // Check positions that should not be found
        for(int i=0; i < size; i++) {
            var pos2 = randUtil.randInt(maxPos); // Random position
            if( !positions.contains(pos2)) {
                var ref = randUtil.randAcgt(); // Reference: A random value from 'A', 'C', 'G', 'T'
                var dataFrameRow = dataFrame.getRow(pos2, ref, alt);
                Assertions.assertNull(dataFrameRow);
            }
        }
    }
}