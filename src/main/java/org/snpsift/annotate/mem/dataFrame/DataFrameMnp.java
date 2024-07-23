package org.snpsift.annotate.mem.dataFrame;

import java.util.Map;

import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

public class DataFrameMnp extends DataFrame {
    
    public DataFrameMnp(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory);
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
