package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.snpeff.util.Tuple;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrame;
import org.snpsift.annotate.mem.dataFrame.DataFrameMnp;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounter;
import org.snpsift.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

public class TestCasesDataFrame {

    // Check data
    public static final String[] FIELDS = {"field_int", "field_float", "field_char", "field_bool", "field_string", "field_string_2"};
    public static final String[] FIELDS_STRING = {"field_string", "field_string_2", VariantTypeCounter.REF, VariantTypeCounter.ALT};


    protected DataFrameRow randDataFrameRow(RandomUtil randUtil, DataFrame dataFrame, int pos, int stringMaxLen, VariantCategory variantCategory) {
        Tuple<String, String> refAlt = randUtil.randVariant(variantCategory);
        var ref = refAlt.first;
        var alt = refAlt.second;

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

    protected String randAlt(RandomUtil randUtil, VariantCategory variantCategory, String ref, String alt) {
        if( alt != null ) return alt;
        switch (variantCategory) {
            case SNP_A:
                return "A";
            case SNP_C:
                return "C";
            case SNP_G:
                return "G";
            case SNP_T:
                return "T";
            case MNP:
                return randUtil.randBases(ref.length(), ref);
            case INS:
                var len = randUtil.randInt(5) + 1;
                return randUtil.randAcgt() + randUtil.randBases(len);
            case DEL:
                return "";
            case MIXED:
                len = randUtil.randInt(10) + 2;
                while( len == ref.length() ) len = randUtil.randInt(10) + 2;
                return randUtil.randBases(len, ref);
            default:
                throw new RuntimeException("Unimplemented for variant category " + variantCategory);
        }
    }

    protected String randRef(RandomUtil randUtil, VariantCategory variantCategory, String ref) {
        if( ref != null ) return ref;
        switch (variantCategory) {
            case SNP_A:
                return randUtil.randBase("A");
            case SNP_C:
                return randUtil.randBase("C");
            case SNP_G:
                return randUtil.randBase("G");
            case SNP_T:
                return randUtil.randBase("T");
            case MNP:
                var len = randUtil.randInt(5) + 2;
                return randUtil.randBases(len);
            case INS:
                return "";
            case DEL:
                len = randUtil.randInt(5) + 1;
                return randUtil.randBases(len);
        case MIXED:
                len = randUtil.randInt(10) + 2;
                return randUtil.randBases(len);
        default:
                throw new RuntimeException("Unimplemented for variant category " + variantCategory);
        }

    }

    VariantTypeCounter variantTypeCounter(int size, int memSize) {
        Fields fields = new Fields();
        fields.add(new VcfHeaderInfo("field_bool", VcfInfoType.Flag, VcfInfoNumber.NUMBER.toString(), ""));
        fields.add(new VcfHeaderInfo("field_char", VcfInfoType.Character, VcfInfoNumber.NUMBER.toString(), ""));
        fields.add(new VcfHeaderInfo("field_int", VcfInfoType.Integer, VcfInfoNumber.NUMBER.toString(), ""));
        fields.add(new VcfHeaderInfo("field_float", VcfInfoType.Float, VcfInfoNumber.NUMBER.toString(), ""));
        fields.add(new VcfHeaderInfo("field_string", VcfInfoType.Float, VcfInfoNumber.NUMBER.toString(), ""));
        fields.add(new VcfHeaderInfo("field_string_2", VcfInfoType.Float, VcfInfoNumber.NUMBER.toString(), ""));

        for(String field: FIELDS_STRING) fields.add(new VcfHeaderInfo(field, VcfInfoType.String, VcfInfoNumber.NUMBER.toString(), ""));

        VariantTypeCounter variantTypeCounter = new VariantTypeCounter(fields);

        // Set size and memory size for each category
        for(VariantCategory category: VariantCategory.values()) {
            variantTypeCounter.getCountByCategory()[category.ordinal()] = size;
            for(String field: FIELDS_STRING) {
                variantTypeCounter.getSizesByField().get(field)[category.ordinal()] = memSize;
            }
        }

        return variantTypeCounter;
    }

    public void testDataFrame(VariantCategory variantCategory, int size, int stringMaxLen) {
        var varCounter = variantTypeCounter(size, size * stringMaxLen);
        var dataFrame = new DataFrameMnp(varCounter, variantCategory);

        // Create random data, 'size' rows
        int pos = 0, maxPos = 0;
        Set<Integer> positions = new HashSet<>();
        RandomUtil randUtil = new RandomUtil();
        randUtil.reset();   // Initialize random seed
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            maxPos = pos;
            positions.add(pos);
            // Create a dataframe row, and add the random data
            DataFrameRow dataFrameRow = randDataFrameRow(randUtil, dataFrame, pos, stringMaxLen, variantCategory);
            // Add the row to the data frame
            dataFrame.add(dataFrameRow);
        }
        System.out.println(dataFrame);

        // Check data
        var dataFrameExp = new DataFrameMnp(varCounter, variantCategory);
        randUtil.reset();   // Initialize random seed
        pos = 0;
        for(int i=0; i < size; i++) {
            pos += randUtil.randInt(1000); // Random position increment
            // Create a random row using the exact same random seed (i.e. the same data)
            DataFrameRow dataFrameRowExp = randDataFrameRow(randUtil, dataFrameExp, pos, stringMaxLen, variantCategory);
            // Query row from the data frame
            var dataFrameRow = dataFrame.getRow(pos, dataFrameRowExp.getRef(), dataFrameRowExp.getAlt());
            assertNotNull(dataFrameRow);
            assertEquals(dataFrameRowExp.getRef(), dataFrameRow.getRef());
            assertEquals(dataFrameRowExp.getAlt(), dataFrameRow.getAlt());

            // Check data
            for(String field: FIELDS) {
                assertEquals(dataFrameRowExp.get(field), dataFrameRow.getDataFrameValue(field), "Difference at Field: " + field //
                                + ", pos: " + pos //
                                + ", ref: " + dataFrameRowExp.getRef() //
                                + ", alt: " + dataFrameRowExp.getAlt() //
                                + ", expexted: " + dataFrameRowExp.get(field) //
                                + ", actual: " + dataFrameRow.getDataFrameValue(field)//
                );
            }
        }

        // Check positions that should not be found
        for(int i=0; i < size; i++) {
            var pos2 = randUtil.randInt(maxPos); // Random position
            if( !positions.contains(pos2)) {
                var refAlt = randUtil.randVariant(variantCategory);
                var ref = refAlt.first;
                var alt = refAlt.second;
                var dataFrameRow = dataFrame.getRow(pos2, ref, alt);
                Assertions.assertNull(dataFrameRow);
            }
        }
    }

}