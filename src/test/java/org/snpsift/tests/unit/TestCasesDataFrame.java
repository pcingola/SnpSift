package org.snpsift.tests.unit;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrame;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;

public class TestCasesDataFrame {

    // Check data
    public static final String[] FIELDS = {"field_int", "field_float", "field_char", "field_bool", "field_string", "field_string_2"};
    public static final String[] FIELDS_STRING = {"field_string", "field_string_2", VariantTypeCounter.REF, VariantTypeCounter.ALT};


    protected DataFrameRow randDataFrameRow(RandomUtil randUtil, DataFrame dataFrame, int pos, int stringMaxLen, String ref, String alt) {
        if( ref == null) ref = randUtil.randAcgt(); // Reference: A random value from 'A', 'C', 'G', 'T'
        if(alt == null) alt = randUtil.randAcgt(); // Alt: A random value from 'A', 'C', 'G', 'T'

        var ri = randUtil.randIntOrNull();
        var rf = randUtil.randDoubleOrNull();
        var rc = randUtil.randCharOrNull();
        var rb = randUtil.randBoolOrNull();
        var rs = randUtil.randStringOrNull(stringMaxLen);
        var rs2 = randUtil.randStringOrNull(stringMaxLen);

        // Create a dataframe row, and add the random data
        DataFrameRow dataFrameRow = new DataFrameRow(dataFrame, pos, ref, alt);
        dataFrameRow.set("field_int", ri);
        dataFrameRow.set("field_float", rf);
        dataFrameRow.set("field_char", rc);
        dataFrameRow.set("field_bool", rb);
        dataFrameRow.set("field_string", rs);
        dataFrameRow.set("field_string_2", rs2);

        return dataFrameRow;
    }

    VariantTypeCounter variantTypeCounter(int size, int memSize) {
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        fields2type.put("field_bool", VcfInfoType.Flag);
        fields2type.put("field_char", VcfInfoType.Character);
        fields2type.put("field_int", VcfInfoType.Integer);
        fields2type.put("field_float", VcfInfoType.Float);
        fields2type.put("field_string", VcfInfoType.Float);
        fields2type.put("field_string_2", VcfInfoType.Float);

        for( String field: FIELDS_STRING) fields2type.put(field, VcfInfoType.String);

        VariantTypeCounter variantTypeCounter = new VariantTypeCounter(fields2type);

        // Set size and memory size for each category
        for(VariantCategory category: VariantCategory.values()) {
            variantTypeCounter.getCountByCategory()[category.ordinal()] = size;
            for(String field: FIELDS_STRING) {
                variantTypeCounter.getSizesByField().get(field)[category.ordinal()] = memSize;
            }
        }

        return variantTypeCounter;
    }

}