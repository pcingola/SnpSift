package org.snpsift.annotate.mem.arrays;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * Implement a memory efficient array of strings
 * It only stores bytes (i.e. UTF-8 encoding) and uses a single byte to mark the end of a string ('\0')
 */
public class StringArray implements Serializable {
    /**
     * Calculate the size of a string in memory
     */
    public static int sizeOf(String s) {
        var len = (s == null ? 0 : s.getBytes().length);
        return len + 1; // +1 for the '\0' character
    }

    public static int sizeOf(String[] strings) {
        int len = 0;
        for (var s : strings)
            len += sizeOf(s);
        return len;
    }

    public static int sizeOf(Collection<String> strings) {
        int len = 0;
        for (var s : strings)
            len += sizeOf(s);
        return len;
    }

    public static final int MAX_NUM_STRING_TO_SHOW = 10;
    
    protected byte[] data; // Raw data as a byte array
    protected int[] index2offset; // Convert array index to data offset
    protected int currentIndex; // Index of latest element added
    protected int offset; // Current offset in the data array

    /**
     * Create a StringArray from an array of strings
     */
    public static StringArray of(String[] strings) {
        var sa = new StringArray(strings.length, sizeOf(strings));
        for (var s : strings)
            sa.add(s);
        return sa;
    }

    /**
     * Create a StringArray from a collection of strings
     */
    public static StringArray of(Collection<String> strings) {
        var sa = new StringArray(strings.size(), sizeOf(strings));
        for (String s : strings)
            sa.add(s);
        return sa;
    }

    /**
     * Constructor
     * @param numElements : Number of elements in the array
     * @param size : Initial size of the array
     */
    public StringArray(int numElements, int size) {
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
        var offset = index2offset[i];
        if (offset == -1) return null;
        return getByOffset(index2offset[i]);
    }

    /**
     * Get a string from a data offset
     * This method is used to get a string from the data array
     * Should be used only internally
     */
    private String getByOffset(int offset) {
        // Find the end of the string
        int end = offset;
        while (end < data.length && data[end] != 0)
            end++;
        // Return the string
        return new String(data, offset, end - offset);
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Number of ellements in the array
     */
    public int length() {
        return currentIndex;
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
            byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
            if (offset + strBytes.length > data.length) throw new RuntimeException("StringArray: Out of memory. The allocated memory is " + data.length + " bytes, but we need " + (offset + strBytes.length) + " bytes, to add new entry " + i + " with string '" + str + "'");
            System.arraycopy(strBytes, 0, data, offset, strBytes.length);
            offset += strBytes.length;
        }
        // Null terminated string
        data[offset++] = 0; 
    }

    /**
     * Size of the data array (capacity of number of bytes)
     */
    public int size() {
        return data.length;
    }

    /**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return data.length + index2offset.length * 4;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StringArray(" + index2offset.length + ", " + data.length + ")");
        if (currentIndex > 0 ) sb.append(":\n");
        for (int i = 0; i < currentIndex && i < MAX_NUM_STRING_TO_SHOW; i++) {
            sb.append("\t" + i + ": ' " + get(i) + "'\n");
        }
        return sb.toString();
    }

}