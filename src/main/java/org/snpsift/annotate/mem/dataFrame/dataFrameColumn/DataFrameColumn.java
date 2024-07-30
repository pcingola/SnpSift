package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

import java.io.Serializable;

import org.snpsift.annotate.mem.arrays.BoolArray;

/**
 * A wrapper for a data column of primitive type T
 * A Data Column is a column of a specific data tyle (String, Float, Long, etc.) that is stored using primitive types for memory efficiency.
 * The data that can be null, so we need to store a bit array to indicate which data is null.
 */
public abstract class DataFrameColumn<T> implements Serializable {

	public static final int MAX_NUMBER_OF_ELEMENTS_TO_SHOW = 10;

	protected String name;
	protected BoolArray isNUllData; // If the data is null, we set the corresponding bit in this array to 1

	public DataFrameColumn(String name, int size) {
		this.name = name;
		isNUllData = new BoolArray(size);
		isNUllData.fill();
	}

	/**
	 * Check data consistency
	 */
	public void check() {
		// Nothing to do
	}

	public String getName() {
		return name;
	}

	/**
	 * Is the data at index i null?
	 */
	public boolean isNull(int i) {
		return isNUllData.is(i);
	}

	/**
	 * Get value at index i, considering null data
	 */
	public T get(int i) {
		if (isNull(i)) return null;
		return getData(i);
	}

	protected abstract T getData(int i);

	/**
	 * Resize and memory optimize the data
	 */
	public void resize() {
		// Nothing to do
	}

	/**
	 * Set value at index i, consideting null data
	 */
	public void set(int i, Object value) {
		if (value == null) isNUllData.set(i);
		else {
			isNUllData.clear(i);
			setData(i, value);
		}
	}

	/**
	 * Set data at index i
	 */
	protected abstract void setData(int i, Object value);

	/** Number of elements in this DataColumn */
	public abstract int size();

	/**
	 * Memory size of this object (approximate size in bytes)
	 */
	public abstract long sizeBytes();

	public String toString() {
		var sb = new StringBuilder();
		sb.append( this.getClass().getName() + ": '" + name + "', size: " + size() + "\n");
		for (int i = 0; i < size() && i < MAX_NUMBER_OF_ELEMENTS_TO_SHOW; i++) {
			sb.append("\t" + i + ": " + (isNull(i) ? "null" : get(i).toString()) + "\n");
		}
		return sb.toString();
	}
}
