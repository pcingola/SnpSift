package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.dataFrame.DataFrameMnp;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

public class TestCasesDataFrameMnp extends TestCasesDataFrame {

    @Test
    public void testDataFrame01() {
        var varCounter = variantTypeCounter(10, 100);
        var dataFrame = new DataFrameMnp(varCounter, VariantCategory.MNP);

        int pos = 5;
        String ref = "AC";
        String alt = "GT";
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
        assertEquals(ref, retrievedRow.getRef());
        assertEquals(alt, retrievedRow.getAlt());
    }

    @Test
    public void testDataFrame02() {
        var size = 100;
        var stringMaxLen = 100;
        var varCounter = variantTypeCounter(size, size * stringMaxLen);
        var dataFrame = new DataFrameMnp(varCounter, VariantCategory.SNP_A);

        // Create random data, 'size' rows
        int pos = 0, maxPos = 0;
        var alt = "A";
        Set<Integer> positions = new HashSet<>();
        RandomUtil randUtil = new RandomUtil();
        randUtil.reset();   // Initialize random seed
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            maxPos = pos;
            positions.add(pos);
            // Create a dataframe row, and add the random data
            DataFrameRow dataFrameRow = randDataFrameRow(randUtil, dataFrame, pos, stringMaxLen, null, null);
            // Add the row to the data frame
            dataFrame.addRow(dataFrameRow);
        }

        // Check data
        var dataFrameExp = new DataFrameMnp(varCounter, VariantCategory.SNP_A);
        randUtil.reset();   // Initialize random seed
        pos = 0;
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            // Create a random row using the exact same random seed (i.e. the same data)
            DataFrameRow dataFrameRowExp = randDataFrameRow(randUtil, dataFrameExp, pos, stringMaxLen, null, null);
            // Query row from the data frame
            var dataFrameRow = dataFrame.getRow(pos, dataFrameRowExp.getRef(), dataFrameRowExp.getAlt());
            assertNotNull(dataFrameRow);
            assertEquals(dataFrameRowExp.getRef(), dataFrameRow.getRef());
            assertEquals(dataFrameRowExp.getAlt(), dataFrameRow.getAlt());

            // Check data
            for(String field: FIELDS) {
                assertEquals(dataFrameRowExp.get(field), dataFrameRow.getDataFrameValue(field), "Difference at Field: " + field //
                                + ", pos: " + pos //
                                + ", ref: " + dataFrameRowExp.getRef() //
                                + ", alt: " + dataFrameRowExp.getAlt() //
                                + ", expexted: " + dataFrameRowExp.get(field) //
                                + ", actual: " + dataFrameRow.getDataFrameValue(field)//
                );
            }
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