package org.snpsift.annotate.mem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfHeaderInfo;

public class Fields implements Iterable<VcfHeaderInfo>, Serializable {
    
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

}
