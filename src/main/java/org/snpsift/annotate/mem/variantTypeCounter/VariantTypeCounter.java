package org.snpsift.annotate.mem.variantTypeCounter;

import org.snpeff.vcf.VcfEntry;

/**
 * Count different types of variants
 * These statistics are used to create data sets
 */
public class VariantTypeCounter {

	public int countSnps = 0; // Count SNPs
	public int countSnpA = 0, countSnpC = 0, countSnpG = 0, countSnpT = 0;	// Count SNPs
	public int countIns = 0, countDel = 0, countMnp = 0, countMixed = 0, countOther = 0;	// Count insertions, deletions and other variants
	public int countVariants = 0; // Count all variants
	public int countVcfEntries = 0; // Count VCF entries

	/**
	 * Count the number of variants in a VCF file
	 */
	public void count(VcfEntry vcfEntry) {
		countVcfEntries++;

		// Count all ALTs
		for(var variant: vcfEntry.variants()) {
			countVariants++;
			if(variant.isSnp()) {
				countSnps++;
				switch (variant.getAlt().toUpperCase()) {
					case "A":
						countSnpA++;
						break;
					case "C":
						countSnpC++;
						break;
					case "G":
						countSnpG++;
						break;
					case "T":
						countSnpT++;
						break;
					default:
						throw new RuntimeException("Unknown SNP: " + variant.getAlt() + "\t" + vcfEntry.toString());
				} 
			} else if(variant.isIns()) {
				countIns++;
			} else if(variant.isDel()) {
				countDel++;
			} else if(variant.isMnp()) {
				countMnp++;
			} else if(variant.isMixed()) {
				countMixed++;
			} else {
				countOther++;
			}
		}
	}

	public String toString() {
		return "\tVCF entries: " + countVcfEntries
							+ "\n\tVariants: " + countVariants
							+ "\n\tSNPs: " + countSnps + " (" + (100.0 * countSnps / countVariants) + "%)"
							+ "\n\tSNP A: " + countSnpA
							+ "\n\tSNP C: " + countSnpC
							+ "\n\tSNP G: " + countSnpG
							+ "\n\tSNP T: " + countSnpT
							+ "\n\tInsertions: " + countIns
							+ "\n\tDeletions: " + countDel
							+ "\n\tMNPs: " + countMnp
							+ "\n\tMixed: " + countMixed
							+ "\n\tOther: " + countOther
							+ "\n"
							;
	}
}
