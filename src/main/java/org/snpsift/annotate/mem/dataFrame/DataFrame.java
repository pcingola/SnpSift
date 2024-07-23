package org.snpsift.annotate.mem.dataFrame;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.PosIndex;
import org.snpsift.annotate.mem.dataColumn.BoolColumn;
import org.snpsift.annotate.mem.dataColumn.CharColumn;
import org.snpsift.annotate.mem.dataColumn.IntColumn;
import org.snpsift.annotate.mem.dataColumn.FloatColumn;
import org.snpsift.annotate.mem.dataColumn.DataColumn;
import org.snpsift.annotate.mem.dataColumn.StringColumn;

/**
 * A set of DataColumns, indexed by position
 * This is used to store data for a chromosome
 */
public abstract class DataFrame implements java.io.Serializable {
	int size;	// Number of entries
	int currentIdx = 0;	// Current index
	PosIndex posIndex;	// Index by position (i.e. chromosome position is transformed into a "column / array index")
	StringColumn ref;	// Reference allele
	StringColumn alt;	// Alternative allele
	Map<String, DataColumn<?>> columns;	// Data columns
	String[] fields;	// Fields to annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate

	public DataFrame(int size, Map<String, VcfInfoType> fields2type) {
		this.size = size;
		posIndex = new PosIndex(size);
		columns = new HashMap<>();
		this.fields2type = fields2type;
		this.fields = fields2type.keySet().toArray(new String[0]);
		createColumns();
	}

	/**
	 * Add a column
	 */
	public void addColumn(String name, DataColumn<?> column) {
		columns.put(name, column);
	}

	/**
	 * Create a column of a given type
	 */
	DataColumn<?> createColumn(String field, VcfInfoType type) {
		switch (type) {
			case Flag:
				return new BoolColumn(field, size);
			case Integer:
				return new IntColumn(field, size);
			case Float:
				return new FloatColumn(field, size);
			case Character:
				return new CharColumn(field, size);
			case String:
				return new StringColumn(field, size);
			default:
				throw new RuntimeException("Unimplemented type: " + type);
		}
	}

	/**
	 * Create columns based on fields
	*/
	public void createColumns() {
		for(var field: fields2type.keySet()) {
			var column = createColumn(field, fields2type.get(field));
			columns.put(field, column);
		}
	}

	/**
	 * Get a column
	 */
	public DataColumn<?> getColumn(String name) {
		return columns.get(name);
	}

	/**
	 * Get data from a column
	 * Note: The value can be null
	 */
	public Object getData(String columnName, int pos, String ref, String alt) {
		// Find column
		var col = columns.get(columnName);
		if(col == null) throw new RuntimeException("Cannot find column: " + columnName);
		// Find index in the column
		// TODO: Find lower and upper bounds to index, scan multiple entries
		var idx = posIndex.indexOf(pos);
		if(idx < 0) return null; // Not found
		if(col.isNull(idx)) return null; // Found, but entry has a null value
		return col.get(idx);
	}

	/**
	 * Resize and memory optimize the data
	 */
	public void resize() {
		for(var col: columns.values())
			col.resize();
	}

	/**
	 * Set data in a column.
	 * Note: The value can be null
	 */
	public abstract void setData(String columnName, Object value, int pos, String ref, String alt);
}

