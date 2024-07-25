package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataColumn.DoubleColumn;
import org.snpsift.util.RandomUtil;


public class TestCasesFloatColumn {

    @Test
    public void testGet01() {
        DoubleColumn column = new DoubleColumn("test", 6);
        column.set(0, 10.123456);
        column.set(1, 20.123456);
        column.set(2, 30.123456);
        column.set(3, null);
        column.set(4, 40.123456);
        column.set(5, 50.123456);

        assertEquals(10.123456, column.get(0));
        assertEquals(20.123456, column.get(1));
        assertEquals(30.123456, column.get(2));
        assertEquals(null, column.get(3));
        assertEquals(40.123456, column.get(4));
        assertEquals(50.123456, column.get(5));
        assertEquals(6, column.size());
    }

    @Test
    public void testSize() {
        DoubleColumn column = new DoubleColumn("test", 5);
        assertEquals(5, column.size());
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            DoubleColumn column = new DoubleColumn("test", size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                column.set(i, ru.randDoubleOrNull());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var r = ru.randDoubleOrNull();
                assertEquals(r, column.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + column.get(i) + " != " + r);
            }
        }
    }

}