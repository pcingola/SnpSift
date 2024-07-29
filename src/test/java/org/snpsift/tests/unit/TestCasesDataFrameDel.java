package org.snpsift.tests.unit;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.dataFrame.DataFrameMnp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCasesDataFrameDel extends TestCasesDataFrame {

    @Test
    public void testDataFrame01() {
        var varCounter = variantTypeCounter(10, 100);
        var dataFrame = new DataFrameMnp(varCounter, VariantCategory.DEL);

        int pos = 5;
        String ref = "AC";
        String alt = "";
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
        testDataFrame(VariantCategory.DEL, 100, 100);
   }
}