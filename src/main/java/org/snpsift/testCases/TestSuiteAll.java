package org.snpsift.testCases;

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
		TestCasesAnnotateCreateIndex.class, // Execute these test cases first to delete and create index files
		TestCasesAnnotateMem.class, //
		TestCasesAnnotateTabix.class, //
		TestCasesAnnotateUseIndex.class, // These test cases use the indexes created in 'TestCasesAnnotateCreateIndex'
		TestCasesCaseControl.class, //
		TestCasesConcordance.class, //
		TestCasesDbNsfp.class, // Test that use 'data type cache' files build in previous test
		TestCasesDbNsfpDeleteCache.class, // Test that delete 'data type cache' files 
		TestCasesExtractFields.class, //
		TestCasesFilter.class, //
		TestCasesFilterALL.class, //
		TestCasesFilterChrPos.class, //
		TestCasesFilterGt.class, //
		TestCasesGeneSets.class, //
		TestCasesGwasCatalog.class, //
		TestCasesGt.class, //
		TestCasesHwe.class, //
		TestCasesIndex.class, //
		TestCasesIntervals.class, //
		TestCasesLd.class, //
		TestCasesPrivate.class, //
		TestCasesSort.class, //
		TestCasesSplit.class, //
		TestCasesVarType.class, //
		TestCasesVcfCheck.class, //
})
public class TestSuiteAll {
}
