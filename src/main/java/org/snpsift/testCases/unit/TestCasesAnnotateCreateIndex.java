package org.snpsift.testCases.unit;

/**
 * Annotate test case
 * Deletes index files to make sure they are properly built
 *
 * @author pcingola
 */
public class TestCasesAnnotateCreateIndex extends TestCasesAnnotate {

	public TestCasesAnnotateCreateIndex() {
		String[] memExtraArgs = { "-sorted" };
		defaultExtraArgs = memExtraArgs;
		deleteIndexFile = true; // Make sure that index files ("*.sidx") are deleted.
	}

}
