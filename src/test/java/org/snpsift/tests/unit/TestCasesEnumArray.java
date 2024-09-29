package org.snpsift.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.snpsift.annotate.mem.arrays.EnumArray;
import org.snpsift.util.RandomUtil;

/**
 * Unit test cases for StringArray
 */
public class TestCasesEnumArray {


    @Test
    public void test_01() {
        // Create a StringArray from a collection of strings
        String[] strings = {"one", "two", "three", "four", "five"};
        // Create array and add all strings
        EnumArray ea = new EnumArray(strings.length);
        for (String str : strings)
            ea.add(str);
        // Check size
        assertEquals(strings.length, ea.size(), "Size mismatch");
        assertEquals(5 + 1, ea.numEnums(), "Number of enums mismatch");
        // Check that all strings match
        for(int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], ea.get(i), "String mismatch at index " + i + ": '" + strings[i] + "'' != '" + ea.get(i) + "'");
        }
    }

    @Test
    public void test_02() {
        // Create a StringArray from a collection of strings
        String[] strings = {"one", "two", "three", "four", "five", "three", "four", "five", "three", "four", "five", "three", "four", "five", "three", "four", "five"};
        // Create array and add all strings
        EnumArray ea = new EnumArray(strings.length);
        for (String str : strings)
            ea.add(str);
        // Check size
        assertEquals(strings.length, ea.size(), "Size mismatch");
        assertEquals(5 + 1, ea.numEnums(), "Number of enums mismatch");
        // Check that all strings match
        for(int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], ea.get(i), "String mismatch at index " + i + ": '" + strings[i] + "'' != '" + ea.get(i) + "'");
        }
    }

    @Test
    public void test_03() {
        // Create a StringArray from a collection of strings
        String[] strings = new String[255];
        for(int i = 0; i < strings.length; i++)
            strings[i] = "str" + i;
        // Create array and add all strings
        EnumArray ea = new EnumArray(strings.length);
        for (String str : strings)
            ea.add(str);
        // Check size
        assertEquals(strings.length, ea.size(), "Size mismatch");
        // Check that all strings match
        for(int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], ea.get(i), "String mismatch at index " + i + ": '" + strings[i] + "'' != '" + ea.get(i) + "'");
        }
    }

    @Test
    public void testRand() {
        int enumStringLen = 10;
        for(int iter = 0 ; iter < 100; iter++) {
            RandomUtil ru = new RandomUtil(iter);
            // Create enums
            ArrayList<String> enumStrings = new ArrayList<>();
            for(int i = 0; i < 100; i++) {
                enumStrings.add(ru.randString(enumStringLen));
            }

            // Create array and add all strings
            var numStrings = ru.randInt(100000);
            // Create a StringArray with random strings
            EnumArray earray = new EnumArray(numStrings);
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                String enumStr = ru.randEnum(enumStrings);
                earray.add(enumStr);
            }

            // Check that all strings match
            ru.reset();
            for(int i = 0; i < numStrings; i++) {
                String enumStr = ru.randEnum(enumStrings);
                assertEquals(enumStr, earray.get(i), "Mismatch at iteration " + iter + ", index " + i + ": " + earray.get(i) + " != " + enumStr);
            }
        }
    }
    
}
