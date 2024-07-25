package org.snpsift.annotate.mem;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A "memory efficient" boolean array
 * Is is implemented as an array of bytes, where each bit is a boolean value.
 */
public class BoolArray implements Serializable {

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
