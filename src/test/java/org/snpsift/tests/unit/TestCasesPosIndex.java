package org.snpsift.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.PosIndex;

public class TestCasesPosIndex {

    @Test
    public void testContains() {
        PosIndex posIndex = new PosIndex(5);
        posIndex.set(0, 10);
        posIndex.set(1, 20);
        posIndex.set(2, 30);
        posIndex.set(3, 40);
        posIndex.set(4, 50);

        assertTrue(posIndex.contains(20));
        assertFalse(posIndex.contains(15));
    }

    @Test
    public void testGet() {
        PosIndex posIndex = new PosIndex(3);
        posIndex.set(0, 100);
        posIndex.set(1, 200);
        posIndex.set(2, 300);

        assertEquals(100, posIndex.get(0));
        assertEquals(200, posIndex.get(1));
        assertEquals(300, posIndex.get(2));
    }

    @Test
    public void testIndexOf() {
        PosIndex posIndex = new PosIndex(4);
        posIndex.set(0, 10);
        posIndex.set(1, 20);
        posIndex.set(2, 30);
        posIndex.set(3, 40);

        assertEquals(1, posIndex.indexOf(20));
        assertTrue(posIndex.indexOf(15) < 0);
    }

    @Test
    public void testIndexOfSlow() {
        PosIndex posIndex = new PosIndex(3);
        posIndex.set(0, 100);
        posIndex.set(1, 200);
        posIndex.set(2, 300);

        assertEquals(1, posIndex.indexOfSlow(200));
        assertTrue(posIndex.indexOfSlow(150) < 0);
    }

    @Test
    public void testCapacity() {
        PosIndex posIndex = new PosIndex(5);
        assertEquals(5, posIndex.capacity());
    }

    @Test
    public void testSize() {
        PosIndex posIndex = new PosIndex(5);
        posIndex.set(0, 10);
        posIndex.set(1, 20);
        posIndex.set(2, 30);
        assertEquals(3, posIndex.size());
    }

}