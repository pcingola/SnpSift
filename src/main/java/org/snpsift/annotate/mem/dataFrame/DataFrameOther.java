package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameOther extends DataFrame {
    
    public DataFrameOther(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory);
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
