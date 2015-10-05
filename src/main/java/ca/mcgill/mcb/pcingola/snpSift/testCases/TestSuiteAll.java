package ca.mcgill.mcb.pcingola.snpSift.testCases;

import org.junit.runner.JUnitCore;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */
public class TestSuiteAll {

	public static void main(String args[]) {
		JUnitCore.main("ca.mcgill.mcb.pcingola.snpSift.testCases.TestSuiteAll");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TestCasesFilter.class);
		suite.addTestSuite(TestCasesFilterALL.class);
		suite.addTestSuite(TestCasesFilterGt.class);
		suite.addTestSuite(TestCasesHwe.class);
		suite.addTestSuite(TestCasesLd.class);
		suite.addTestSuite(TestCasesCaseControl.class);
		suite.addTestSuite(TestCasesIntervalFile.class);
		suite.addTestSuite(TestCasesAnnotate.class);
		suite.addTestSuite(TestCasesAnnotateMem.class);
		suite.addTestSuite(TestCasesAnnotateTabix.class);
		suite.addTestSuite(TestCasesIndex.class);
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
