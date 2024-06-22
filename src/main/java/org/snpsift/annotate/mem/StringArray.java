package org.snpsift.annotate.mem;

import java.util.Arrays;

/**
 * Implement a memory efficient array of strings
 */
public class StringArray {
    protected byte[] data;
    protected int[] index2offset; // Convert array index to data offset
    protected int size;
    protected int currentIndex;
    protected int offset;

    /**
     * Calculate the size of a string in memory
     */
    public static int sizeOf(String s) {
        var len = (s == null ? 0 : s.getBytes().length);
        return len + 1; // +1 for the '\0' character
    }

    /**
     * Constructor
     * @param numElements : Number of elements in the array
     * @param size : Initial size of the array
     */
    public StringArray(int numElements, int size) {
        this.size = size;
        offset = 0;
        currentIndex = 0;
        // Initialize arrays
        data = new byte[size];
        Arrays.fill(data, (byte) 0);
        index2offset = new int[numElements];
        Arrays.fill(index2offset, -1);
    }

    /**
     * Add a string to the array
     * WARNING: Typically you use either 'add' or 'set', but not both
     */
    public void add(String str) {
        set(currentIndex++, str);
    }

    /**
     * Get the string at array index 'i'
     */
    public String get(int i) {
        var offsst = index2offset[i];
        if (offsst == -1) return null;
        return get_by_offset(index2offset[i]);
    }

    /**
     * Get a string
     */
    String get_by_offset(int offset) {
        // Find the end of the string
        int end = offset;
        while (end < data.length && data[end] != 0)
            end++;
        // Return the string
        return new String(data, offset, end - offset);
    }

    /**
     * Add a string to the array
     * WARNING: Typically you use either 'add' or 'set', but not both
     */
    public void set(int i, String str) {
        // Store the offset
        if( index2offset[i] != -1) throw new RuntimeException("Index already set: " + i);
        index2offset[i] = offset;
        // Null string is converted to an empty string
        if (str == null) str = "";
        // Copy non-empty strings
        if (str.length() > 0) {
            // Copy bytes from the string to the data array
            byte[] strBytes = str.getBytes();
            if (offset + strBytes.length > data.length) throw new RuntimeException("Out of memory");
            System.arraycopy(strBytes, 0, data, offset, strBytes.length);
            offset += strBytes.length;
        }
        // Null terminated string
        data[offset++] = 0; 
    }

    /**
     * Number of strings
     */
    public int size() {
        return size;
    }
}