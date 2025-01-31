package org.snpsift.annotate.mem;

import org.snpeff.interval.Variant;

/**
 * The `VariantCategory` enum represents different categories of genetic variants.
 * It provides a method to categorize a given `Variant` object based on its type.
 * 
 * Categories include:
 *   - SNP_A, SNP_C, SNP_G, SNP_T - Single Nucleotide Polymorphisms (SNPs) for each nucleotide base
 *   - INS - Insertions
 *   - DEL - Deletions
 *   - MNP - Multiple Nucleotide Polymorphisms
 *   - MIXED - Mixed variants
 *   - OTHER - Any other type of variant not covered by the above categories
 * 
 * The `of` method determines the category of a given `Variant` by checking its type and, 
 * in the case of SNPs, the specific nucleotide base involved.
 * 
 * The `size` method returns the total number of variant categories defined in this enum.
 * 
 * Usage example:
 * ```
 *   Variant variant = ...; // Obtain a Variant object
 *   VariantCategory category = VariantCategory.of(variant);
 *   System.out.println("Variant category: " + category);
 * ```
 */
public enum VariantCategory {

	SNP_A, SNP_C, SNP_G, SNP_T, INS, DEL, MNP, MIXED, OTHER;

	public static VariantCategory of(Variant variant) {
		if(variant.isSnp()) {
			char alt = variant.getAlt().toUpperCase().charAt(0);
			switch (alt) {
				case 'A':
					return SNP_A;
				case 'C':
					return SNP_C;
				case 'G':
					return SNP_G;
				case 'T':
					return SNP_T;
				default:
					throw new RuntimeException("Unknown SNP: " + variant.getAlt() + "\t" + variant.toString());
			} 
		} else if(variant.isIns()) {
			return INS;
		} else if(variant.isDel()) {
			return DEL;
		} else if(variant.isMnp()) {
			return MNP;
		} else if(variant.isMixed()) {
			return MIXED;
		} else {
			return OTHER;
		}
	}

	public static int size() {
		return VariantCategory.values().length;
	}
}