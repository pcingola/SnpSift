package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnChar;
import org.snpsift.util.RandomUtil;


public class TestCasesDataFrameColumnChar {

    @Test
    public void testGet01() {
        DataFrameColumnChar column = new DataFrameColumnChar("test", 6);
        column.set(0, '1');
        column.set(1, '2');
        column.set(2, '3');
        column.set(3, null);
        column.set(4, '4');
        column.set(5, '5');

        assertEquals('1', column.get(0));
        assertEquals('2', column.get(1));
        assertEquals('3', column.get(2));
        assertEquals(null, column.get(3));
        assertEquals('4', column.get(4));
        assertEquals('5', column.get(5));
        assertEquals(6, column.size());
    }

    @Test
    public void testSize() {
        DataFrameColumnChar column = new DataFrameColumnChar("test", 5);
        assertEquals(5, column.size());
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            DataFrameColumnChar column = new DataFrameColumnChar("test", size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                column.set(i, ru.randCharOrNull());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var exp = ru.randCharOrNull();
                assertEquals(exp, column.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + column.get(i) + " != " + exp);
            }
        }
    }
}