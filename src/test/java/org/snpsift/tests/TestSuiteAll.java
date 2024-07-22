package org.snpsift.tests;


import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */

@Suite
@SuiteDisplayName("Unit test cases")
@SelectPackages({"org.snpsift.testCases.unit"})
public class TestSuiteAll {
}
