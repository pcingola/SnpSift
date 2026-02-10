package org.snpsift.tests.unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.mem.VariantCategory;
import org.snpsift.annotate.mem.dataFrame.DataFrameRow;
import org.snpsift.annotate.mem.database.VariantDataFrame;
import org.snpsift.annotate.mem.database.VariantDatabase;

public class TestCasesVariantDatabase {

    /**
     * Compare two databases for a chromosome
     */
    void compareDataBases(VariantDatabase dbOri, VariantDatabase dbNew, String chr) {
        // Load dataframe for chromosome 1
        var df1Ori = dbOri.get(chr);
        var df1New = dbNew.get(chr);

        // Compare dataframes in all variant categories
        for(VariantCategory vc : VariantCategory.values()) {
            var dfcOri = df1Ori.getDataFrameByCategory(vc);
            var dfcNew = df1New.getDataFrameByCategory(vc);
            for(VcfHeaderInfo field: dbOri.getFields()) {
                var fieldName = field.getId();
                var colOri = dfcOri.getColumn(fieldName);
                var colNew = dfcNew.getColumn(fieldName);
                // Compare size
                assertEquals(colOri.size(), colNew.size(), "Size of columns should be the same for category " + vc + ", column " + fieldName + ", expected: " + colOri.size() + ", found: " + colNew.size());
                // Compare values in each entry
                for(int i=0 ; i < colOri.size(); i++) {
                    assertEquals(colOri.get(i), colNew.get(i), "Values should be the same for category " + vc + ", column " + fieldName + ", entry " + i + ", expected: " + colOri.get(i) + ", found: " + colNew.get(i));
                }
            }
        }
    }

    VariantDatabase createDb01() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "##INFO=<ID=FIELD_STRING,Number=1,Type=String,Description=\"Test INFO field string\">\n" //
            + "##INFO=<ID=FIELD_INT,Number=1,Type=Integer,Description=\"Test INFO field int\">\n" //
            + "##INFO=<ID=FIELD_FLOAT,Number=1,Type=Float,Description=\"Test INFO field float\">\n" //
            + "##INFO=<ID=FIELD_FLAG,Number=1,Type=Flag,Description=\"Test INFO field flag\">\n" //
            + "1\t1000\tID_1234567\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;

        // Create a database
        String[] fieldNames = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG", "ID" };
        String dbDir = System.getProperty("java.io.tmpdir") + "snpsift.TestCasesVariantDatabase.createDb01";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
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
        String dbDir = System.getProperty("java.io.tmpdir") + "snpsift.TestCasesVariantDatabase.createDb02";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
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
            + "1\t1000\tID_1234567.\tA\tC,G,T\t.\t.\tFIELD_STRING=Value_C,Value_G,Value_T;FIELD_INT=1,2,3;FIELD_FLOAT=1.1,2.2,3.3;FIELD_FLAG\n" //
            ;

        // Create a database
        String[] fieldNames = { "ID", "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.createDb03";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        return variantDatabase;
    }

