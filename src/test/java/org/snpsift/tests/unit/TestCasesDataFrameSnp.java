package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCasesDataFrameSnp extends TestCasesDataFrame {

    @Test
    public void testGetAlt() {
        var varCounter = variantTypeCounter(10, 100);
        var dataFrame = new DataFrameSnp(varCounter, VariantCategory.SNP_A);
        String expectedAlt = "A";
        String actualAlt = dataFrame.getAlt();
        Assertions.assertEquals(expectedAlt, actualAlt);
    }

    @Test
    public void testDataFrame01() {
        var varCounter = variantTypeCounter(10, 100);
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
        testDataFrame(VariantCategory.SNP_A, 100, 100);
   }

    @Test
    public void testDataFrame03() {
        testDataFrame(VariantCategory.SNP_C, 100, 100);
    }

    @Test
    public void testDataFrame04() {
        testDataFrame(VariantCategory.SNP_G, 100, 100);
    }

    @Test
    public void testDataFrame05() {
        testDataFrame(VariantCategory.SNP_T, 100, 100);
    }

}