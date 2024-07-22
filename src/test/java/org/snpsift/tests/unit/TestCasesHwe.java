package org.snpsift.tests.unit;

import org.junit.jupiter.api.Test;
import org.snpeff.util.Log;
import org.snpsift.hwe.VcfHwe;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HWE test cases
 *
 * @author pcingola
 */
public class TestCasesHwe {

    public static boolean verbose = false;

    /**
     * Calculate p-value using ChiSquare approximation
     */
    @Test
    public void test_01() {
        VcfHwe vcfHwe = new VcfHwe();

        // See "Principles of population genetics" Hartl & Clark, page 55
        double pchi2 = vcfHwe.hwePchi2(452, 184, 120);
        assertEquals(0.14201030881111387, pchi2, 0.0001);
    }

    /**
     * Calculate p-value using exact formula
     */
    @Test
    public void test_02() {
        VcfHwe vcfHwe = new VcfHwe();

        // See "Principles of population genetics" Hartl & Clark, page 59
        double p = vcfHwe.hwePn12(452, 184, 120);
        assertEquals(0.03532606464457993, p, 0.0001);
    }

    /**
     * Calculate "P[ n12 | n1, n2]" according to Table 1 in the paper "A Note on Exact test of Hardy Weinberg Equilibrium", G. Abecasis et. al.
     */
    @Test
    public void test_02_table_1() {
        if (verbose)
            Log.info(" Calculate P[ n12 | n1, n2] according to Table 1 in the paper 'A Note on Exact test of Hardy Weinberg Equilibrium', G. Abecasis et. al.");

        VcfHwe vcfHwe = new VcfHwe();

        int N = 100; // Individuals
        int na = 21; // Rare allele
        int nb = 2 * N - na; // Common allele

        int[] nabs = {5, 7, 9, 11, 13, 15, 17, 19, 21};
        double[] ps = {2.1173148965188926E-8, 1.4034773028353937E-6, 4.693851868371584E-5, 8.704961646798517E-4, 0.009374574081167143, 0.05928302085614469, 0.21446504603840832, 0.40635482407275675, 0.3096036754840131};

        for (int i = 0; i < nabs.length; i++) {
            double p = vcfHwe.hwePn12(na, nb, nabs[i]);
            if (verbose) Log.info("\tnab: " + nabs[i] + "\t\tP[ n12 | n1, n2] : " + p);
            assertEquals(ps[i], p, 0.0000000001);
        }
    }

    /**
     * Calculate "P[ n12 | n1, n2]" according to Page 59 in the "Principles of population genetics"
     */
    @Test
    public void test_03_page_59() {
        if (verbose)
            Log.info("Calculate P[ n12 | n1, n2] according to Page 59 in the 'Principles of population genetics'");

        VcfHwe vcfHwe = new VcfHwe();

        int N = 8; // Individuals
        int na = 8; // Rare allele
        int nb = 2 * N - na; // Common allele

        int[] nabs = {0, 2, 4, 6, 8};
        double[] ps = {0.005439005439005438, 0.1740481740481739, 0.5221445221445218, 0.2784770784770781, 0.01989121989121986};

        for (int i = 0; i < nabs.length; i++) {
            double p = vcfHwe.hwePn12(na, nb, nabs[i]);
            if (verbose) Log.info("\tnab: " + nabs[i] + "\t\tP[ n12 | n1, n2] : " + p);
            assertEquals(ps[i], p, 0.0000000001);
        }
    }

    /**
     * Calculate "P_{HWE}" using Chi-square approximation.
     * Compares results to Table 1 in the paper "A Note on Exact test of Hardy Weinberg Equilibrium", G. Abecasis et. al.
     */
    @Test
    public void test_04_pChi2_table_1() {
        if (verbose) Log.info("Calculate P_{HWE} using Chi-square approximation");

        VcfHwe vcfHwe = new VcfHwe();

        int N = 100; // Individuals
        int na = 21; // Rare allele
        int nb = 2 * N - na; // Common allele

        int[] nabs = {5, 7, 9, 11, 13, 15, 17, 19, 21};
        double[] ps = {2.1405099914773018E-13, 3.4827685180260914E-10, 1.873274999564245E-7, 3.3630218789326705E-5, 0.002047414882462517, 0.04347121256973019, 0.33955684500144223, 0.9131457308575013, 0.2407218835565368,};

        for (int i = 0; i < nabs.length; i++) {
            double p = vcfHwe.hwePchi2(na, nb, nabs[i]);
            if (verbose) Log.info("\tnab: " + nabs[i] + "\t\tP_{HWE} : " + p);
            assertEquals(ps[i], p, 0.0000000001);
        }
    }

    /**
     * Calculate "P_{HWE}" according to Table 1 in the paper "A Note on Exact test of Hardy Weinberg Equilibrium", G. Abecasis et. al.
     * <p>
     * WARNING: The formula P_{HWE} in page 2 of the paper, seems to have two mistakes (probably typos?)
     */
    @Test
    public void test_04_pHwe_table_1() {
        if (verbose)
            Log.info("Calculate P_{HWE} according to a corrected formula from 'A Note on Exact test of Hardy Weinberg Equilibrium'");

        VcfHwe vcfHwe = new VcfHwe();

        int N = 100; // Individuals
        int na = 21; // Rare allele
        int nb = 2 * N - na; // Common allele

        int[] nabs = {5, 7, 9, 11, 13, 15, 17, 19, 21};
        double[] ps = {2.130704311953056E-8, 1.4247843459549242E-6, 4.8363303029670764E-5, 9.188594677095224E-4, 0.010293433548876666, 0.06957645440502136, 0.2840415004434297, 1.0, 0.5936451759274428};

        for (int i = 0; i < nabs.length; i++) {
            double p = vcfHwe.hweP(na, nb, nabs[i]);
            if (verbose) Log.info("\tnab: " + nabs[i] + "\t\tP_{HWE} : " + p);
            assertEquals(ps[i], p, 0.0000000001);
        }
    }
}
