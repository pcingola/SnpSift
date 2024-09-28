package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameDel extends DataFrame {
    
    public DataFrameDel(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, false);
    }
}
