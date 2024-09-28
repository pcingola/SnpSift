package org.snpsift.tests.unit;

/**
 * Annotate test case
 * Uses the index files created during the execution of TestCasesAnnotateCreateIndex test suite
 *
 * @author pcingola
 */
public class TestCasesAnnotateUseIndex extends TestCasesAnnotate {

	public TestCasesAnnotateUseIndex() {
		String[] memExtraArgs = { "-sorted" };
		defaultExtraArgs = memExtraArgs;

		// Use the index file creates during the execution of TestCasesAnnotateCreateIndex test suite
		deleteIndexFile = false;
	}

}
