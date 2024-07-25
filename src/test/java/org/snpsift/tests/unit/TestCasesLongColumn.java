package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataColumn.LongColumn;
import org.snpsift.util.RandomUtil;


public class TestCasesLongColumn {

    @Test
    public void testGet01() {
        LongColumn column = new LongColumn("test", 6);
        column.set(0, 10L);
        column.set(1, 20L);
        column.set(2, 30L);
        column.set(3, null);
        column.set(4, 40L);
        column.set(5, 50L);

        assertEquals(10L, (long) column.get(0));
        assertEquals(20L, (long) column.get(1));
        assertEquals(30L, (long) column.get(2));
        assertEquals(null, column.get(3));
        assertEquals(40L, (long) column.get(4));
        assertEquals(50L, (long) column.get(5));
        assertEquals(6, column.size());
    }

    @Test
    public void testSize() {
        LongColumn column = new LongColumn("test", 5);
        assertEquals(5, column.size());
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            LongColumn column = new LongColumn("test", size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                column.set(i, ru.randLongOrNull());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var exp = ru.randLongOrNull();
                assertEquals(column.get(i), exp, "Mismatch at iteration " + iter + ", index " + i + ": " + column.get(i) + " != " + exp);
            }
        }
    }

}