package org.snpsift.annotate.mem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;

public class Fields implements Iterable<VcfHeaderInfo>, Serializable {

	private static final long serialVersionUID = 2024092801L;
    
    String[] fieldNames; // Fields to create or annotate
    Map<String, VcfHeaderInfo> fieldByName; // Fields to create or annotate

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
