package org.snpsift.tests.unit;

/**
 * Annotate test case
 *
 * @author pcingola
 */
public class TestCasesAnnotateMem extends TestCasesAnnotate {

	public TestCasesAnnotateMem() {
		String[] memExtraArgs = { "-mem" };
		defaultExtraArgs = memExtraArgs;
	}

}
