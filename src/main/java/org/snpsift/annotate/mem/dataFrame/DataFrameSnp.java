package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * The DataFrameSnp class extends the DataFrame class and represents a specific type of data frame
 * that deals with single nucleotide polymorphisms (SNPs). Each instance of DataFrameSnp is associated
 * with a specific alternative allele ('A', 'C', 'G', or 'T') based on the provided variant category.
 *
 * This class provides methods to retrieve the alternative allele and to check if a given entry matches
 * specific position and allele values.
 *
 * Limitations:
 * - The class assumes that the variant category provided during instantiation is one of the SNP categories
 *   (SNP_A, SNP_C, SNP_G, SNP_T). If an invalid category is provided, a RuntimeException is thrown.
 * - The eq method assumes that the alternative allele (alt) is always in uppercase. If the provided alt
 *   parameter in the eq method is not in uppercase, a RuntimeException is thrown.
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
