package org.snpsift.annotate.mem.dataFrame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.vcf.VcfHeaderInfo;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.arrays.PosIndex;
import org.snpsift.annotate.mem.arrays.StringArray;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnBool;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnChar;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumn;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnDouble;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnInt;
import org.snpsift.annotate.mem.dataFrame.dataFrameColumn.DataFrameColumnString;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.FormatUtil;

/**
 * A set of DataColumns, indexed by position
 * This is used to store data for a chromosome
 */
public class DataFrame implements Serializable {

	private static final long serialVersionUID = 2024073101L;

	public static final int MAX_ROWS_TO_SHOW = 100;

	VariantTypeCounter variantTypeCounter;
	VariantCategory variantCategory;
	int currentIdx = 0;	// Current index
	PosIndex posIndex;	// Index by position (i.e. chromosome position is transformed into a "column / array index")
	StringArray refs;	// Reference allele.
	StringArray alts;	// Alternative allele.
	Map<String, DataFrameColumn<?>> columns;	// Data columns
	Fields fields; // Fields to create or annotate

	public DataFrame(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory, boolean hasRefs, boolean hasAlts) {
		this.variantTypeCounter = variantTypeCounter;
		this.variantCategory = variantCategory;
		int size = variantTypeCounter.getCount(variantCategory);
		posIndex = new PosIndex(size);
		columns = new HashMap<>();
		this.fields = variantTypeCounter.getFields();
		createColumns();
		if(hasRefs) refs = new StringArray(size, stringArrayMemSize(variantCategory, VariantTypeCounter.REF));
		if(hasAlts) alts = new StringArray(size, stringArrayMemSize(variantCategory, VariantTypeCounter.ALT));
	}

	/**
	 * Add a column
	 */
	void add(String name, DataFrameColumn<?> column) {
		columns.put(name, column);
	}

	/**
	 * Add a row to the data frame
	 */
	public void add(DataFrameRow row) {
		if(row.getIdx() >= 0) throw new RuntimeException("Row already added");
		// Set possition, index, reference, and alternative alleles
		posIndex.set(currentIdx, row.getPos());
		row.setIdx(currentIdx);
		if(refs != null) refs.set(currentIdx, row.getRef());
		if(alts != null) alts.set(currentIdx, row.getAlt());
		// Set all fields
		row.setDataFrame();
		// Prepare for next entry
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
	protected DataFrameColumn<?> createColumn(VcfHeaderInfo vcfHeaderInfo) {
		int numEntries = variantTypeCounter.getCount(variantCategory);
		var fieldName = vcfHeaderInfo.getId();
		switch (vcfHeaderInfo.getVcfInfoType()) {
			case Flag:
				return new DataFrameColumnBool(fieldName, numEntries);
			case Integer:
				return new DataFrameColumnInt(fieldName, numEntries);
			case Float:
				return new DataFrameColumnDouble(fieldName, numEntries);
			case Character:
				return new DataFrameColumnChar(fieldName, numEntries);
			case String:
				int memSize = stringArrayMemSize(variantCategory, fieldName);
				return new DataFrameColumnString(fieldName, numEntries, memSize);
			default:
				throw new RuntimeException("Unimplemented type: " + vcfHeaderInfo.getVcfInfoType());
		}
	}

	/**
	 * Create columns based on fields
	*/
	protected void createColumns() {
		for(var field: fields) {
			var column = createColumn(field);
			add(field.getId(), column);
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
	public DataFrameColumn<?> getColumn(String name) {
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

	/**
	 * Memory size of this object
	 */
	public long sizeBytes() {
		long size = posIndex.sizeBytes();
		for(var col: columns.values())
			size += col.sizeBytes();
		if(refs != null) size += refs.sizeBytes();
		if(alts != null) size += alts.sizeBytes();
		return size;
	}

	int stringArrayMemSize(VariantCategory variantCategory, String field) {
		var size = variantTypeCounter.getSize(variantCategory, field);
		var num = variantTypeCounter.getCount(variantCategory);
		return size + num;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataFrame: " + variantCategory);
		sb.append(", size: " + posIndex.size());
		sb.append(", current index: " + currentIdx);
		sb.append(", memory: " + FormatUtil.formatBytes(sizeBytes()) + "\n");
		sb.append("\tField types:\n");
		for(var field: fields)
			sb.append("\t\t" + field + "\n");
		
		// Show columns as a table
		int rowToShow = Math.min(posIndex.size(), MAX_ROWS_TO_SHOW);
		for(int i=0 ; i < rowToShow; i++) {
			sb.append("\t" + i + "\t" + posIndex.get(i) 
						+ (refs != null ? "\t| " + refs.get(i) : "")
						+ (alts != null ? "\t| " + alts.get(i) : "")
						+ "\t| ");
			for(var col: columns.values())
				sb.append(col.get(i) + "\t| ");
			sb.append("\n");
		}
		return sb.toString();
	}
}

