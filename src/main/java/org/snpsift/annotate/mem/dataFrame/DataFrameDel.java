package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;


/**
 * DataFrameDel is a specialized subclass of DataFrame designed to handle deletion variants.
 * 
 * This class extends the DataFrame class and is specifically tailored to work with 
 * deletion variants by setting the appropriate flags in the superclass constructor.
 * 
 * Constructor:
 * - DataFrameDel(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory):
 *   Initializes a new instance of DataFrameDel with the provided VariantTypeCounter and 
 *   VariantCategory. It sets the flags for handling deletions.
 */
public class DataFrameDel extends DataFrame {

    private static final long serialVersionUID = 2024073102L;

    public DataFrameDel(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, false);
    }
}
