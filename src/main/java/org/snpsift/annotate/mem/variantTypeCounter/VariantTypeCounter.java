package org.snpsift.annotate.mem.variantTypeCounter;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;

/**
 * Count different types of variants
 * These statistics are used to create data sets
 */
public class VariantTypeCounter {

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
	};

	protected Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	protected String[] fieldsString; // Fields to create or annotate
	protected int countByCategory[]; // Count by category
	protected long countVcfEntries = 0;
	protected long countVariants = 0;
	protected Map<String, int[]> sizeByField; // Total size (in bytes) by field (only string fields)

	public VariantTypeCounter(Map<String, VcfInfoType> fields2type) {
		this.fields2type = fields2type;
		fieldsString = fields2type.values().stream().filter(t -> t == VcfInfoType.String).toArray(String[]::new);
		var numCategories = VariantCategory.values().length;
		countByCategory= new int[numCategories]; // Count by category
		// Size by field
		Map<String, int[]> sizeByField = new HashMap<>();
		for(var field: fieldsString) {
			sizeByField.put(field, new int[numCategories]);
		}
	}

	/**
	 * Count the number of variants in a VCF file
	 */
	public void count(VcfEntry vcfEntry) {
		countVcfEntries++;

		// Count all variants (i.e. all ALTs)
		for(var variant: vcfEntry.variants()) {
			countVariants++;
			var variantCategory = VariantCategory.of(variant);
			int variantCategoryOrd = variantCategory.ordinal();
			countByCategory[variantCategoryOrd]++;
			// Size of each 'string' field
			for(var field: fieldsString) {
				var fieldValue = vcfEntry.getInfo(field);
				if(fieldValue == null) continue;
				int sizes[] = sizeByField.get(field);
				sizes[variantCategoryOrd] += fieldValue.length();
			}
		}
	}

	public int getCount(VariantCategory variantCategory) {
		return countByCategory[variantCategory.ordinal()];
	}

	public String toString() {
		var sb = new StringBuffer();
		sb.append("VariantTypeCounter:\n");
		sb.append("\tVCF entries: " + countVcfEntries + "\n");
		sb.append("\tVariants: " + countVariants + "\n");
		// Show counterd by "category"
		for(var variantCategory: VariantCategory.values()) {
			var variantCategoryOrd = variantCategory.ordinal();
			sb.append("\t" + variantCategory + ": " + countByCategory[variantCategoryOrd] + "\n");
			// Show string field total lengths by category
			for(var field: fieldsString) {
				sb.append("\t\t" + field + ": " + sizeByField.get(field)[variantCategoryOrd] + "\n");
			}
		}
		return sb.toString();
	}
}
