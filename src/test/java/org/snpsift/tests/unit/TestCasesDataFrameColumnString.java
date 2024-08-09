package org.snpsift.tests.unit;
import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.arrays.EnumArray;
import org.snpsift.annotate.mem.arrays.StringArrayBase;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnString;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.*;

public class TestCasesDataFrameColumnString {

    @Test
    public void testGetData() {
        String[] strings = {"Value1", "Value2", "Value3"};
        DataFrameColumnString stringColumn = DataFrameColumnString.of("Test", strings);
        assertEquals("Value1", stringColumn.get(0));
        assertEquals("Value2", stringColumn.get(1));
        assertEquals("Value3", stringColumn.get(2));
    }

    @Test
    public void testGetNull() {
        String[] strings = {"Value1", "Value2", "Value3", null, "Value4"};
        DataFrameColumnString stringColumn = DataFrameColumnString.of("Test", strings);
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
            DataFrameColumnString stringColumn = new DataFrameColumnString("test", numStrings + 1, size + numStrings);
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                stringColumn.set(i, ru.randStringOrNull());
            }

            // Check that all strings match
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                var exp = ru.randStringOrNull();
                assertEquals(exp, stringColumn.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + stringColumn.get(i) + " != " + exp);
            }
        }
    }

    @Test
    public void testResize01() {
        String[] strings = {"one", "two", "three", "four", "five"};
        DataFrameColumnString stringColumn = DataFrameColumnString.of("Test", strings);
        // Test that the column before resize is a StringArray
        assertTrue(stringColumn.getData() instanceof StringArrayBase);
        // Test contents of the column before resize
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], stringColumn.get(i));
        }
        stringColumn.resize();
        // Test that the column after resize is an EnumArray
        assertTrue(stringColumn.getData() instanceof EnumArray);
        // Test contents of the column after resize
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], stringColumn.get(i));
        }
    }


    @Test
    public void testResize02() {
        RandomUtil ru = new RandomUtil();

        var numStrings = ru.randInt(100000);

        // Calculate the size of the StringArray
        var size = 0;
        ru.reset();
        for(int i = 0; i < numStrings; i++) {
            var r = ru.randStringOrNull();
            size += r == null ? 0 : r.length();
        }

        // Create a StringArray with random strings
        DataFrameColumnString stringColumn = new DataFrameColumnString("test", numStrings + 1, size + numStrings);
        ru.reset();
        for(int i = 0; i < numStrings; i++) {
            stringColumn.set(i, ru.randStringOrNull());
        }

        assertTrue(stringColumn.getData() instanceof StringArrayBase);
        stringColumn.resize();
        assertTrue(stringColumn.getData() instanceof StringArrayBase);

        // Check that all strings match
        ru.reset();
        for(int i = 0; i < numStrings; i++) {
            var exp = ru.randStringOrNull();
            assertEquals(exp, stringColumn.get(i), "Mismatch at index " + i + ": " + stringColumn.get(i) + " != " + exp);
        }
}
}