    VariantDatabase createDb04() {
        // Create some VCF lines, create a string buffer reader and a VcfFileIterator
        var vcfLines = "" //
            + "1\t1000\tID_1234567\tA\tT\t.\t.\tFIELD_STRING=Value1;FIELD_INT=123;FIELD_FLOAT=3.14\n" //
            ;

        // Create a database
        String[] fieldNames = { "ID" };
        String dbDir = System.getProperty("java.io.tmpdir") + "snpsift.TestCasesVariantDatabase.createDb04";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
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
   public void testCount07CreateAndAnnotatePrefix() {
        var variantDatabase = createDb01();

        // Set a prefix for all the fields to annotate
        variantDatabase.setPrefix("ZZZ_");
        
        // Annotate a VCF line
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(vcfLines).next();
        variantDatabase.annotate(vcfEntry);
        // Check values
        assertEquals("Value1", vcfEntry.getInfo("ZZZ_FIELD_STRING"));
        assertEquals("3.14", vcfEntry.getInfo("ZZZ_FIELD_FLOAT"));
        assertEquals(3.14, vcfEntry.getInfoFloat("ZZZ_FIELD_FLOAT"));
        assertEquals(true, vcfEntry.getInfoFlag("ZZZ_FIELD_FLAG"));
        assertEquals("123", vcfEntry.getInfo("ZZZ_FIELD_INT"));
        assertEquals(123, vcfEntry.getInfoInt("ZZZ_FIELD_INT"));
    }

    @Test
    public void testCount08CreateAndAnnotateCheckHeader() {
        var variantDatabase = createDb01();
        // Check headers
        var headers = variantDatabase.vcfHeaders();
        assertEquals(5, headers.size());
        // Convert headers to a Set<String>
        var headerSet = headers.stream().map(h -> h.getId()).collect(Collectors.toSet());
        assert(headerSet.contains("FIELD_STRING"));
        assert(headerSet.contains("FIELD_INT"));
        assert(headerSet.contains("FIELD_FLOAT"));
        assert(headerSet.contains("FIELD_FLAG"));
    }

    @Test
    public void testCount09CreateAndAnnotateCheckHeaderPrefix() {
          var variantDatabase = createDb01();
          variantDatabase.setPrefix("ZZZ_");
          // Check headers
          var headers = variantDatabase.vcfHeaders();
          assertEquals(5, headers.size());
          // Convert headers to a Set<String>
          var headerSet = headers.stream().map(h -> h.getId()).collect(Collectors.toSet());
          assert(headerSet.contains("ZZZ_FIELD_STRING"));
          assert(headerSet.contains("ZZZ_FIELD_INT"));
          assert(headerSet.contains("ZZZ_FIELD_FLOAT"));
          assert(headerSet.contains("ZZZ_FIELD_FLAG"));
    }

    @Test
    public void testCount10CreateAndAnnotate() {
        var variantDatabase = createDb04();

        // Set the fields we want to annotate
        String[] fieldNames = { "ID" };
        variantDatabase.setFieldNamesAnnotate(fieldNames);
        
        // Annotate a VCF line
        var vcfLines = "1\t1000\t.\tA\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(vcfLines).next();
        variantDatabase.annotate(vcfEntry);
        // Check values
        assertEquals("ID_1234567", vcfEntry.getInfo("ID"));
    }

    /**
     * Bug: SNP with Number=R field stores only the ALT value instead of REF,ALT.
     * For T->A with CAF=0.01,1.0001 (Number=R), annotation should give "CAF=0.01,1.0001" not "CAF=1.0001".
     */
    @Test
    public void testCount12SnpNumberR() {
        // Database with Number=R field (one value per REF + each ALT)
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=R,Type=String,Description=\"Allele frequencies, REF then ALTs\">\n" //
            + "chr13\t21172461\trs151272242\tT\tA\t.\t.\tRS=151272242;CAF=0.01,1.0001\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_12";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        // Annotate a VCF entry for the same SNP
        var inputVcf = "chr13\t21172461\t.\tT\tA\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        // Number=R: both REF and ALT values must be present
        assertEquals("0.01,1.0001", vcfEntry.getInfo("CAF"), "Number=R field should contain both REF and ALT values");
        assertEquals("151272242", vcfEntry.getInfo("RS"));
    }

    /**
     * Bug: Same as testCount12 but with Type=Float instead of Type=String.
     * Number=R with numeric type should still store the full comma-separated value as a String.
     */
    @Test
    public void testCount13SnpNumberRFloat() {
        var vcfLines = "" //
            + "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tT\tA\t.\t.\tCAF=0.01,1.0001\n" //
            ;

        String[] fieldNames = { "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_13";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tT\tA\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("0.01,1.0001", vcfEntry.getInfo("CAF"), "Number=R Float field should store full comma-separated value");
    }

    /**
     * Bug: DEL (TG->T) with Number=A CAF field: annotation output has no CAF at all.
     * The database has CAF=0.861 (Number=A) for the DEL variant, yet annotation produces no CAF.
     */
    @Test
    public void testCount14DelNumberA() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tTG\tT\t.\t.\tRS=151272242;CAF=0.861\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_14";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        // Annotate a VCF entry for the same DEL
        var inputVcf = "chr13\t21172461\t.\tTG\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for DEL");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for DEL with Number=A");
        assertEquals("0.861", vcfEntry.getInfo("CAF"), "Number=A CAF value wrong for DEL");
    }

