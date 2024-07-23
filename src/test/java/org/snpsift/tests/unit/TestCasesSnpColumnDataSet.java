package org.snpsift.tests.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.dataFrame.DataFrameSnp;

import java.util.HashMap;
import java.util.Map;

public class TestCasesSnpColumnDataSet {
    private DataFrameSnp dataSet;

    @BeforeEach
    public void setup() {
        int numEntries = 10;
        String alt = "A";
        Map<String, VcfInfoType> fields2type = new HashMap<>();
        dataSet = new DataFrameSnp(numEntries, alt, fields2type);
    }

    @Test
    public void testGetAlt() {
        String expectedAlt = "A";
        String actualAlt = dataSet.getAlt();
        Assertions.assertEquals(expectedAlt, actualAlt);
    }

    @Test
    public void testSetData01() {
        String columnName = "column1";
        Object value = 42;
        int pos = 5;
        String ref = "N";
        String alt = "A";

        Assertions.assertThrows(RuntimeException.class, () -> {
            dataSet.setData(columnName, value, pos, ref, "C");
        });

        dataSet.setData(columnName, value, pos, ref, alt);
        Object actualValue = dataSet.getData(columnName, pos, ref, alt);
        Assertions.assertEquals(value, actualValue);
    }


    @Test
    public void testSetData02() {
        String columnName = "column1";
        Object value = "Value42";
        int pos = 5;
        String ref = "N";
        String alt = "A";

        Assertions.assertThrows(RuntimeException.class, () -> {
            dataSet.setData(columnName, value, pos, ref, "C");
        });

        dataSet.setData(columnName, value, pos, ref, alt);
        Object actualValue = dataSet.getData(columnName, pos, ref, alt);
        Assertions.assertEquals(value, actualValue);
    }

    @Test
    public void testSetData03() {
        String columnName = "column1";
        Object value = "Value42";
        int pos = 5;
        String ref = "N";
        String alt = "A";

        Assertions.assertThrows(RuntimeException.class, () -> {
            dataSet.setData(columnName, value, pos, ref, "C");
        });

        dataSet.setData(columnName, value, pos, ref, alt);
        Object actualValue = dataSet.getData(columnName, pos, ref, alt);
        Assertions.assertEquals(value, actualValue);
    }
}