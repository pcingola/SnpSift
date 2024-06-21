package org.snpsift.annotate.mem.database;

import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;

public class OtherColumnDataSet extends IndexedColumnDataSet {
    
    public OtherColumnDataSet(int numEntries, String[] fields) {
        super(numEntries, fields);
    }

    @Override
    public Object getData(String columnName, int pos, String ref, String alt) {
        // TODO Look up the data in the column
        throw new UnsupportedOperationException("Unimplemented method 'getData'");
    }

    @Override
    public void setData(String columnName, Object value, int pos, String ref, String alt) {
        // TODO Set the data in the column, ignore the reference and alternative alleles
        // Check that 'ref' and 'alt' are not empty
        if (!ref.isEmpty() || !alt.isEmpty()) throw new RuntimeException("Reference and alternative alleles should be empty for other types");
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }

}
