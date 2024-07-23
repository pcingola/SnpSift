package org.snpsift.annotate.mem.dataFrame;

import org.snpeff.util.Log;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A set of DataColumns specific SNP 'alt'  (e.g. Data for SNP "N -> A")
 * A set of DataColumns, indexed by position.
 * This is used to store data for a chromosome
 */
public class DataFrameSnp extends DataFrame {
	String alt;	// Alternative allele, i.e. one of 'A', 'C', 'G', 'T'

	public DataFrameSnp(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
		super(variantTypeCounter, variantCategory);
		// Set 'alt' based on the variant category
		switch(variantCategory) {
			case SNP_A:
				alt = "A";
				break;
			case SNP_C:
				alt = "C";
				break;
			case SNP_G:
				alt = "G";
				break;
			case SNP_T:
				alt = "T";
				break;
			default:
				throw new RuntimeException("Cannot create DataFrameSnp for variant category: " + variantCategory);
		}
	}

	public String getAlt() {
		return alt;
	}

	@Override
	public Object getData(String columnName, int pos, String ref, String alt) {
		var column = columns.get(columnName);
		Log.debug("COLUMN: " + columnName + "\t" + column);
		if(column == null) throw new RuntimeException("Cannot find column: " + columnName);
		int idx = posIndex.indexOf(pos);
		Log.debug("IDX: " + idx);
		if(idx < 0) return null;
		Log.debug("VALUE: " + column.get(idx));
		return column.get(idx);
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
