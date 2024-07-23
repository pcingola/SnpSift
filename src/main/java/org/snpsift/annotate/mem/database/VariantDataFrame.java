package org.snpsift.annotate.mem.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.dataFrame.DataFrame;
import org.snpsift.annotate.mem.dataFrame.DataFrameDel;
import org.snpsift.annotate.mem.dataFrame.DataFrameIns;
import org.snpsift.annotate.mem.dataFrame.DataFrameMixed;
import org.snpsift.annotate.mem.dataFrame.DataFrameMnp;
import org.snpsift.annotate.mem.dataFrame.DataFrameOther;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.annotate.mem.VariantCategory;

/**
 * A table of variant's data that is indexed by possition and variant type.
 * 
 * We create an "DataFrame" for each variant type: SNP(A), SNP(C), SNP(G), SNP(T), INS, DEL, MNP, MIXED, OTHER.
 */
public class VariantDataFrame implements java.io.Serializable {

	String[] fields;	// Fields to annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	DataFrame dataFrames[]; // Each dataFrame indexed by variant type

	public static VariantDataFrame load(String fileName) {
		// Deserialize data from a file
		try {
			System.out.println("Loading from file: " + fileName);
			var file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			var vd = (VariantDataFrame) ois.readObject();
			ois.close();
			return vd;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load from file '" + fileName + "'", e);
		}
	}

	public VariantDataFrame(VariantTypeCounter variantTypeCounter, Map<String, VcfInfoType> fields2type) {
		this.fields2type = fields2type;
		this.fields = fields2type.keySet().toArray(new String[0]);
		// Create data sets according to the variant type, and counters for each variant type
		dataFrames = new DataFrame[VariantCategory.size()];
		dataFrames[VariantCategory.SNP_A.ordinal()] = new DataFrameSnp(variantTypeCounter.getCount(VariantCategory.SNP_A), "A", fields2type);
		dataFrames[VariantCategory.SNP_C.ordinal()] = new DataFrameSnp(variantTypeCounter.getCount(VariantCategory.SNP_C), "C", fields2type);
		dataFrames[VariantCategory.SNP_G.ordinal()] = new DataFrameSnp(variantTypeCounter.getCount(VariantCategory.SNP_G), "G", fields2type);
		dataFrames[VariantCategory.SNP_T.ordinal()] = new DataFrameSnp(variantTypeCounter.getCount(VariantCategory.SNP_T), "T", fields2type);
		dataFrames[VariantCategory.INS.ordinal()] = new DataFrameIns(variantTypeCounter.getCount(VariantCategory.INS), fields2type);
		dataFrames[VariantCategory.DEL.ordinal()] = new DataFrameDel(variantTypeCounter.getCount(VariantCategory.DEL), fields2type);
		dataFrames[VariantCategory.MNP.ordinal()] = new DataFrameMnp(variantTypeCounter.getCount(VariantCategory.MNP), fields2type);
		dataFrames[VariantCategory.MIXED.ordinal()] = new DataFrameMixed(variantTypeCounter.getCount(VariantCategory.MIXED), fields2type);
		dataFrames[VariantCategory.OTHER.ordinal()] = new DataFrameOther(variantTypeCounter.getCount(VariantCategory.OTHER), fields2type);
	}

	/**
	 * Add data to this VariantDataFrame
	 */
	void add(VcfEntry vcfEntry) {
		for(var variant: vcfEntry.variants()) {
			var dataFrame = getDataFrameByVariantType(variant);
			if(dataFrame == null) throw new RuntimeException("Cannot find data frame for variant: " + variant.toString());

			// Add fields
			for(var field : fields2type.keySet()) {
				Object value = getFieldValue(vcfEntry, field);
				dataFrame.setData(field, value, variant.getStart(), variant.getReference(), variant.getAlt());
			}
		}
	}

	/**
	 * Annotate a VCF entry with the fields in this VariantDataFrame
	 */
	void annotate(VcfEntry vcfEntry, String[] fields) {
		for(var variant: vcfEntry.variants()) {
			var dataSet = getDataFrameByVariantType(variant);
			if(dataSet == null) throw new RuntimeException("Cannot find data set for variant: " + variant.toString());

			// Add fields
			for(var field : fields) {
				var data = dataSet.getData(field, variant.getStart(), variant.getReference(), variant.getAlt());
				if(data != null) vcfEntry.addInfo(field, data.toString());
			}
		}
	}

	/**
	 * Get a field value from a VCF entry
	 */
	Object getFieldValue(VcfEntry vcfEntry, String field) {
		var type = fields2type.get(field);
		var valueStr = vcfEntry.getInfo(field);
		if (valueStr == null) return null;
		switch(type) {
			case Flag:
				return Boolean.TRUE;	// If the field is present, it's true
			case Integer:
				return Integer.parseInt(valueStr);
			case Float:
				return Float.parseFloat(valueStr);
			case Character:
				return valueStr.charAt(0);
			case String:
				return valueStr;
			default:
				throw new RuntimeException("Unimplemented type: " + type);
		}
	}

	/**
	 * Select the appropirate data set for a variant
	 */
	public DataFrame getDataFrameByVariantType(Variant variant) {
		return getDataFrameByCategory(VariantCategory.of(variant));
	}

	public DataFrame getDataFrameByCategory(VariantCategory category) {
		return dataFrames[category.ordinal()];
	}

	/**
	 * Resize and memory optimize the data
	 */
	void resize() {
		for(var dataFrame : dataFrames) {
			dataFrame.resize();
		}
	}

	/**
	 * Save to file
	 */
	void save(String fileName) {
		resize();	// Optimize memory usage
		try {
			System.out.println("Saving to file: " + fileName);
			var file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException("Cannot save to file '" + fileName + "'", e);
		}
	}
}
