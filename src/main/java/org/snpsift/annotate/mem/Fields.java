package org.snpsift.annotate.mem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.snpeff.vcf.VcfHeaderInfo;

public class Fields implements Iterable<VcfHeaderInfo> {
    
    String[] fieldNames; // Fields to create or annotate
    Map<String, VcfHeaderInfo> fieldByName; // Fields to create or annotate

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

}
