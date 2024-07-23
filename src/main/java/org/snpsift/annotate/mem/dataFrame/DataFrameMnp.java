package org.snpsift.annotate.mem.dataFrame;

import java.util.Map;

import org.snpeff.vcf.VcfInfoType;

public class DataFrameMnp extends DataFrame {
    
    public DataFrameMnp(int numEntries, Map<String, VcfInfoType> fields2type) {
        super(numEntries, fields2type);
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
