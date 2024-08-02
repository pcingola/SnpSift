package org.snpsift.annotate.mem.variantTypeCounter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.util.FormatUtil;

/**
 * Count different types of variants
 * These statistics are used to create data sets
 */
public class VariantTypeCounter implements Serializable{

	private static final long serialVersionUID = 2024073102L;

	public static final String REF = "REF";
	public static final String ALT = "ALT";

	protected Fields fields; // Fields to create or annotate
	protected String[] fieldsString; // Fields to create or annotate
	protected int countByCategory[]; // Count by category
	protected long countVcfEntries = 0;
	protected long countVariants = 0;
	protected Map<String, int[]> sizesByField; // Total size (in bytes) by field (only string fields)


	/**
     * Count variants in a string of VCF lines (i.e. a VCF file contents in a string)
	 * This is used for testing
     */
    public static VariantTypeCounter countVariants(String vcfLines) {
        VcfHeader vcfHeader = FormatUtil.lines2VcfFileIterator(vcfLines).readHeader(); // Skip header

        // Create a map of names to types
        Fields fields = new Fields();
        for(VcfHeaderInfo vi : vcfHeader.getVcfHeaderInfo()) {
            if( ! vi.isImplicit() ) fields.add(vi);
        }

        // Create a variant type counter and count variants
        var variantTypeCounter = new VariantTypeCounter(fields);
        for(VcfEntry vcfEntry : FormatUtil.lines2VcfFileIterator(vcfLines)) {
            variantTypeCounter.count(vcfEntry);
        }
        return variantTypeCounter;
    }

	public VariantTypeCounter(Fields fields) {
		this.fields = fields;
		// Get all 'string' fields
		var fieldsStringList = new ArrayList<>();
		for(var field: fields.getNames()) {
			if(fields.get(field).getVcfInfoType() == VcfInfoType.String) fieldsStringList.add(field);
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
		sizesByField.put(REF, new int[numberOfCategories]);
		sizesByField.put(ALT, new int[numberOfCategories]);
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
			for(var fieldName: fieldsString) {
				var fieldValue = vcfEntry.getInfo(fieldName);
				updateSizes(variantCategory, fieldName, fieldValue);
			}
			// Size of REF and ALT
			updateSizes(variantCategory, REF, variant.getReference());
			updateSizes(variantCategory, ALT, variant.getAlt());
		}
	}

	public int getCount(VariantCategory variantCategory) {
		return countByCategory[variantCategory.ordinal()];
	}

	public int[] getCountByCategory() {
		return countByCategory;
	}

	public Fields getFields() {
		return fields;
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

	public Map<String, int[]> getSizesByField() {
		return sizesByField;
	}

	@Override
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
		// Convert the string to UTF-8 bytes, otherwise we'll get the number of characters (not bytes)
		byte[] utf8Bytes = value.getBytes(StandardCharsets.UTF_8);
		sizes[variantCategory.ordinal()] += utf8Bytes.length;
	}

}