    /**
     * Bug: DEL (TG->T) with Number=R CAF field: annotation output has no CAF at all.
     * The database has CAF=0.861,0.139 (Number=R) for the DEL variant.
     */
    @Test
    public void testCount15DelNumberR() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tTG\tT\t.\t.\tRS=151272242;CAF=0.861,0.139\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_15";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        // Annotate a VCF entry for the same DEL
        var inputVcf = "chr13\t21172461\t.\tTG\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for DEL");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for DEL with Number=R");
        assertEquals("0.861,0.139", vcfEntry.getInfo("CAF"), "Number=R CAF value wrong for DEL");
    }

    /**
     * INS (T->TA) with Number=A field: verify CAF is annotated correctly.
     * INS normalization strips common prefix, giving ref="" alt="A".
     */
    @Test
    public void testCount16InsNumberA() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tT\tTA\t.\t.\tRS=151272242;CAF=0.123\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_16";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tT\tTA\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for INS");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for INS with Number=A");
        assertEquals("0.123", vcfEntry.getInfo("CAF"), "Number=A CAF value wrong for INS");
    }

    /**
     * INS (T->TA) with Number=R field: verify both REF and ALT values are annotated.
     */
    @Test
    public void testCount17InsNumberR() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tT\tTA\t.\t.\tRS=151272242;CAF=0.877,0.123\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_17";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tT\tTA\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for INS");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for INS with Number=R");
        assertEquals("0.877,0.123", vcfEntry.getInfo("CAF"), "Number=R CAF value wrong for INS");
    }

    /**
     * MNP (AC->TG) with Number=A field.
     */
    @Test
    public void testCount18MnpNumberA() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tAC\tTG\t.\t.\tRS=151272242;CAF=0.456\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_18";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tAC\tTG\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for MNP");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for MNP with Number=A");
        assertEquals("0.456", vcfEntry.getInfo("CAF"), "Number=A CAF value wrong for MNP");
    }

    /**
     * MNP (AC->TG) with Number=R field.
     */
    @Test
    public void testCount19MnpNumberR() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tAC\tTG\t.\t.\tRS=151272242;CAF=0.544,0.456\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_19";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tAC\tTG\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for MNP");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for MNP with Number=R");
        assertEquals("0.544,0.456", vcfEntry.getInfo("CAF"), "Number=R CAF value wrong for MNP");
    }

    /**
     * MIXED (AC->T) with Number=A field.
     * MIXED: ref and alt have different lengths, and neither starts with the other.
     */
    @Test
    public void testCount20MixedNumberA() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tAC\tT\t.\t.\tRS=151272242;CAF=0.321\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_20";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tAC\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for MIXED");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for MIXED with Number=A");
        assertEquals("0.321", vcfEntry.getInfo("CAF"), "Number=A CAF value wrong for MIXED");
    }

    /**
     * MIXED (AC->T) with Number=R field.
     */
    @Test
    public void testCount21MixedNumberR() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs151272242\tAC\tT\t.\t.\tRS=151272242;CAF=0.679,0.321\n" //
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_21";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        var inputVcf = "chr13\t21172461\t.\tAC\tT\t.\t.\t.\n";
        var vcfEntry = VcfFileIterator.fromString(inputVcf).next();
        variantDatabase.annotate(vcfEntry);

        assertEquals("151272242", vcfEntry.getInfo("RS"), "RS annotation missing for MIXED");
        assertNotNull(vcfEntry.getInfo("CAF"), "CAF annotation missing for MIXED with Number=R");
        assertEquals("0.679,0.321", vcfEntry.getInfo("CAF"), "Number=R CAF value wrong for MIXED");
    }

    /**
     * Database has a Number=A field for one variant but not another at the same position.
     * Verify that the variant without a value does not get a spurious annotation.
     */
    @Test
    public void testCount22NumberAMissingValue() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs111\tT\tA\t.\t.\tRS=111;CAF=0.5\n" //
            + "chr13\t21172461\trs222\tT\tG\t.\t.\tRS=222\n" // No CAF for this variant
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_22";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        // Variant with CAF
        var inputVcf1 = "chr13\t21172461\t.\tT\tA\t.\t.\t.\n";
        var vcfEntry1 = VcfFileIterator.fromString(inputVcf1).next();
        variantDatabase.annotate(vcfEntry1);
        assertEquals("111", vcfEntry1.getInfo("RS"));
        assertEquals("0.5", vcfEntry1.getInfo("CAF"), "Number=A CAF should be present for T->A");

        // Variant without CAF
        var inputVcf2 = "chr13\t21172461\t.\tT\tG\t.\t.\t.\n";
        var vcfEntry2 = VcfFileIterator.fromString(inputVcf2).next();
        variantDatabase.annotate(vcfEntry2);
        assertEquals("222", vcfEntry2.getInfo("RS"));
        assertEquals(null, vcfEntry2.getInfo("CAF"), "Number=A CAF should be null for T->G (not in database)");
    }

    /**
     * Multiple variant types at the same position: SNP + DEL + INS, all with Number=A.
     * Verify each is annotated independently with the correct value.
     */
    @Test
    public void testCount23MultipleVariantTypesNumberA() {
        var vcfLines = "" //
            + "##INFO=<ID=RS,Number=1,Type=Integer,Description=\"dbSNP ID\">\n" //
            + "##INFO=<ID=CAF,Number=A,Type=Float,Description=\"Allele frequencies\">\n" //
            + "chr13\t21172461\trs111\tT\tA\t.\t.\tRS=111;CAF=0.1\n" //   SNP
            + "chr13\t21172461\trs222\tTG\tT\t.\t.\tRS=222;CAF=0.2\n" //  DEL
            + "chr13\t21172461\trs333\tT\tTA\t.\t.\tRS=333;CAF=0.3\n" //  INS
            ;

        String[] fieldNames = { "RS", "CAF" };
        String dbDir = System.getProperty("java.io.tmpdir") + "/snpsift.TestCasesVariantDatabase.test_23";
        VariantDatabase variantDatabase = new VariantDatabase(null, dbDir, fieldNames);
        variantDatabase.create(vcfLines);
        variantDatabase.setFieldNamesAnnotate(fieldNames);

        // SNP
        var vcfEntry = VcfFileIterator.fromString("chr13\t21172461\t.\tT\tA\t.\t.\t.\n").next();
        variantDatabase.annotate(vcfEntry);
        assertEquals("111", vcfEntry.getInfo("RS"));
        assertEquals("0.1", vcfEntry.getInfo("CAF"));

        // DEL
        vcfEntry = VcfFileIterator.fromString("chr13\t21172461\t.\tTG\tT\t.\t.\t.\n").next();
        variantDatabase.annotate(vcfEntry);
        assertEquals("222", vcfEntry.getInfo("RS"));
        assertEquals("0.2", vcfEntry.getInfo("CAF"));

        // INS
        vcfEntry = VcfFileIterator.fromString("chr13\t21172461\t.\tT\tTA\t.\t.\t.\n").next();
        variantDatabase.annotate(vcfEntry);
        assertEquals("333", vcfEntry.getInfo("RS"));
        assertEquals("0.3", vcfEntry.getInfo("CAF"));
    }

    @Test
    public void testCount11SaveAndLoad() {
        // Create and save database
        var variantDatabase = createDb01();
        variantDatabase.saveFields();
        variantDatabase.saveCurrentDataFrame();

        // Load database
        var dbDir = variantDatabase.getDbDir();
        String[] fields = { "FIELD_STRING", "FIELD_INT", "FIELD_FLOAT", "FIELD_FLAG", "ID" };
        var varDb = new VariantDatabase("", dbDir, fields, null, false);
        varDb.load();

        // Compare databases for chromosome 1
        compareDataBases(variantDatabase, varDb, "1");

        // Clean up (delete temporary directory 'dbDir')
        new File(dbDir).delete();
   }
}