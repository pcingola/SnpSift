
package org.snpsift.annotate.mem.dataFrame;

import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;


/**
 * DataFrameMixed is a specialized subclass of DataFrame that is designed to handle mixed data types.
 * It extends the functionality of the DataFrame class by allowing both variant type counting and 
 * variant categorization to be enabled simultaneously.
 *
 * This class is initialized with a VariantTypeCounter and a VariantCategory, and it sets both 
 * the variant type counting and variant categorization flags to true.
 */
public class DataFrameMixed extends DataFrame {

    private static final long serialVersionUID = 2024073104L;

    public DataFrameMixed(VariantTypeCounter variantTypeCounter, VariantCategory variantCategory) {
        super(variantTypeCounter, variantCategory, true, true);
    }

}
