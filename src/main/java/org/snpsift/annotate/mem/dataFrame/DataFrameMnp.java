
package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;


/**
 * DataFrameMnp is a specialized subclass of DataFrame designed to handle 
 * multi-nucleotide polymorphisms (MNPs). This class extends the functionality 
 * of the DataFrame class by providing specific handling for MNPs.
 * 
 * The constructor initializes the DataFrameMnp with a VariantTypeCounter and 
 * a VariantCategory, and sets the flags for handling MNPs to true.
 */
public class DataFrameMnp extends DataFrame {
    
    private static final long serialVersionUID = 2024073105L;

    public DataFrameMnp(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, true);
    }

}
