package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.database.VariantDataFrame;
import org.snpsift.annotate.mem.database.VariantDatabase;

public class TestCasesVariantDatabase {

    VariantDatabase createDb01() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=1,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;

        // Create a database
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        VariantDatabase variantDatabase = new VariantDatabase(null, null, fieldNames);
        variantDatabase.create(vcfLines);
        return variantDatabase;
    }

    VariantDatabase createDb02() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=1,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t999\t.\tA\tAC\t.\t.\tFIELD_STRING=Value_INS_A;FIELD_INT=4;FIELD_FLOAT=4.4\n" //
            + "1\t999\t.\tAC\tA\t.\t.\tFIELD_STRING=Value_DEL_A;FIELD_INT=5;FIELD_FLOAT=5.5\n" //
            + "1\t1000\t.\tA\tC\t.\t.\tFIELD_STRING=Value_C;FIELD_INT=1;FIELD_FLOAT=1.1\n" //
            + "1\t1000\t.\tA\tG\t.\t.\tFIELD_STRING=Value_G;FIELD_INT=2;FIELD_FLOAT=2.2\n" //
            + "1\t1000\t.\tA\tT\t.\t.\tFIELD_STRING=Value_T;FIELD_INT=3;FIELD_FLOAT=3.3\n" //
            ;

        // Create a database
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        VariantDatabase variantDatabase = new VariantDatabase(null, null, fieldNames);
        variantDatabase.create(vcfLines);
        return variantDatabase;
    }

    VariantDatabase createDb03() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=A,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=A,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=A,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=A,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t1000\t.\tA\tC,G,T\t.\t.\tFIELD_STRING=Value_C,Value_G,Value_T;FIELD_INT=1,2,3;FIELD_FLOAT=1.1,2.2,3.3;FIELD_FLAG\n" //
            ;

        // Create a database
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        VariantDatabase variantDatabase = new VariantDatabase(null, null, fieldNames);
        variantDatabase.create(vcfLines);
        return variantDatabase;
    }

    @Test
    public void testCount01Snp() {
        var variantDatabase = createDb01();

        // Check the fields' types
        var fields = variantDatabase.getFields();
        assertEquals(VcfInfoType.String, fields.get("FIELD_STRING").getVcfInfoType());
        assertEquals(VcfInfoType.Integer, fields.get("FIELD_INT").getVcfInfoType());
        assertEquals(VcfInfoType.Float, fields.get("FIELD_FLOAT").getVcfInfoType());
        assertEquals(VcfInfoType.Flag, fields.get("FIELD_FLAG").getVcfInfoType());
        // Check that the dataframe was created
        VariantDataFrame df = variantDatabase.get("1");
        assertNotNull(df);
        // Check that the dataframe was added to the database
        var dfSnpT = df.getDataFrameByCategory(VariantCategory.SNP_T);
        assertNotNull(dfSnpT);
        // Check that we can retrieve the row
        DataFrameRow dfrow = dfSnpT.getRow(999, "A", "T"); // Position is 0-based
        assertNotNull(dfrow);
        // Check that the value is correct
        assertEquals("Value1", dfrow.getDataFrameValue("FIELD_STRING"));
    }

    @Test
    public void testCount02SnpsInDel() {
        VariantDatabase variantDatabase = createDb02();

        // Check the fields' types
        var fields = variantDatabase.getFields();
        assertEquals(VcfInfoType.String, fields.get("FIELD_STRING").getVcfInfoType());
        assertEquals(VcfInfoType.Integer, fields.get("FIELD_INT").getVcfInfoType());
        assertEquals(VcfInfoType.Float, fields.get("FIELD_FLOAT").getVcfInfoType());
        assertEquals(VcfInfoType.Flag, fields.get("FIELD_FLAG").getVcfInfoType());

        // Get VariantDataFrame
        VariantDataFrame vdf = variantDatabase.get("1");
        assertNotNull(vdf);

        // Check that the dataframe was added to the database
        String[] refs = { "A", "A", "A", "", "C" };
        String[] alts = { "C", "G", "T", "C", "" };
        String[] fieldString = { "Value_C", "Value_G", "Value_T", "Value_INS_A", "Value_DEL_A" };
        int[] fieldInt = { 1, 2, 3, 4, 5 };
        double[] fieldFloat = { 1.1, 2.2, 3.3, 4.4, 5.5 };
        Chromosome chr1 = new Chromosome(null, 0, 0, "1"); 
        for (int i = 0; i < refs.length; i++) {
            Variant variant = new Variant(chr1, 999, refs[i], alts[i]);
            VariantCategory variantCategory = VariantCategory.of(variant);
            var df = vdf.getDataFrameByCategory(variantCategory);
            assertNotNull(df);
            DataFrameRow dfrow = df.getRow(999, refs[i], alts[i]);
            assertNotNull(dfrow);
            assertEquals(fieldString[i], dfrow.getDataFrameValue("FIELD_STRING"));
            assertEquals(fieldInt[i], dfrow.getDataFrameValue("FIELD_INT"));
            assertEquals(fieldFloat[i], dfrow.getDataFrameValue("FIELD_FLOAT"));
        }
    }

    @Test
    public void testCount03NumberA() {
        var variantDatabase = createDb03();

        // Check the fields' types
        var fields = variantDatabase.getFields();
        assertEquals(VcfInfoType.String, fields.get("FIELD_STRING").getVcfInfoType());
        assertEquals(VcfInfoType.Integer, fields.get("FIELD_INT").getVcfInfoType());
        assertEquals(VcfInfoType.Float, fields.get("FIELD_FLOAT").getVcfInfoType());
        assertEquals(VcfInfoType.Flag, fields.get("FIELD_FLAG").getVcfInfoType());

        // Get VariantDataFrame
        VariantDataFrame vdf = variantDatabase.get("1");
        assertNotNull(vdf);

        // Check that the dataframe was added to the database
        String[] refs = { "A", "A", "A", };
        String[] alts = { "C", "G", "T", };
        String[] fieldString = { "Value_C", "Value_G", "Value_T" };
        int[] fieldInt = { 1, 2, 3 };
        double[] fieldFloat = { 1.1, 2.2, 3.3 };
        boolean[] fieldFlag = { true, true, true };
        Chromosome chr1 = new Chromosome(null, 0, 0, "1"); 
        for (int i = 0; i < refs.length; i++) {
            Variant variant = new Variant(chr1, 999, refs[i], alts[i]);
            VariantCategory variantCategory = VariantCategory.of(variant);
            var df = vdf.getDataFrameByCategory(variantCategory);
            assertNotNull(df);
            DataFrameRow dfrow = df.getRow(999, refs[i], alts[i]);
            assertNotNull(dfrow);
            assertEquals(fieldString[i], dfrow.getDataFrameValue("FIELD_STRING"));
            assertEquals(fieldInt[i], dfrow.getDataFrameValue("FIELD_INT"));
            assertEquals(fieldFloat[i], dfrow.getDataFrameValue("FIELD_FLOAT"));
            assertEquals(fieldFlag[i], dfrow.getDataFrameValue("FIELD_FLAG"));
        }
    }

    @Test
    public void testCount04CreateAndCheckFields() {
        var variantDatabase = createDb01();
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        variantDatabase.checkFields(fieldNames, true);
   }

   @Test
   public void testCount05CreateAndAnnotate() {
        var variantDatabase = createDb01();

        // Set the fields we want to annotate: null means "all fields"
        variantDatabase.setFieldNamesAnnotate(null);
        
        // Annotate a VCF line
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(vcfLines).next();
        variantDatabase.annotate(vcfEntry);
        // Check values
        assertEquals("Value1", vcfEntry.getInfo("FIELD_STRING"));
        assertEquals("3.14", vcfEntry.getInfo("FIELD_FLOAT"));
        assertEquals(3.14, vcfEntry.getInfoFloat("FIELD_FLOAT"));
        assertEquals(true, vcfEntry.getInfoFlag("FIELD_FLAG"));
        assertEquals("123", vcfEntry.getInfo("FIELD_INT"));
        assertEquals(123, vcfEntry.getInfoInt("FIELD_INT"));
  }

   @Test
   public void testCount06CreateAndAnnotate() {
        var variantDatabase = createDb01();

        // Set the fields we want to annotate
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT"};
        variantDatabase.setFieldNamesAnnotate(fieldNames);
        
        // Annotate a VCF line
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(vcfLines).next();
        variantDatabase.annotate(vcfEntry);
        // Check values
        assertEquals("Value1", vcfEntry.getInfo("FIELD_STRING"));
        assertEquals("123", vcfEntry.getInfo("FIELD_INT"));
        assertEquals(123, vcfEntry.getInfoInt("FIELD_INT"));
        // These should NOT be annotated
        assertEquals(null, vcfEntry.getInfo("FIELD_FLOAT"));
        assertEquals(null, vcfEntry.getInfo("FIELD_FLAG"));
        assertEquals(false, vcfEntry.getInfoFlag("FIELD_FLAG"));
  }

   @Test
   public void testCount05CreateAndAnnotatePrefix() {
        var variantDatabase = createDb01();

        // Set a prefix for all the fields to annotate
        variantDatabase.setPrefix("ZZZ_");
        
        // Annotate a VCF line
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(vcfLines).next();
        variantDatabase.annotate(vcfEntry);
        System.out.println(vcfEntry);
        // Check values
        assertEquals("Value1", vcfEntry.getInfo("ZZZ_FIELD_STRING"));
        assertEquals("3.14", vcfEntry.getInfo("ZZZ_FIELD_FLOAT"));
        assertEquals(3.14, vcfEntry.getInfoFloat("ZZZ_FIELD_FLOAT"));
        assertEquals(true, vcfEntry.getInfoFlag("ZZZ_FIELD_FLAG"));
        assertEquals("123", vcfEntry.getInfo("ZZZ_FIELD_INT"));
        assertEquals(123, vcfEntry.getInfoInt("ZZZ_FIELD_INT"));
    }

  @Test
  public void testCount06CreateAndAnnotateCheckHeader() {
      // Only annotate with some fields using a prefix, check that the header is added

      throw new RuntimeException("Not implemented");
 }

}