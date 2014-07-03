package ca.mcgill.mcb.pcingola.snpSift.testCases;

/**
 * Annotate test case using TABIX indexed files
 *
 * @author pcingola
 */
public class TestCasesAnnotateTabix extends TestCasesAnnotate {

	public TestCasesAnnotateTabix() {
		String[] memExtraArgs = { "-tabix" };
		defaultExtraArgs = memExtraArgs;
	}

}
