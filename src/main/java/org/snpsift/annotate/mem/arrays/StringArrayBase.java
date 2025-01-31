package org.snpsift.annotate.mem.arrays;

import java.io.Serializable;
import java.util.Iterator;


/**
 * Abstract base class for a string array that supports iteration and serialization.
 * This class provides a framework for managing a collection of strings with methods to add, get, and set elements.
 * 
 * The class maintains an internal index to keep track of the current position in the array.
 * It also provides an iterator to traverse the elements in the array.
 * 
 * Limitations:
 *   - The class is abstract and requires concrete implementations to define the storage mechanism for the strings.
 *   - The 'add' and 'set' methods are not intended to be used together; typically, you should use one or the other.
 *   - The size of the array is managed by the implementing class, and the memory size calculation is approximate.
 */
public abstract class StringArrayBase implements Iterable<String>, Serializable {

    private static final long serialVersionUID = 20240731059L;

    protected int currentIndex; // Index of latest element added

    class StringArrayIterator implements Iterator<String> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i < currentIndex;
        }

        @Override
        public String next() {
            return get(i++);
        }
    }

    /**
     * Constructor
     * @param numElements : Number of elements in the array
     */
    public StringArrayBase() {
        currentIndex = 0;
    }

    /**
     * Add a string to the array
     * WARNING: Typically you use either 'add' or 'set', but not both
     */
    public void add(String str) {
        currentIndex = set(currentIndex, str);
    }

    /**
     * Get the string at array index 'i'
     */
    public abstract String get(int i);

    @Override
    public Iterator<String> iterator() {
        return new StringArrayIterator();
    }

    /**
     * Number of ellements in the array
     */
    public int length() {
        return currentIndex;
    }

    /**
     * Add a string to the array
     * Return the index of the next element
     */
    public abstract int set(int i, String str);

    /**
     * Number of different strings in the array
     */
    public abstract int size();

    /**
	 * Memory size of this object (approximate size in bytes)
	 */
    public abstract long sizeBytes();

}