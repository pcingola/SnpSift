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
import org.snpsift.annotate.mem.dataSet.DelColumnDataSet;
import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;
import org.snpsift.annotate.mem.dataSet.InsColumnDataSet;
import org.snpsift.annotate.mem.dataSet.MixedColumnDataSet;
import org.snpsift.annotate.mem.dataSet.MnpColumnDataSet;
import org.snpsift.annotate.mem.dataSet.OtherColumnDataSet;
import org.snpsift.annotate.mem.dataSet.SnpColumnDataSet;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A table of variant's data that is indexed by possition and variant type.
 * 
 * We create an "IndexedColumnDataSet" for each variant type: SNP(A), SNP(C), SNP(G), SNP(T), INS, DEL, MNP, MIXED, OTHER.
 */
public class VariantDatabaseChr implements java.io.Serializable {

	String[] fields;	// Fields to annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	SnpColumnDataSet snpA, snpC, snpG, snpT;	// Data sets for each SNP
	IndexedColumnDataSet ins, del;	// Data sets for insertions and deletions
	IndexedColumnDataSet mnp, mixed, other;	// Data set for other variants

	public static VariantDatabaseChr load(String fileName) {
		// Deserialize data from a file
		try {
			System.out.println("Loading from file: " + fileName);
			var file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			var vd = (VariantDatabaseChr) ois.readObject();
			ois.close();
			return vd;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load from file '" + fileName + "'", e);
		}
	}

	public VariantDatabaseChr(VariantTypeCounter variantTypeCounter, Map<String, VcfInfoType> fields2type) {
		this.fields2type = fields2type;
		this.fields = fields2type.keySet().toArray(new String[0]);
		// Create data sets according to the variant type, and counters for each variant type
		snpA = new SnpColumnDataSet(variantTypeCounter.countSnpA, "A", fields2type);
		snpC = new SnpColumnDataSet(variantTypeCounter.countSnpC, "C", fields2type);
		snpG = new SnpColumnDataSet(variantTypeCounter.countSnpG, "G", fields2type);
		snpT = new SnpColumnDataSet(variantTypeCounter.countSnpT, "T", fields2type);
		ins = new InsColumnDataSet(variantTypeCounter.countIns, fields2type);
		del = new DelColumnDataSet(variantTypeCounter.countDel, fields2type);
		mnp = new MnpColumnDataSet(variantTypeCounter.countMnp, fields2type);
		mixed = new MixedColumnDataSet(variantTypeCounter.countMixed, fields2type);
		other = new OtherColumnDataSet(variantTypeCounter.countOther, fields2type);
	}

	/**
	 * Add data to the database
	 */
	void add(VcfEntry vcfEntry) {
		for(var variant: vcfEntry.variants()) {
			var dataSet = getDataSetByVariantType(variant);
			if(dataSet == null) throw new RuntimeException("Cannot find data set for variant: " + variant.toString());

			// Add fields
			for(var field : fields2type.keySet()) {
				Object value = getFieldValue(vcfEntry, field);
				if(value == null) continue;	// No value (i.e. field not present in VCF entry
				dataSet.setData(field, value, variant.getStart(), variant.getReference(), variant.getAlt());
			}
		}
	}

	/**
	 * Annotate a VCF entry
	 */
	void annotate(VcfEntry vcfEntry, String[] fields) {
		for(var variant: vcfEntry.variants()) {
			var dataSet = getDataSetByVariantType(variant);
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
	IndexedColumnDataSet getDataSetByVariantType(Variant variant) {
		if(variant.isSnp()) {
			switch (variant.getAlt().toUpperCase()) {
				case "A":
					return snpA;
				case "C":
					return snpC;
				case "G":
					return snpG;
				case "T":
					return snpT;
				default:
					throw new RuntimeException("Unknown SNP: " + variant.getAlt() + "\t" + variant.toString());
			} 
		} else if(variant.isIns()) {
			return ins;
		} else if(variant.isDel()) {
			return del;
		} else if(variant.isMnp()) {
			return mnp;
		} else if(variant.isMixed()) {
			return mixed;
		} else {
			return other;
		}
	}

	/**
	 * Resize and memory optimize the data
	 */
	void resize() {
		var columns = new IndexedColumnDataSet[] { snpA, snpC, snpG, snpT, ins, del, mnp, mixed, other };
		for(var column: columns) {
			column.resize();
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
