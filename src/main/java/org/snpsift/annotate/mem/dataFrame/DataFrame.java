package org.snpsift.annotate.mem.dataFrame;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.arrays.PosIndex;
import org.snpsift.annotate.mem.arrays.StringArray;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnBool;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnChar;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataColumn;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnDouble;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnInt;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnString;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A set of DataColumns, indexed by position
 * This is used to store data for a chromosome
 */
public class DataFrame implements java.io.Serializable {
	VariantTypeCounter variantTypeCounter;
	VariantCategory variantCategory;
	int currentIdx = 0;	// Current index
	PosIndex posIndex;	// Index by position (i.e. chromosome position is transformed into a "column / array index")
	StringArray refs;	// Reference allele.
	StringArray alts;	// Alternative allele.
	Map<String, DataColumn<?>> columns;	// Data columns
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate

	public DataFrame(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
		this.variantTypeCounter = variantTypeCounter;
		this.variantCategory = variantCategory;
		int size = variantTypeCounter.getCount(variantCategory);
		posIndex = new PosIndex(size);
		columns = new HashMap<>();
		this.fields2type = variantTypeCounter.getFields2type();
		createColumns();
	}

	/**
	 * Add a column
	 */
	void addColumn(String name, DataColumn<?> column) {
		columns.put(name, column);
	}

	/**
	 * Add a row to the data frame
	 */
	public void addRow(DataFrameRow row) {
		if(row.getIdx() >= 0) throw new RuntimeException("Row already added");
		posIndex.set(currentIdx, row.getPos());
		row.setIdx(currentIdx);
		row.setDataFrame();
		currentIdx++;
	}

	public void check() {
		posIndex.check();
		for(var col: columns.values())
			col.check();
	}
	
	public Iterable<String> columnNames() {
		return columns.keySet();
	}

	/**
	 * Create a column of a given type
	 */
	protected DataColumn<?> createColumn(String field, VcfInfoType type) {
		int numEntries = variantTypeCounter.getCount(variantCategory);
		switch (type) {
			case Flag:
				return new DataFrameColumnBool(field, numEntries);
			case Integer:
				return new DataFrameColumnInt(field, numEntries);
			case Float:
				return new DataFrameColumnDouble(field, numEntries);
			case Character:
				return new DataFrameColumnChar(field, numEntries);
			case String:
				int memSize = variantTypeCounter.getSize(variantCategory, field);
				return new DataFrameColumnString(field, numEntries, memSize);
			default:
				throw new RuntimeException("Unimplemented type: " + type);
		}
	}

	/**
	 * Create columns based on fields
	*/
	protected void createColumns() {
		for(var field: fields2type.keySet()) {
			var column = createColumn(field, fields2type.get(field));
			addColumn(field, column);
		}
	}

	/**
	 * Does the entry at possition 'idx' match the given (pos, ref, alt) values?
	 */
	protected boolean eq(int idx, int pos, String ref, String alt) {
		if( posIndex.get(idx) != pos) return false;
		if( (ref != null) && (this.refs != null) && !this.refs.get(idx).equals(ref)) return false;
		if( (alt != null) && (this.alts != null) && !this.alts.get(idx).equals(alt)) return false;
		return true;
	}

	/**
	 * Get data from a column by searching by position, reference and alternative alleles.
	 * Note: The value can be null
	 */
	protected Object get(String columnName, int idx) {
		return columns.get(columnName).get(idx);
	}

	/**
	 * Get a column
	 */
	public DataColumn<?> getColumn(String name) {
		return columns.get(name);
	}

	/**
	 * Get a 'row' from the data frame.
	 * @param pos : Position
	 * @param ref : Reference allele
	 * @param alt : Alternative allele
	 * @return A data frame row if found, or null if not found
	 */
	public DataFrameRow getRow(int pos, String ref, String alt) {
		var idx = find(pos, ref, alt);
		if(idx < 0) return null; // Not found
		return new DataFrameRow(this, pos, ref, alt, idx);
	}

	/**
	 * Get data from a column index by searching by position, reference and alternative alleles.
	 * @return The index of the row in the data frame, or -1 if not found
	 */
	protected int find(int pos, String ref, String alt) {
		var idx = posIndex.indexOf(pos);
		if(idx < 0) return -1; // Not found

		// Check current index
		if( eq(idx, pos, ref, alt)) return idx;

		// Check previous indexes
		for(int i = idx - 1; i >= 0; i--) {
			if( posIndex.get(i) != pos) break;
			if( eq(i, pos, ref, alt)) return i;
		}

		// Check next indexes
		for(int i = idx + 1; i < posIndex.size(); i++) {
			if( posIndex.get(i) != pos) return -1;
			if( eq(i, pos, ref, alt)) return i;
		}

		return -1;
	}

	/**
	 * Get data from a column by searching by position, reference and alternative alleles.
	 * Note: The value can be null
	 */
	public boolean hasEntry(int pos, String ref, String alt) {
		return find(pos, ref, alt) >= 0;
	}

	/**
	 * Resize and memory optimize the data
	 */
	public void resize() {
		for(var col: columns.values())
			col.resize();
	}

	/**
	 * Set data in a column
	 */
	protected void set(String columnName, int idx, Object value) {
		columns.get(columnName).set(idx, value);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataFrame: " + variantCategory);
		sb.append(", size: " + posIndex.size());
		sb.append(", current index: " + currentIdx + "\n");
		sb.append("\tField types:\n");
		for(var field: fields2type.keySet())
			sb.append("\t\t" + field + " : " + fields2type.get(field) + "\n");
		
		// Show columns as a table
		int size = posIndex.size();
		for(int i=0 ; i < size; i++) {
			sb.append("\t" + i + "\t" + posIndex.get(i) 
						+ (refs != null ? "\t" + refs.get(i) : "")
						+ (alts != null ? "\t" + alts.get(i) : "")
						+ " | ");
			for(var col: columns.values())
				sb.append(col.get(i) + "\t| ");
			sb.append("\n");
		}
		return sb.toString();
	}
}

