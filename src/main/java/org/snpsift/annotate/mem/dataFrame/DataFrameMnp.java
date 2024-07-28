package org.snpsift.annotate.mem.dataFrame;

import java.util.Map;

import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameMnp extends DataFrame {
    
    public DataFrameMnp(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory);
    }

}
