package org.snpsift.annotate.mem.database;

import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;

public class MnpColumnDataSet extends IndexedColumnDataSet {
    
    public MnpColumnDataSet(int numEntries, String[] fields) {
        super(numEntries, fields);
    }

    @Override
    public Object getData(String columnName, int pos, String ref, String alt) {
        // TODO Look up the data in the column
        throw new UnsupportedOperationException("Unimplemented method 'getData'");
    }

    @Override
    public void setData(String columnName, Object value, int pos, String ref, String alt) {
        // TODO Set the data in the column
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }

}
