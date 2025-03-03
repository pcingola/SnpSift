package org.snpsift.annotate.mem.dataFrame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The DataFrameRow class represents a row in a DataFrame. It implements the Serializable and Iterable<String> interfaces.
 * Each DataFrameRow object holds a reference to its parent DataFrame, its position, reference allele, alternative allele, 
 * and an index within the DataFrame. It also maintains a map of values for each column in the DataFrame.
 *
 * The class provides methods to:
 * - Retrieve all data for the row from the DataFrame.
 * - Get and set values for specific columns.
 * - Populate the DataFrame with values from the row.
 * - Iterate over the column names of the DataFrame.
 *
 * Non-obvious limitations:
 * - The values map is lazily initialized when getDataFrameValues() is called for the first time.
 * - The get() method assumes that the values map has been populated, either by calling getDataFrameValues() or by setting values explicitly.
 * - The setDataFrame() methods update the DataFrame with values from the row, but do not automatically synchronize changes made directly to the DataFrame.
 */
public class DataFrameRow implements java.io.Serializable,Iterable<String> {

	private static final long serialVersionUID = 2024073108L;

	DataFrame dataFrame; // Parent data frame
	int pos; // Position
	String ref; // Reference allele
	String alt; // Alternative allele
	int idx; // Index in the data frame
	Map<String, Object> values; // Values for each column

	public DataFrameRow(DataFrame dataFrame, int pos, String ref, String alt) {
		this(dataFrame, pos, ref, alt, -1);
	}

	public DataFrameRow(DataFrame dataFrame, int pos, String ref, String alt, int idx) {
		this.dataFrame = dataFrame;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.idx = idx;
	}

	/**
	 * Return all data for this row from the data frame
	 */
	public Map<String, Object> getDataFrameValues() {
		if( values == null) {
			values = new HashMap<>();
			for(var col: this) {
				values.put(col, getDataFrameValue(col));
			}
		}
		return values;
	}

	public Object get(String columnName) {
		return values.get(columnName);
	}

	/**
	 * Get data single column value from the data frame
	 */
	public Object getDataFrameValue(String columnName) {
		return dataFrame.get(columnName, idx);
	}

	public String getAlt() {
		return alt;
	}

	public int getIdx() {
		return idx;
	}

	public int getPos() {
		return pos;
	}

	public String getRef() {
		return ref;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	@Override
	public Iterator<String> iterator() {
		return dataFrame.columnNames().iterator();
	}

	public void set(String columnName, Object value) {
		if( values == null) values = new HashMap<>();
		values.put(columnName, value);
	}

	/**
	 * Set the data frame with a value from this row
	 */
	public void setDataFrame(String columnName) {
		dataFrame.set(columnName, idx, get(columnName));
	}

	/**
	 * Set the data frame with a specific value
	 */
	public void setDataFrame(String columnName, Object value) {
		dataFrame.set(columnName, idx, value);
	}


	/**
	 * Populate row in DataFrame with the values in this row
	 */
	public void setDataFrame() {
		for(var col: this) {
			setDataFrame(col, values.get(col));
		}
	}

	protected void setIdx(int idx) {
		this.idx = idx;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataFrameRow: pos=" + pos + ", ref=" + ref + ", alt=" + alt + ", idx=" + idx);
		if(values != null) {
			for(var col: this) {
				sb.append(", " + col + "='" + get(col) + "'");
			}
		}
		return sb.toString();
	}
}

