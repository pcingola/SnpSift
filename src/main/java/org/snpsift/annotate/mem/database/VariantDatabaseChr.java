package org.snpsift.annotate.mem.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;
import org.snpsift.annotate.mem.dataSet.SnpColumnDataSet;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * A database of variant's data that is indexed by possition and variant type.
 * We create an "IndexedColumnDataSet" for each variant type: SNP(A), SNP(C), SNP(G), SNP(T), INS, DEL, MNP, MIXED, OTHER.
 */
public class VariantDatabaseChr {

	String[] fields;	// Fields to annotate
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

	public VariantDatabaseChr(VariantTypeCounter variantTypeCounter, String[] fields) {
		// Create data sets
		snpA = new SnpColumnDataSet(variantTypeCounter.countSnpA, "A", fields);
		snpC = new SnpColumnDataSet(variantTypeCounter.countSnpC, "C", fields);
		snpG = new SnpColumnDataSet(variantTypeCounter.countSnpG, "G", fields);
		snpT = new SnpColumnDataSet(variantTypeCounter.countSnpT, "T", fields);
		ins = new InsColumnDataSet(variantTypeCounter.countIns, fields);
		del = new DelColumnDataSet(variantTypeCounter.countDel, fields);
		mnp = new MnpColumnDataSet(variantTypeCounter.countMnp, fields);
		mixed = new MixedColumnDataSet(variantTypeCounter.countMixed, fields);
		other = new OtherColumnDataSet(variantTypeCounter.countOther, fields);
	}

	/**
	 * Add data to the database
	 */
	void add(VcfEntry vcfEntry, String[] fields) {
		for(var variant: vcfEntry.variants()) {
			var dataSet = getDataSetByVariantType(variant);
			if(dataSet == null) throw new RuntimeException("Cannot find data set for variant: " + variant.toString());

			// Add fields
			for(var field : fields) {
				String value = vcfEntry.getInfo(field);
				if(value != null) dataSet.setData(field, value, variant.getStart(), variant.getReference(), variant.getAlt());
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
	 * Save to file
	 */
	void save(String fileName) {
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
