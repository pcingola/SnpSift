package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */
public class TestSuiteAll {

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TestCasesFilter.class);
		suite.addTestSuite(TestCasesFilterALL.class);
		suite.addTestSuite(TestCasesHwe.class);
		suite.addTestSuite(TestCasesLd.class);
		suite.addTestSuite(TestCasesCaseControl.class);
		suite.addTestSuite(TestCasesAnnotate.class);
		suite.addTestSuite(TestCasesAnnotateMem.class);
		suite.addTestSuite(TestCasesAnnotateTabix.class);
		suite.addTestSuite(TestCasesVarType.class);
		suite.addTestSuite(TestCasesGwasCatalog.class);
		suite.addTestSuite(TestCasesDbNsfp.class);
		suite.addTestSuite(TestCasesIntervals.class);
		suite.addTestSuite(TestCasesExtractFields.class);
		suite.addTestSuite(TestCasesSplit.class);
		suite.addTestSuite(TestCasesPrivate.class);
		suite.addTestSuite(TestCasesGt.class);
		suite.addTestSuite(TestCasesEpistasis.class);
		suite.addTestSuite(TestCasesGeneSets.class);

		return suite;
	}
}
