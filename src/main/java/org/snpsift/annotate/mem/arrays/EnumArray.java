
package org.snpsift.annotate.mem.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * The EnumArray class is a specialized array for storing and managing strings as enumerated values.
 * It uses a byte array to store the ordinal values of the strings, which are mapped to their corresponding
 * string values using two data structures: an ArrayList for ordinal-to-string mapping and a HashMap for
 * string-to-ordinal mapping.
 * 
 * The class provides methods to add strings to the array, retrieve strings by their index, and manage
 * the internal mappings between strings and their ordinal values. It also includes methods to get the
 * size of the array and the approximate memory size of the object.
 * 
 * Key features:
 * - Stores strings as enumerated values using a byte array.
 * - Supports up to 255 unique strings.
 * - Provides methods to add, retrieve, and manage strings in the array.
 * - Ensures consistency between the ordinal-to-string and string-to-ordinal mappings.
 * - Converts null strings to empty strings when adding to the array.
 * 
 * Usage:
 * - Use the constructor to initialize the array with a specified number of elements or an array of strings.
 * - Use the addEnum method to add a new string and get its ordinal value.
 * - Use the get method to retrieve a string by its index.
 * - Use the set method to add or update a string at a specific index.
 * - Use the size and sizeBytes methods to get the size of the array and the approximate memory size of the object.
 */
public class EnumArray extends StringArrayBase {
    private static final long serialVersionUID = 2024080900L;

    public static final int MAX_NUM_STRING_TO_SHOW = 10;
    public static final int MAX_NUMBER_OF_ENUM_VALUES = 255;
    
    protected byte[] data; // Raw data stored as enum ordinal
    protected ArrayList<String> ord2enum; // Enums by ordinal
    protected Map<String, Integer> enum2ord; // Ordinal by enum

    /**
     * Constructor for using an array of strings
     * @param data
     */
    public EnumArray(String[] data) {
        this(data.length);
        for (String str : data)
            add(str);
    }

    /**
     * Constructor
     * @param numElements : Number of elements in the array
     */
    public EnumArray(int numElements) {
        super();
        // Initialize arrays
        data = new byte[numElements];
        Arrays.fill(data, (byte) 0);
        // Initialize maps
        enum2ord = new HashMap<>();
        ord2enum = new ArrayList<>();
        // Add empty string
        addEnum("");
    }

    /**
     * Add an enum, return the ordinal
     */
    public int addEnum(String str) {
        // Already added?
        if( enum2ord.containsKey(str) ) return enum2ord.get(str);
        // Add a new string
        int ord = enum2ord.size();
        if( ord > MAX_NUMBER_OF_ENUM_VALUES ) throw new RuntimeException("EnumArray: Too many elements. Maximum number of elements in an Enum is " + MAX_NUMBER_OF_ENUM_VALUES);
        enum2ord.put(str, ord);
        ord2enum.add(str);
        if( enum2ord.size() != ord2enum.size() ) throw new RuntimeException("EnumArray: Inconsistent data structures. ord2enum.size()=" + ord2enum.size() + ", enum2ord.size()=" + enum2ord.size());
        return ord;
    }

    /**
     * Get the string at array index 'i'
     */
    public String get(int i) {
        int ord = getOrd(i);
        return ord2enum.get(ord);
    }

    /**
     * Get the ordinal at array index 'i'
     */
    public int getOrd(int i) {
        return data[i] & 0xFF;
    }

    /**
     * Number of enums in the array
     */
    public int numEnums() {
        return ord2enum.size();
    }

    /**
     * Add a string to the array
     * WARNING: Typically you use either 'add' or 'set', but not both
     */
    public int set(int i, String str) {
        if (str == null) str = ""; // Null string is converted to an empty string
        int ord = addEnum(str); // Get the ordinal
        data[i] = (byte) ord;   // Set data with the ordinal
        currentIndex = i + 1;   // Update the current index
        return currentIndex;
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
        return data.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EnumArray: " //
                    + "length: " + currentIndex + " / " + data.length //
                    + ", number of enums: " + numEnums() + "\n" //
                    );
        for (int i = 0; i < currentIndex && i < MAX_NUM_STRING_TO_SHOW; i++) {
            sb.append("\t" + i + ": '" + get(i) + "' (" + getOrd(i) + ")\n");
        }
        return sb.toString();
    }

}