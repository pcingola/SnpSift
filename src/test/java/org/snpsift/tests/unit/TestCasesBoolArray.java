package org.snpsift.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.BoolArray;
import org.snpsift.util.RandomUtil;

public class TestCasesBoolArray {

    @Test
    public void testClear() {
        BoolArray array = new BoolArray(10);
        array.clear(5);
        for(int i = 0; i < 10; i++) {
            assertFalse(array.is(i));
        }
        assertFalse(array.is(5));
    }

    @Test
    public void testFill() {
        BoolArray array = new BoolArray(10);
        array.fill();
        for(int i = 0; i < 10; i++) {
            assertTrue(array.is(i));
        }
    }

    @Test
    public void testIs() {
        BoolArray array = new BoolArray(10);
        array.set(3);
        assertTrue(array.is(3));
        assertFalse(array.is(7));
    }

    @Test
    public void testSet() {
        BoolArray array = new BoolArray(10);
        array.set(8);
        assertTrue(array.is(8));
    }

    @Test
    public void testSe2() {
        BoolArray array = new BoolArray(10);
        array.set(8);
        assertTrue(array.is(8));
        array.clear(8);
        assertFalse(array.is(8));
    }

    @Test
    public void testReset() {
        BoolArray array = new BoolArray(10);
        array.fill();
        array.reset();
        assertFalse(array.is(0));
        assertFalse(array.is(9));
    }

    @Test
    public void testRand() {
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            var size = ru.randInt(100000);
            BoolArray array = new BoolArray(size);

            ru.reset();
            for(int i = 0; i < size; i++) {
                array.set(i, ru.randBool());
            }

            ru.reset();
            for(int i = 0; i < size; i++) {
                var exp = ru.randBool();
                assertEquals(exp, array.is(i), "Mismatch at iteration " + iter + ", index " + i + ": " + array.is(i) + " != " + exp);
            }
        }
    }
}