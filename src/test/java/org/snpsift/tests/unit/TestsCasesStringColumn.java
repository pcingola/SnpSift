package org.snpsift.tests.unit;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataColumn.StringColumn;

import static org.junit.jupiter.api.Assertions.*;

public class TestsCasesStringColumn {

    @Test
    public void testGetData() {
        String[] strings = {"Value1", "Value2", "Value3"};
        StringColumn stringColumn = StringColumn.of("Test", strings);
        assertEquals("Value1", stringColumn.get(0));
        assertEquals("Value2", stringColumn.get(1));
        assertEquals("Value3", stringColumn.get(2));
    }

    @Test
    public void testGetNull() {
        String[] strings = {"Value1", "Value2", "Value3", null, "Value4"};
        StringColumn stringColumn = StringColumn.of("Test", strings);
        System.err.println("STRING COLUMN: " + stringColumn);
        assertEquals("Value1", stringColumn.get(0));
        assertEquals("Value2", stringColumn.get(1));
        assertEquals("Value3", stringColumn.get(2));
        assertNull(stringColumn.get(3));
        assertEquals("Value4", stringColumn.get(4));
    }
}