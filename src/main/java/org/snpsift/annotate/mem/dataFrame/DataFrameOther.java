package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;

/**
 * DataFrameOther is a subclass of DataFrame that is specifically tailored for handling 
 * variant type counting and categorization. It extends the functionality of DataFrame 
 * by initializing it with specific parameters.
 *
 * This class is designed to work with a VariantTypeCounter and a VariantCategory, 
 * and it sets two boolean flags to true in the superclass constructor, which might 
 * enable specific features or behaviors in the DataFrame class.
 *
 */
public class DataFrameOther extends DataFrame {

    private static final long serialVersionUID = 2024073106L;
    
    public DataFrameOther(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, true);
    }

}
