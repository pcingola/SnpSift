package org.snpsift.annotate.mem.dataColumn;

import java.io.Serializable;

/**
 * A wrapper for a data column of primitive type T
 */
public abstract class DataColumn<T> implements Serializable {

	public static final int MAX_NUMBER_OF_ELEMENTS_TO_SHOW = 10;

	protected String name;
	protected byte[] isNUllData; // If the data is null, we set the corresponding bit in this array to 1

	public DataColumn(String name, int size) {
		this.name = name;
		isNUllData = new byte[(size + 7) / 8];
		// Initialize all data to not null
		for (int i = 0; i < isNUllData.length; i++)
			isNUllData[i] = 0;
	}

	/**
	 * Clear null data
	 */
	public void clearNull(int i) {
		isNUllData[i / 8] &= ~(1 << (i % 8));
	}

	public String getName() {
		return name;
	}

	/**
	 * Is the data at index i null?
	 */
	public boolean isNull(int i) {
		return (isNUllData[i / 8] & (1 << (i % 8))) != 0;
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
		System.err.println("SET " + i + ": " + value);
		if (value == null) setNull(i);
		else {
			clearNull(i);
			setData(i, value);
		}
	}

	/**
	 * Set data at index i
	 */
	protected abstract void setData(int i, Object value);

	/**
	 * Set data to null
	 */
	public void setNull(int i) {
		System.err.println("SET NULL: " + i);
		isNUllData[i / 8] |= 1 << (i % 8);
	}

	/** Number of elements in this DataColumn */
	public abstract int size();

	public String toString() {
		var sb = new StringBuilder();
		sb.append( this.getClass().getName() + ": '" + name + "', size: " + size() + "\n");
		for (int i = 0; i < size() && i < MAX_NUMBER_OF_ELEMENTS_TO_SHOW; i++) {
			sb.append("\t" + i + ": " + (isNull(i) ? "null" : get(i).toString()) + "\n");
		}
		return sb.toString();
	}
}
