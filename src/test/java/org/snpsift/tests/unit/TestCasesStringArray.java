package org.snpsift.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.StringArray;

/**
 * Unit test cases for StringArray
 */
public class TestCasesStringArray {
  
    @Test
    public void test_01() {
        // Create a StringArray from a collection of strings
        String[] strings = {"one", "two", "three", "four", "five"};
        StringArray sa = StringArray.of(strings);
        // Check that all strings match
        for(int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], sa.get(i), "String mismatch at index " + i + ": '" + strings[i] + "'' != '" + sa.get(i) + "'");
        }
    }

    @Test
    public void test_02() {
        // Create a StringArray from a collection of strings
        List<String> strings = List.of("one", "two", "three");
        StringArray sa = StringArray.of(strings);
        // Check that all strings match
        for(int i = 0; i < strings.size(); i++) {
            assertEquals(strings.get(i), sa.get(i), "String mismatch at index " + i + ": '" + strings.get(i) + "'' != '" + sa.get(i) + "'");
        }
    }

    @Test
    public void test_03() {
        // Create an empty StringArray
        StringArray sa = new StringArray(0,0);
        // Check that the size is 0
        assertEquals(0, sa.size(), "Size should be 0 for an empty StringArray");
    }

    @Test
    public void test_04() {
        // Create a StringArray with a single element
        StringArray sa = StringArray.of(List.of("hello"));
        // Check that the size is 1
        assertEquals(1, sa.length(), "Size should be 1 for a StringArray with a single element");
        // Check that the element matches
        assertEquals("hello", sa.get(0), "String mismatch at index 0: 'hello' != '" + sa.get(0) + "'");
    }

    @Test
    public void test_05() {
        // Create a StringArray with multiple elements
        StringArray sa = StringArray.of(List.of("apple", "banana", "cherry"));
        // Check that the size is 3
        assertEquals(3, sa.length(), "Size should be 3 for a StringArray with multiple elements");
        // Check that the elements match
        assertEquals("apple", sa.get(0), "String mismatch at index 0: 'apple' != '" + sa.get(0) + "'");
        assertEquals("banana", sa.get(1), "String mismatch at index 1: 'banana' != '" + sa.get(1) + "'");
        assertEquals("cherry", sa.get(2), "String mismatch at index 2: 'cherry' != '" + sa.get(2) + "'");
    }
}
