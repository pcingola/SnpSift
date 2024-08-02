package org.snpsift.annotate.mem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Fields implements Iterable<Field> {
    
    String[] fieldNames; // Fields to create or annotate
    Map<String, Field> fieldByName; // Fields to create or annotate

    public Fields() {
        this.fieldByName = new HashMap<>();
    }

    public void add(Field field) {
        fieldByName.put(field.getName(), field);
        fieldNames = null;
    }

    public Field get(String name) {
        return fieldByName.get(name);
    }

    public String[] getNames() {
        if( fieldNames == null ) {
            fieldNames = fieldByName.keySet().toArray(new String[0]);
        }
        return fieldNames;
    }

    @Override
    public Iterator<Field> iterator() {
        return fieldByName.values().iterator();
    }

}
