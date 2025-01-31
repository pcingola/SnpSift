package org.snpsift.annotate.mem.arrays;

import java.io.Serializable;
import java.util.Arrays;

/**
 * BoolArray is a class that provides a compact representation of a boolean array using a byte array.
 * Each bit in the byte array represents a boolean value, allowing for efficient storage and manipulation
 * of boolean values.
 * 
 * The class supports operations to set, clear, and check boolean values at specific indices, as well as
 * methods to fill the array with true values, reset the array to false values, and get the size of the array
 * in both bits and bytes.
 * 
 * The boolean values are stored in a byte array, where each bit in a byte represents a boolean value.
 * The index of the boolean value is used to determine the corresponding byte and bit position within that byte.
 * 
 * Methods:
 * - BoolArray(int size): Constructor to initialize the byte array with the specified size in bits.
 * - void clear(int i): Clears the boolean value at the specified index (sets it to false).
 * - void fill(): Sets all boolean values in the array to true.
 * - boolean is(int i): Checks if the boolean value at the specified index is true.
 * - void set(int i): Sets the boolean value at the specified index to true.
 * - void set(int i, boolean value): Sets the boolean value at the specified index to the specified value.
 * - void reset(): Resets all boolean values in the array to false.
 * - int size(): Returns the size of the array in bits.
 * - int sizeBytes(): Returns the size of the array in bytes.
 */
public class BoolArray implements Serializable {

	private static final long serialVersionUID = 2024073104L;

	protected byte[] bytes;

	public BoolArray(int size) {
		bytes = new byte[(size + 7) / 8];
		reset();
	}

	/**
	 * Clear null data
	 */
	public void clear(int i) {
		bytes[i / 8] &= ~(1 << (i % 8));
	}

	public void fill() {
		Arrays.fill(bytes, (byte) 0xFF);
	}

	/**
	 * Is the data value is index i set?
	 */
	public boolean is(int i) {
		return (bytes[i / 8] & (1 << (i % 8))) != 0;
	}

	/**
	 * Set data to null
	 */
	public void set(int i) {
		bytes[i / 8] |= 1 << (i % 8);
	}

	public void set(int i, boolean value) {
		if( value ) set(i);
		else clear(i);
	}

	public void reset() {
		Arrays.fill(bytes, (byte) 0x00);
	}

	public int size() {
		return bytes.length * 8;
	}

	public int sizeBytes() {
		return bytes.length;
	}

}
