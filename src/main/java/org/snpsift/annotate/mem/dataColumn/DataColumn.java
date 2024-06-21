package org.snpsift.annotate.mem.dataColumn;

/**
 * A wrapper for a data column of primitive type T
 */
public interface DataColumn<T> {
	public String getName();	// Column name
	public T get(int i);	// Get value at index i
	public void set(int i, Object value);	// Set value at index i
}
