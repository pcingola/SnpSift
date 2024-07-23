package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameIns extends DataFrame {

    public DataFrameIns(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory);
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
