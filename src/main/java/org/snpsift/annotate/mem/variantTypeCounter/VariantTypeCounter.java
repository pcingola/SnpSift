package org.snpsift.annotate.mem.variantTypeCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;

/**
 * Count different types of variants
 * These statistics are used to create data sets
 */
public class VariantTypeCounter {

	protected Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	protected String[] fieldsString; // Fields to create or annotate
	protected int countByCategory[]; // Count by category
	protected long countVcfEntries = 0;
	protected long countVariants = 0;
	protected Map<String, int[]> sizesByField; // Total size (in bytes) by field (only string fields)

	public VariantTypeCounter(Map<String, VcfInfoType> fields2type) {
		this.fields2type = fields2type;
		// Get all 'string' fields
		var fieldsStringList = new ArrayList<>();
		for(var field: fields2type.keySet()) {
			if(fields2type.get(field) == VcfInfoType.String) fieldsStringList.add(field);
		}
		fieldsString = fieldsStringList.toArray(new String[0]);
		// Initialize counters
		var numberOfCategories = VariantCategory.size();
		countByCategory= new int[numberOfCategories]; // Count by category
		// Initialize field size counters
		sizesByField = new HashMap<>();
		for(var field: fieldsString) {
			sizesByField.put(field, new int[numberOfCategories]);
		}
	}

	/**
	 * Count the number of variants in a VCF file
	 */
	public void count(VcfEntry vcfEntry) {
		countVcfEntries++;	// Count VCF entries
		// Count all variants (i.e. all ALTs)
		for(var variant: vcfEntry.variants()) {
			countVariants++;	// Count variants
			// Get variant category
			var variantCategory = VariantCategory.of(variant);
			int variantCategoryOrd = variantCategory.ordinal();
			// Count by category
			countByCategory[variantCategoryOrd]++;
			// Size of each 'string' field
			for(var field: fieldsString) {
				var fieldValue = vcfEntry.getInfo(field);
				updateSizes(variantCategory, field, fieldValue);
			}
		}
	}

	public int getCount(VariantCategory variantCategory) {
		return countByCategory[variantCategory.ordinal()];
	}

	/**
	 * Get total size (in bytes) of a field (only string fields)
	 * @return Total size (in bytes) or -1 if the field is not found
	 */
	public int getSize(VariantCategory variantCategory, String field) {
		var sizes = sizesByField.get(field);
		if(sizes == null) return -1;
		return sizes[variantCategory.ordinal()];
	}

	public String toString() {
		var sb = new StringBuffer();
		sb.append("VariantTypeCounter:");
		sb.append("VCF entries: " + countVcfEntries);
		sb.append(", Variants: " + countVariants + "\n");
		// Show counterd by "category"
		for(var variantCategory: VariantCategory.values()) {
			var variantCategoryOrd = variantCategory.ordinal();
			sb.append("\t" + variantCategory + ": " + countByCategory[variantCategoryOrd] + ". ");
			// Show string field total lengths by category
			for(var field: fieldsString) {
				sb.append(field + ": " + sizesByField.get(field)[variantCategoryOrd] + ", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Update sizes for a field
	 */
	protected void updateSizes(VariantCategory variantCategory, String field, String value) {
		if(value == null) return;
		int sizes[] = sizesByField.get(field);
		sizes[variantCategory.ordinal()] += value.length();
	}
}
