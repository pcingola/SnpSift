package ca.mcgill.mcb.pcingola.snpSift.testCases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */

@RunWith(Suite.class)
@SuiteClasses({ //
		TestCasesFilter.class, //
		TestCasesFilterALL.class, //
		TestCasesFilterGt.class, //
		TestCasesHwe.class, //
		TestCasesLd.class, //
		TestCasesCaseControl.class, //
		TestCasesIntervalFile.class, //
		TestCasesAnnotateCreateIndex.class, // Execute these test cases first to delete and create index files
		TestCasesAnnotateUseIndex.class, // These test cases use the indexes created in 'TestCasesAnnotateCreateIndex'
		TestCasesAnnotateMem.class, //
		TestCasesAnnotateTabix.class, //
		TestCasesIndex.class, //
		TestCasesVarType.class, //
		TestCasesGwasCatalog.class, //
		TestCasesDbNsfp.class, //
		TestCasesIntervals.class, //
		TestCasesExtractFields.class, //
		TestCasesSplit.class, //
		TestCasesPrivate.class, //
		TestCasesGt.class, //
		TestCasesGeneSets.class, //
		TestCasesConcordance.class, //
		TestCasesFilterChrPos.class, //
})
public class TestSuiteAll {
}
