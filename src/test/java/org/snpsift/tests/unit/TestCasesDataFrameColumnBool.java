package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnBool;
import org.snpsift.util.RandomUtil;


public class TestCasesDataFrameColumnBool {

    @Test
    public void testGet01() {
        DataFrameColumnBool column = new DataFrameColumnBool("test", 6);

        // Set values
        for(int i=0 ; i < 6 ; i++) {
            column.set(i, i % 2 == 0);
        }

        // Check values
        for(int i=0 ; i < 6 ; i++) {
            assertEquals(i % 2 == 0, column.get(i));
        }

        column.set(3, null);
        assertEquals(null, column.get(3));
    }

    @Test
    public void testSize() {
        DataFrameColumnBool column = new DataFrameColumnBool("test", 5);
        assertTrue(5 <= column.size());
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            DataFrameColumnBool column = new DataFrameColumnBool("test", size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                column.set(i, ru.randBoolOrNull());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var exp = ru.randBoolOrNull();
                assertEquals(exp, column.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + column.get(i) + " != " + exp);
            }
        }
    }
}