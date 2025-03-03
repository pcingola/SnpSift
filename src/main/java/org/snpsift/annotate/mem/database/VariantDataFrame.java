package org.snpsift.annotate.mem.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.snpeff.interval.Variant;
import org.snpeff.util.Log;
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
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.VariantCategory;

/**
 * A DataFrame of variant's data that is indexed "variant type AND chromosome possition".
 * 
 * We create an "DataFrame" for each variant type: SNP(A), SNP(C), SNP(G), SNP(T), INS, DEL, MNP, MIXED, OTHER.
 * Each DataFrame is indexed by chromosome position. DataFrames have columns for each field to annotate.
 * 
 * The `VariantDataFrame` class represents a data structure for storing and annotating variant data indexed by variant type and chromosome position.
 * 
 * This class manages multiple `DataFrame` objects, each corresponding to a different variant type (e.g., SNP, INS, DEL, MNP, MIXED, OTHER).
 * Each `DataFrame` is indexed by chromosome position and contains columns for various fields to annotate.
 * 
 * Key functionalities of this class include:
 * 
 * - Loading and saving `VariantDataFrame` objects from/to files.
 * - Adding variant data to the appropriate `DataFrame`.
 * - Annotating VCF entries with fields from the `VariantDataFrame`.
 * - Checking the integrity of the data frames.
 * - Resizing and optimizing memory usage of the data frames.
 * 
 * The class also provides methods to set a prefix for field names, calculate the total memory size of the data frames, and generate a string representation of the `VariantDataFrame`.
 * 
 * The class implements `Serializable` to allow for serialization and deserialization of its instances.
 * 
 * Example usage:
 * 
 * - Load a `VariantDataFrame` from a file:
 *   `VariantDataFrame vd = VariantDataFrame.load(chr, fileName, emptyIfNotFound);`
 * 
 * - Add a variant to the `VariantDataFrame`:
 *   `vd.add(variantVcfEntry);`
 * 
 * - Annotate a VCF entry:
 *   `int found = vd.annotate(vcfEntry, fieldNames);`
 * 
 * - Save the `VariantDataFrame` to a file:
 *   `vd.save(fileName);`
 * 
 * - Check the integrity of the data frames:
 *   `vd.check();`
 * 
 * - Resize and optimize memory usage:
 *   `vd.resize();`
 * 
 * - Set a prefix for field names:
 *   `vd.setPrefix(prefix);`
 * 
 * - Get the total memory size of the data frames:
 *   `long size = vd.sizeBytes();`
 * 
 * - Get a string representation of the `VariantDataFrame`:
 *   `String str = vd.toString();`
 * 
 */
public class VariantDataFrame implements Serializable {

	private static final long serialVersionUID = 2024092701L;

	Fields fields;	// Fields to annotate
	DataFrame dataFrames[]; // Each dataFrame indexed by variant type
	String prefix; // Prefix for inf field names added
	VariantTypeCounter variantTypeCounter;

	public static VariantDataFrame load(String chr, String fileName, boolean emptyIfNotFound) {
		// Deserialize data from a file
		try {
			var file = new File(fileName);
			if(!file.exists()) {
				if(emptyIfNotFound) {
					Log.warning("File not found for chromosome '" + chr + "', file '" + fileName + "'");
					return new VariantDataFrame(new VariantTypeCounter(new Fields()));
				}
				throw new RuntimeException("File not found for chromosome '" + chr + "', '" + fileName + "'");
			}
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			var vd = (VariantDataFrame) ois.readObject();
			ois.close();
			return vd;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load file for chromosome '" + chr + "', file '" + fileName + "'", e);
		}
	}

	public VariantDataFrame(VariantTypeCounter variantTypeCounter) {
		this.variantTypeCounter = variantTypeCounter;
		this.fields = variantTypeCounter.getFields();
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
		// Add fields to the row
		for(var field : fields) {
			Object value = Fields.getFieldValue(field, variantVcfEntry);
			row.set(field.getId(), value);
		}
		// Add row to dataFrame
		try {
			dataFrame.add(row);
		} catch (Exception e) {
			throw new RuntimeException("Error adding row to dataFrame:" //
										+ "\n\trow: " + row //
										+ "\n\tvariant category: " + VariantCategory.of(variantVcfEntry) //
										+ "\n\t" + variantVcfEntry //
										, e);
		}
	}

	/**
	 * Annotate a VCF entry with the fields in this VariantDataFrame
	 */
	public int annotate(VcfEntry vcfEntry, String[] fieldNames) {
		int found = 0;
		for(var variant: vcfEntry.variants()) {
			var dataFrame = getDataFrameByVariantType(variant);
			if(dataFrame == null) throw new RuntimeException("Cannot find data set for variant: " + variant.toString());

			// Get the row for this variant
			DataFrameRow row = dataFrame.getRow(variant.getStart(), variant.getReference(), variant.getAlt());
			if(row == null) continue;	// No data for this variant

			// Add fields to the VCF entry
			if( fieldNames != null ) {
				// Add only the requested fields to the VCF entry
				for(var field : fieldNames) {
					var value = row.getDataFrameValue(field);
					if(value != null) {
						String outFieldName = (prefix != null ? prefix + field : field );
						vcfEntry.addInfo(outFieldName, value.toString());
						found++;
					}
				}
			} else {
				// Add all fields to the VCF entry
				for(var field : row) {
					var value = row.getDataFrameValue(field);
					if(value != null) {
						String outFieldName = (prefix != null ? prefix + field : field );
						vcfEntry.addInfo(outFieldName, value.toString());
						found++;
					}
				}
			}
		}
		return found;
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
			Log.info("Saving to file '" + fileName + "'");
			var file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException("Cannot save to file '" + fileName + "'", e);
		}
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
