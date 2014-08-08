package ca.mcgill.mcb.pcingola.snpSift.testCases;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.JUnitCore;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */
public class TestSuiteAll {

	public static void main(String args[]) {
		// junit.textui.TestRunner.run(suite());
		JUnitCore.main("ca.mcgill.mcb.pcingola.snpSift.testCases.TestSuiteAll");
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
		suite.addTestSuite(TestCasesGeneSets.class);
		suite.addTestSuite(TestCasesConcordance.class);
		suite.addTestSuite(TestCasesFilterChrPos.class);

		return suite;
	}
}
