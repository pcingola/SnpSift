package org.snpsift.annotate.mem.database;

import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;

public class DelColumnDataSet extends IndexedColumnDataSet {
    
    public DelColumnDataSet(int numEntries, String[] fields) {
        super(numEntries, fields);
    }

    @Override
    public Object getData(String columnName, int pos, String ref, String alt) {
        // TODO Look up the data in the column
        // For deletions, we ignore the alternative allele
        throw new UnsupportedOperationException("Unimplemented method 'getData'");
    }

    @Override
    public void setData(String columnName, Object value, int pos, String ref, String alt) {
        // TODO Set the data in the column, ignore the alternative allele
        // Check that 'alt' is empty
        if (!alt.isEmpty()) throw new RuntimeException("Alternative allele should be empty for deletions");
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
}
