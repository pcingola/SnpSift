package org.snpsift.annotate.mem.database;

import org.snpsift.annotate.mem.dataSet.IndexedColumnDataSet;

public class InsColumnDataSet extends IndexedColumnDataSet {

    public InsColumnDataSet(int numEntries, String[] fields) {
        super(numEntries, fields);
    }

    @Override
    public Object getData(String columnName, int pos, String ref, String alt) {
        // TODO Look up the data in the column
        // For insertions, we ignore the reference allele
        throw new UnsupportedOperationException("Unimplemented method 'getData'");
    }

    @Override
    public void setData(String columnName, Object value, int pos, String ref, String alt) {
        // TODO Set the data in the column, ignore the reference allele
        // Check that 'ref' is empty
        if (!ref.isEmpty()) throw new RuntimeException("Reference allele should be empty for insertions");
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }

}
