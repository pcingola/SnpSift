package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A set of DataColumns specific SNP 'alt'  (e.g. Data for SNP "N -> A")
 * A set of DataColumns, indexed by position.
 * This is used to store data for a chromosome
 */
public class DataFrameSnp extends DataFrame {

	private static final long serialVersionUID = 2024073109L;

	String alt;	// Alternative allele, i.e. one of 'A', 'C', 'G', 'T'

	public DataFrameSnp(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
		super(variantTypeCounter, variantCategory, false, false);
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

	/**
	 * Does the entry at possition 'idx' match the given (pos, ref, alt) values?
	 */
	protected boolean eq(int idx, int pos, String ref, String alt) {
		if(posIndex.get(idx) != pos) return false;
		if(!this.alt.equals(alt.toUpperCase())) throw new RuntimeException("This should never happen! Alt: " + alt + " != " + this.alt);
		return true;
	}

}
