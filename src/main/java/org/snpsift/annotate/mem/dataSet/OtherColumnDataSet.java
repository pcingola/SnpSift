package org.snpsift.annotate.mem.dataSet;

import java.util.Map;

import org.snpeff.vcf.VcfInfoType;

public class OtherColumnDataSet extends IndexedColumnDataSet {
    
    public OtherColumnDataSet(int numEntries, Map<String, VcfInfoType> fields2type) {
        super(numEntries, fields2type);
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
