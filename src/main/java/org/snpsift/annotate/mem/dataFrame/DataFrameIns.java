package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * DataFrameIns is a specialized subclass of DataFrame designed to handle insertion variants.
 * 
 * This class extends the DataFrame class and is specifically tailored to work with insertion
 * variants by initializing the DataFrame with specific parameters.
 * 
 * Constructor:
 * - DataFrameIns(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory):
 *   Initializes the DataFrameIns object with the given VariantTypeCounter and VariantCategory.
 *   The constructor sets the 'isSorted' parameter to false and 'isUnique' parameter to true.
 */
public class DataFrameIns extends DataFrame {

    private static final long serialVersionUID = 2024073103L;

    public DataFrameIns(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, false, true);
    }

}
