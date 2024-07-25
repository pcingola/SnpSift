package org.snpsift.tests.unit;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataColumn.StringColumn;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.*;

public class TestCasesStringColumn {

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

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);

            var numStrings = ru.randInt(100000);

            // Calculate the size of the StringArray
            var size = 0;
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                var r = ru.randStringOrNull();
                size += r == null ? 0 : r.length();
            }

            // Create a StringArray with random strings
            StringColumn sarray = new StringColumn("test", numStrings + 1, size + numStrings);
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                sarray.set(i, ru.randStringOrNull());
            }

            // Check that all strings match
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                var exp = ru.randStringOrNull();
                assertEquals(exp, sarray.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + sarray.get(i) + " != " + exp);
            }
        }
    }

}