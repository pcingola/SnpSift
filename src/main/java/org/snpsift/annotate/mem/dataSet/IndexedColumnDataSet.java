package org.snpsift.annotate.mem.dataSet;

import java.util.HashMap;
import java.util.Map;

import org.snpsift.annotate.mem.PosIndex;
import org.snpsift.annotate.mem.dataColumn.DataColumn;
import org.snpsift.annotate.mem.dataColumn.StringColumn;

/**
 * A set of DataColumns, indexed by position
 * This is used to store data for a chromosome
 */
public abstract class IndexedColumnDataSet {
	int currentIdx = 0;	// Current index
	PosIndex posIndex;	// Index by position
	StringColumn ref;	// Reference allele
	StringColumn alt;	// Alternative allele
	Map<String, DataColumn<?>> columns;	// Data columns

	public IndexedColumnDataSet(int numEntries, String[] fields) {
		posIndex = new PosIndex(numEntries);
		columns = new HashMap<>();
	}

	/**
	 * Add a column
	 */
	public void addColumn(String name, DataColumn<?> column) {
		columns.put(name, column);
	}

	/**
	 * Get a column
	 */
	public DataColumn<?> getColumn(String name) {
		return columns.get(name);
	}

	/**
	 * Get data from a column
	 */
	public abstract Object getData(String columnName, int pos, String ref, String alt);

	public abstract void setData(String columnName, Object value, int pos, String ref, String alt);
}

