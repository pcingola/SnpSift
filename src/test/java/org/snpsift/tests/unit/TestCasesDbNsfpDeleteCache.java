package org.snpsift.tests.unit;

/**
 * Test cases for dbNSFP database annotations
 * Note: This class deletes and re-builds 'data type cache' files
 *
 * @author pcingola
 */
public class TestCasesDbNsfpDeleteCache extends TestCasesDbNsfp {

    public TestCasesDbNsfpDeleteCache() {
        removeDataTypesCache = true;
    }

}
