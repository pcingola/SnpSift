package org.snpsift.annotate.mem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snpeff.util.Log;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;

/**
 * The Fields class represents a collection of VCF (Variant Call Format) header information fields.
 * These Fields are stored in VariantDatabase / VariantDataFrame objects, that are used to annotate VCF files.
 * 
 * It provides methods to manipulate and retrieve field values from VCF entries.
 * 
 * This class implements the Iterable interface to allow iteration over VcfHeaderInfo objects.
 * It also implements Serializable to allow instances to be serialized and deserialized.
 * 
 * Fields are stored in a Map with their names as keys and VcfHeaderInfo objects as values.
 * The class provides methods to add fields, retrieve field values, and save/load the Fields object to/from a file.
 * 
 * Limitations:
 * - The class cannot use REF/ALT fields directly because they are used to separate variant types in the database.
 * - INFO and FORMAT fields are not appropriate to be stuffed into an INFO field.
 * - The class assumes that certain VCF columns (e.g., CHROM, POS, ID, QUAL, FILTER) are implicitly present and handles them separately.
 * - The getFieldValue method handles different VCF info types (Flag, Integer, Float, Character, String) and converts values accordingly.
 * - The class does not handle all possible VCF info types and throws a RuntimeException for unimplemented types.
 */

public class Fields implements Iterable<VcfHeaderInfo>, Serializable {

	private static final long serialVersionUID = 2024092801L;
	
	/* 
	Fields that are columns in VCF files.
	Note that we cannot use REF/ALT because the way the Database is built (they are used to separate variant types), and 
	we cannot use INFO and FORMAT (it is not appropriate to stuff the whole INFO or FORMAT fields into an INFO field).
	*/
	public static final Set<String> VCF_COLUMN_FIELD_NAMES = new HashSet<String>(Arrays.asList("CHROM", "POS", "ID", "QUAL", "FILTER")); 
    
    String[] fieldNames; // Fields to create or annotate
    Map<String, VcfHeaderInfo> fieldByName; // Fields to create or annotate

	/**
	 * Get a field value from a VCF entry
	 */
	public static Object getFieldValue(VcfHeaderInfo vcfHeaderInfo, VariantVcfEntry varVcfEntry) {
		var type = vcfHeaderInfo.getVcfInfoType();
		var vcfEntry = varVcfEntry.getVcfEntry();
		String valueStr;
		var fieldName = vcfHeaderInfo.getId();
		// Do we need to annotate for a specific "ALT"?
		var vin = vcfHeaderInfo.getVcfInfoNumber();
		// Get 'ALT' dependent values?
		if((vin == VcfInfoNumber.ALLELE || vin == VcfInfoNumber.ALL_ALLELES)	// Is this a field that depends on the ALT?
			&& (type != VcfInfoType.Flag)	// 'Flag' fields are either present or not, so they are not dependent on the ALT
			) {
			valueStr = vcfEntry.getInfo(fieldName, varVcfEntry.getAlt());
		} else {
			valueStr = getFieldValueString(fieldName, vcfEntry);
		}
		// Convert the value to the appropriate type (handle missing values)
		switch(type) {
			case Flag:
				return (valueStr != null); // Flag is present
			case Integer:
				if (valueStr == null) return null;
				try {
					return Integer.parseInt(valueStr);
				} catch (Exception e) {
					Log.warning("Could not pase field '" + vcfHeaderInfo.getId() + "', value '" + valueStr + "' as integer for field '" + vcfHeaderInfo.getId() + "' in VCF entry: " + vcfEntry.getChromosomeNameOri() + ":" + (vcfEntry.getStart() + 1));
					return null;
				}
			case Float:
				if (valueStr == null) return null;
				try {
					return Double.parseDouble(valueStr);
				} catch (Exception e) {
					Log.warning("Could not pase field '" + vcfHeaderInfo.getId() + "', value '" + valueStr + "' as float for field '" + vcfHeaderInfo.getId() + "' in VCF entry: " + vcfEntry.getChromosomeNameOri() + ":" + (vcfEntry.getStart() + 1));
					return null;
				}				
			case Character:
				return (valueStr != null) && valueStr.length() > 0 ? valueStr.charAt(0) : null;
			case String:
				return valueStr;
			default:
				throw new RuntimeException("Unimplemented type: " + type);
		}
	}

	/**
	 * Get a field value (string) from a VCF entry
	 */
	public static String getFieldValueString(String fieldName, VcfEntry vcfEntry) {
		if( VCF_COLUMN_FIELD_NAMES.contains(fieldName)) {
			// Implicit fields that are VCF columns: 'CHROM', 'POS', 'ID', 'REF', 'ALT', 'QUAL', 'FILTER'
			switch(fieldName) {
				case "ID":
					return vcfEntry.getId();
				case "CHROM":
					return vcfEntry.getChromosomeNameOri();
				case "POS":
					return "" + (vcfEntry.getStart() + 1);
				case "REF":
					return vcfEntry.getRef();
				case "ALT":
					return vcfEntry.getAltsStr();
				case "QUAL":
					return Double.toString(vcfEntry.getQuality());
				case "FILTER":
					return vcfEntry.getFilter();
				default:
					throw new RuntimeException("Unimplemented field: " + fieldName);
			}
		}
		return vcfEntry.getInfo(fieldName);
	}

	public static Fields load(String fileName) {
		// Deserialize data from a file
		try {
			var file = new File(fileName);
			if(!file.exists()) throw new RuntimeException("File not found: '" + fileName + "'");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			var vd = (Fields) ois.readObject();
			ois.close();
			return vd;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load from file '" + fileName + "'", e);
		}
	}

    public Fields() {
        this.fieldByName = new HashMap<>();
    }

    public void add(VcfHeaderInfo field) {
        fieldByName.put(field.getId(), field);
        fieldNames = null;
    }

    public VcfHeaderInfo get(String name) {
        return fieldByName.get(name);
    }

    public String[] getNames() {
        if( fieldNames == null ) {
            fieldNames = fieldByName.keySet().toArray(new String[0]);
        }
        return fieldNames;
    }

    @Override
    public Iterator<VcfHeaderInfo> iterator() {
        return fieldByName.values().iterator();
    }

    	/**
	 * Save to file
	 */
	public void save(String fileName) {
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

	/** 
	 * Get VCF headers
	 */
	public Collection<VcfHeaderEntry> vcfHeaders(String prefix) {
		List<VcfHeaderEntry> headerInfos = new LinkedList<>();

		if( prefix == null ) {
			// No prefix? Return all fields
			headerInfos.addAll(fieldByName.values());
		} else {
			// We need to add a prefix to the field names, create a new list of VcfHeaderInfo with the prefix
			for(var field: this) {
				// Create a new field with a prefix
				var fieldToAdd = new VcfHeaderInfo(prefix + field.getId(), field.getVcfInfoType(), field.getNumberString(), field.getDescription());
				headerInfos.add(fieldToAdd);
			}
		}
		return headerInfos;
	}
}
