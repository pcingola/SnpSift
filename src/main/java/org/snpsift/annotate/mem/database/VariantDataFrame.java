package org.snpsift.annotate.mem.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.dataFrame.DataFrame;
import org.snpsift.annotate.mem.dataFrame.DataFrameDel;
import org.snpsift.annotate.mem.dataFrame.DataFrameIns;
import org.snpsift.annotate.mem.dataFrame.DataFrameMixed;
import org.snpsift.annotate.mem.dataFrame.DataFrameMnp;
import org.snpsift.annotate.mem.dataFrame.DataFrameOther;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.FormatUtil;
import org.snpsift.annotate.mem.VariantCategory;

/**
 * A DataFrame of variant's data that is indexed "variant type AND chromosome possition".
 * 
 * We create an "DataFrame" for each variant type: SNP(A), SNP(C), SNP(G), SNP(T), INS, DEL, MNP, MIXED, OTHER.
 * Each DataFrame is indexed by chromosome position. DataFrames have columns for each field to annotate.
 */
public class VariantDataFrame implements java.io.Serializable {

	String[] fields;	// Fields to annotate
	DataFrame dataFrames[]; // Each dataFrame indexed by variant type
	VariantTypeCounter variantTypeCounter;

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

	public VariantDataFrame(VariantTypeCounter variantTypeCounter) {
		this.variantTypeCounter = variantTypeCounter;
		this.fields = variantTypeCounter.getFields2type().keySet().toArray(new String[0]);
		// Create data sets according to the variant type, and counters for each variant type
		dataFrames = new DataFrame[VariantCategory.size()];
		dataFrames[VariantCategory.SNP_A.ordinal()] = new DataFrameSnp(variantTypeCounter, VariantCategory.SNP_A);
		dataFrames[VariantCategory.SNP_C.ordinal()] = new DataFrameSnp(variantTypeCounter, VariantCategory.SNP_C);
		dataFrames[VariantCategory.SNP_G.ordinal()] = new DataFrameSnp(variantTypeCounter, VariantCategory.SNP_G);
		dataFrames[VariantCategory.SNP_T.ordinal()] = new DataFrameSnp(variantTypeCounter, VariantCategory.SNP_T);
		dataFrames[VariantCategory.INS.ordinal()] = new DataFrameIns(variantTypeCounter, VariantCategory.INS);
		dataFrames[VariantCategory.DEL.ordinal()] = new DataFrameDel(variantTypeCounter, VariantCategory.DEL);
		dataFrames[VariantCategory.MNP.ordinal()] = new DataFrameMnp(variantTypeCounter, VariantCategory.MNP);
		dataFrames[VariantCategory.MIXED.ordinal()] = new DataFrameMixed(variantTypeCounter, VariantCategory.MIXED);
		dataFrames[VariantCategory.OTHER.ordinal()] = new DataFrameOther(variantTypeCounter, VariantCategory.OTHER);
	}

	/**
	 * Add data to this VariantDataFrame
	 */
	void add(VariantVcfEntry variantVcfEntry) {
		var dataFrame = getDataFrameByVariantType(variantVcfEntry);
		if(dataFrame == null) throw new RuntimeException("Cannot find data frame for variant: " + variantVcfEntry.toString());
		DataFrameRow row = new DataFrameRow(dataFrame, variantVcfEntry.getStart(), variantVcfEntry.getReference(), variantVcfEntry.getAlt());
		// Add fields
		var vcfEntry = variantVcfEntry.getVcfEntry();
		for(var field : fields) {
			Object value = getFieldValue(vcfEntry, field);
			row.set(field, value);
		}
		dataFrame.add(row); // Add row to dataFrame
	}

	/**
	 * Annotate a VCF entry with the fields in this VariantDataFrame
	 */
	void annotate(VcfEntry vcfEntry, String[] fields) {
		throw new RuntimeException("Unimplemented");
		// for(var variant: vcfEntry.variants()) {
		// 	var dataSet = getDataFrameByVariantType(variant);
		// 	if(dataSet == null) throw new RuntimeException("Cannot find data set for variant: " + variant.toString());

		// 	// Add fields
		// 	for(var field : fields) {
		// 		var data = dataSet.getData(field, variant.getStart(), variant.getReference(), variant.getAlt());
		// 		if(data != null) vcfEntry.addInfo(field, data.toString());
		// 	}
		// }
	}

	/** 
	 * This is used after creating the data to verify there are no issues with the dataFrames and indeces
	*/
	public void check() {
		for(var dataFrame : dataFrames) {
			dataFrame.check();
		}
	}

	/**
	 * Get a field value from a VCF entry
	 */
	Object getFieldValue(VcfEntry vcfEntry, String field) {
		var type = variantTypeCounter.getFields2type().get(field);
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
		check();
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

	public long sizeBytes() {
		long size = 0;
		for(var dataFrame : dataFrames) {
			size += dataFrame.sizeBytes();
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VariantDataFrame: memory= " + FormatUtil.formatBytes(sizeBytes()) + "\n");
		for(var vc : VariantCategory.values()) {
			sb.append("Variant category: " + vc + "\n");
			sb.append(dataFrames[vc.ordinal()] + "\n");
		}
		return sb.toString();
	}
}
