package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.dataColumn.IntColumn;
import org.snpsift.util.RandomUtil;


public class TestCasesIntColumn {

    @Test
    public void testGet01() {
        IntColumn column = new IntColumn("test", 6);
        column.set(0, 10);
        column.set(1, 20);
        column.set(2, 30);
        column.set(3, null);
        column.set(4, 40);
        column.set(5, 50);

        assertEquals(10, column.get(0));
        assertEquals(20, column.get(1));
        assertEquals(30, column.get(2));
        assertEquals(null, column.get(3));
        assertEquals(40, column.get(4));
        assertEquals(50, column.get(5));
        assertEquals(6, column.size());
    }

    @Test
    public void testSize() {
        IntColumn column = new IntColumn("test", 5);
        assertEquals(5, column.size());
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            IntColumn column = new IntColumn("test", size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                column.set(i, ru.randIntOrNull());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var exp = ru.randIntOrNull();
                assertEquals(exp, column.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + column.get(i) + " != " + exp);
            }
        }
    }

}