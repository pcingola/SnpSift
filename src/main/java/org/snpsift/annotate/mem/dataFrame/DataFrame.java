package org.snpsift.annotate.mem.dataFrame;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.util.Tuple;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.PosIndex;
import org.snpsift.annotate.mem.StringArray;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataColumn.BoolColumn;
import org.snpsift.annotate.mem.dataColumn.CharColumn;
import org.snpsift.annotate.mem.dataColumn.IntColumn;
import org.snpsift.annotate.mem.dataColumn.FloatColumn;
import org.snpsift.annotate.mem.dataColumn.DataColumn;
import org.snpsift.annotate.mem.dataColumn.StringColumn;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A set of DataColumns, indexed by position
 * This is used to store data for a chromosome
 */
public abstract class DataFrame implements java.io.Serializable {
	VariantTypeCounter variantTypeCounter;
	VariantCategory variantCategory;
	int currentIdx = 0;	// Current index
	PosIndex posIndex;	// Index by position (i.e. chromosome position is transformed into a "column / array index")
	StringArray ref;	// Reference allele.
	StringArray alt;	// Alternative allele.
	Map<String, DataColumn<?>> columns;	// Data columns
	String[] fields;	// Fields to annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate

	public DataFrame(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
		this.variantTypeCounter = variantTypeCounter;
		this.variantCategory = variantCategory;
		int size = variantTypeCounter.getCount(variantCategory);
		posIndex = new PosIndex(size);
		columns = new HashMap<>();
		this.fields2type = variantTypeCounter.getFields2type();
		this.fields = fields2type.keySet().toArray(new String[0]);
		createColumns();
	}

	/**
	 * Add a column
	 */
	public void addColumn(String name, DataColumn<?> column) {
		columns.put(name, column);
	}

	public void check() {
		posIndex.check();
	}
	
	/**
	 * Create a column of a given type
	 */
	DataColumn<?> createColumn(String field, VcfInfoType type) {
		int numEntries = variantTypeCounter.getCount(variantCategory);
		switch (type) {
			case Flag:
				return new BoolColumn(field, numEntries);
			case Integer:
				return new IntColumn(field, numEntries);
			case Float:
				return new FloatColumn(field, numEntries);
			case Character:
				return new CharColumn(field, numEntries);
			case String:
				int memSize = variantTypeCounter.getSize(variantCategory, field);
				return new StringColumn(field, numEntries, memSize);
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
	 * Does the entry at possition 'idx' match the given (pos, ref, alt) values?
	 */
	public boolean eq(int idx, int pos, String ref, String alt) {
		if( posIndex.get(idx) != pos) return false;
		if( (ref != null) && this.ref.get(idx) != ref) return false;
		if( (alt != null) && this.alt.get(idx) != alt) return false;
		return true;
	}

	/**
	 * Get a column
	 */
	public DataColumn<?> getColumn(String name) {
		return columns.get(name);
	}

	/**
	 * Get data from a column by searching by position, reference and alternative alleles.
	 * Note: The value can be null
	 */
	public boolean hasEntry(int pos, String ref, String alt) {
		throw new RuntimeException("Unimplemented method 'hasEntry'");
	}

	/**
	 * Get data from a column by searching by position, reference and alternative alleles.
	 * Note: The value can be null
	 */
	public Object getData(String columnName, int pos, String ref, String alt) {
		// Find column
		var col = columns.get(columnName);
		if(col == null) throw new RuntimeException("Cannot find column: " + columnName);
		// Find index in the column
		var idx = posIndex.indexOf(pos);
		if(idx < 0) return null; // Not found
		if(col.isNull(idx)) return null; // Found, but entry has a null value
		return col.get(idx);
	}

	/**
	 * Get a lower and upper bound index where the same '(pos, ref, alt)' values are stored.
	 * @return A tuple with the lower and upper bound index (inclusive). If the entry is not found, returns null.
	 * 
	 * For example if there is only one entry as position 123, the tuple will be (123, 123)
	 * If there are two entries at positions 123 and 124, the tuple will be (123, 124)
	 */
	public Tuple<Integer, Integer> getDataRange(String columnName, int pos, String ref, String alt) {
		/*
		 * WE NEED TO RE-IMPLEMENT THIS.
		 * CAN The same 'pos' may have different non-consecutive entries matching '(pos, ref, alt)'?
		 * COULD THIS HAPPEN IN CASES OF multiallelic VCF entries????
		 * ADD A SANITY CHECK?
		 */
		throw new RuntimeException("Unimplemented method 'hasEntry'");
		// var idx = posIndex.indexOf(pos);
		// if(idx < 0) return null; // Not found
		// if(!eq(idx, pos, ref, alt)) return null; // Not found
		// // Find lower bound
		// int lower = idx;
		// while(lower > 0 && eq(lower - 1, pos, ref, alt)) lower--;
		// // Find upper bound
		// int upper = idx;
		// while(upper < posIndex.size() - 1 && eq(upper + 1, pos, ref, alt)) upper++;
		// return new Tuple<>(lower, upper);
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

