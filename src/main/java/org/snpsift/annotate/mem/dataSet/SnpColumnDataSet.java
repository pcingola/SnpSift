package org.snpsift.annotate.mem.dataSet;


/**
 * A set of DataColumns specific SNP 'alt'  (e.g. Data for SNP "N -> A")
 * A set of DataColumns, indexed by position.
 * This is used to store data for a chromosome
 */
public class SnpColumnDataSet extends IndexedColumnDataSet {
	String alt;	// Alternative allele, i.e. one of 'A', 'C', 'G', 'T'

	public SnpColumnDataSet(int numEntries, String alt, String[] fields) {
		super(numEntries, fields);
		this.alt = alt;
	}

	public String getAlt() {
		return alt;
	}

	public void setData(String columnName, Object value, int pos, String ref, String alt) {
		if(!alt.equals(this.alt)) throw new RuntimeException("Cannot set data for SNP '" + this.alt + "' using alt '" + alt + "'");
		posIndex.set(currentIdx, pos);
		var column = columns.get(columnName);
		if(column == null) throw new RuntimeException("Cannot find column: " + columnName);
		column.set(currentIdx, value);
		currentIdx++;
	}
}
