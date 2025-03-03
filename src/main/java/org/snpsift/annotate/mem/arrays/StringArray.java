
package org.snpsift.annotate.mem.arrays;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * The StringArray class implements a memory-efficient array of strings.
 * It stores strings as bytes in a byte array, using UTF-8 encoding, and uses a single byte to mark the end of a string ('\0').
 * 
 * This class provides methods to calculate the size of strings in memory, create StringArray instances from arrays or collections of strings, 
 * and retrieve or set strings at specific indices. The internal data structure uses a byte array to store the string data and an integer array 
 * to map array indices to offsets in the byte array.
 * 
 * Limitations:
 * 
 * - The class does not support dynamic resizing of the internal byte array, so the initial size must be sufficient to store all strings.
 * - Once a string is set at a specific index, it cannot be changed or removed.
 * - The class is not thread-safe and should be used with caution in concurrent environments.
 * - Adding strings beyond the allocated memory will result in a runtime exception.
 * 
 */
public class StringArray extends StringArrayBase {

    private static final long serialVersionUID = 2024073106L;

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
        super();
        offset = 0;
        // Initialize arrays
        data = new byte[size];
        Arrays.fill(data, (byte) 0);
        index2offset = new int[numElements];
        Arrays.fill(index2offset, -1);
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
     * Add a string to the array
     * WARNING: Typically you use either 'add' or 'set', but not both
     */
    public int set(int i, String str) {
        // Store the offset
        if( index2offset[i] != -1) throw new RuntimeException("Index already set: " + i);
        index2offset[i] = offset;
        currentIndex = i + 1;
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
        return currentIndex;
    }

    /**
     * Size of the data array (capacity of number of bytes)
     */
    public int size() {
        return index2offset.length;
    }

    /**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return data.length + index2offset.length * 4;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StringArray(length: " + currentIndex + " / " + index2offset.length + ", sizeBytes: " + offset + " / " + data.length + ")");
        if (currentIndex > 0 ) sb.append(":\n");
        for (int i = 0; i < currentIndex && i < MAX_NUM_STRING_TO_SHOW; i++) {
            sb.append("\t" + i + ": '" + get(i) + "'\n");
        }
        return sb.toString();
    }


}