package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameOther extends DataFrame {
    
    public DataFrameOther(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, true);
    }

}
