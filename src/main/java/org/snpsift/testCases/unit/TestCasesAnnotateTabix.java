package org.snpsift.testCases.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;

/**
 * Annotate test case using TABIX indexed files
 *
 * @author pcingola
 */
public class TestCasesAnnotateTabix extends TestCasesAnnotate {

    public TestCasesAnnotateTabix() {
        String[] memExtraArgs = {"-tabix"};
        defaultExtraArgs = memExtraArgs;
    }
}
