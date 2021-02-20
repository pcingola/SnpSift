package org.snpsift.hwe;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;

/**
 * Calculate Hardy-Weinberg equilibrium and goodness of fit.
 *
 * References:
 * 			"Principles of population genetics", Hartl & Clark
 * 			"A Note on Exact test of Hardy Weinberg Equilibrium", G. Abecasis et. al.
 *
 * Note: This is only for two alleles in diploid individuals. We should
 *       extend this to more alleles and N-ploid species.
 *
 * @author pablocingolani
 */
public class VcfHwe {

	public static boolean debug = false;
	static final double LOG2 = Math.log(2.0);

	public VcfHwe() {
	}

	/**
	 * Calculate Hardy-Weinberg equilibrium and add data to vcfEntry (INFO fields)
	 * @param addInfo : If true, 'HWE' and 'HWEP' INFO tags are added to this entry
	 */
	public double hwe(VcfEntry vcfEntry, boolean addInfo) {
		// Only for two alleles
		if (vcfEntry.getAlts().length != 1) return Double.NaN;

		// Create allele counters
		int countSamples = vcfEntry.getAlts().length + 1;
		int countByGen[] = new int[countSamples];
		int countAA = 0, countAB = 0, countBB = 0;

		// Count each allele
		int tot = 0;
		byte gt[] = vcfEntry.getGenotypesScores();
		for (int i = 0; i < gt.length; i++) {
			switch (gt[i]) {
			case 0: // Hom-Ref
				countAA++;
				countByGen[0] += 2;
				break;

			case 1: // Het
				countAB++;
				countByGen[0]++;
				countByGen[1]++;
				break;

			case 2: // Hom-Alt
				countBB++;
				countByGen[1] += 2;
				break;

			default: // Missing genotype (-1)
				break;
			}
		}

		// Sanity check
		if (countByGen[0] != (countAA * 2 + countAB)) throw new RuntimeException("This should never happen!");
		if (countByGen[1] != (countBB * 2 + countAB)) throw new RuntimeException("This should never happen!");

		// Calculate p and q
		double p = 0, q = 0;
		if (tot > 0) {
			p = ((double) countByGen[0]) / tot;
			q = ((double) countByGen[1]) / tot;
		}

		// Calculate goodness of fit (p value) based on Chi square distribution
		double exactP = hweP(countByGen[0], countByGen[1], countAB);
		double exactLogP = hweLogPn12(countByGen[0], countByGen[1], countAB);

		// Add values to entry
		if (addInfo) {
			vcfEntry.addInfo("HWE", p + "," + q);
			vcfEntry.addInfo("HWEP", exactP + "");
			vcfEntry.addInfo("HWEPCHI", hwePchi2(countByGen[0], countByGen[1], countAB) + "," + countByGen[0] + "," + countByGen[1] + "," + countAB);
		}

		return exactP;
	}

	/**
	 * Calculate exact log of "P[ n12 | n1, n2]"
	 *
	 * References:
	 * 			"Principles of population genetics", Hartl & Clark, page 58 (formula 2.5)
	 * 			"A Note on Exact test of Hardy Weinbeg Equilibrium", G. Abecasis et. al. (formula 1)
	 *
	 * @param n1 : Number of 'A' alleles
	 * @param n2 : Number of 'B' alleles
	 * @param n12 : Number of 'AB' individuals
	 *
	 * Note: Total number of individuals (assuming they are diploid) is N = (n1 + n2) / 2
	 *
	 * @return Log of pValue using exact test
	 */
	public double hweLogPn12(int n1, int n2, int n12) {
		if ((n1 - n12) % 2 != 0) throw new RuntimeException("Bad nuber combination: (n1-n12) must be even");
		if ((n2 - n12) % 2 != 0) throw new RuntimeException("Bad nuber combination: (n2-n12) must be even");

		int n = n1 + n2;
		int n11 = (n1 - n12) / 2;
		int n22 = (n2 - n12) / 2;
		int N = n11 + n12 + n22;

		double num = ArithmeticUtils.factorialLog(N) - (ArithmeticUtils.factorialLog(n11) + ArithmeticUtils.factorialLog(n12) + ArithmeticUtils.factorialLog(n22));//
		double den = ArithmeticUtils.factorialLog(2 * N) - (ArithmeticUtils.factorialLog(n1) + ArithmeticUtils.factorialLog(n2));
		double logp = num - den + n12 * LOG2;
		double p = Math.exp(logp);

		if (debug) Gpr.debug("\n\tn11: " + n11 + "\n\tn12: " + n12 + "\n\tn22: " + n22 + "\n\tn: " + n + "\n\tn1: " + n1 + "\n\tn2: " + n2 + "\n\tlogP: " + logp + "\n\tp: " + p //
				+ "\n\tnum : " + num + " [" + Math.exp(num) + "]" //
				+ "\n\tden : " + den + " [" + Math.exp(den) + "]" //
		);

		return logp;
	}

	/**
	 * Calculate goodness of fit for n1, n2 combination.
	 * References: "Principles of population genetics", Hartl & Clark, page 58
	 *
	 * WARNING: The formula P_{HWE} in page 2 of "A Note on Exact test of Hardy Weinbeg Equilibrium", seems to have two mistakes (probably typos?)
	 */
	public double hweP(int n1, int n2, int n12star) {
		int min = Math.min(n1, n2);
		double pstar = hwePn12(n1, n2, n12star);
		double totP = 0;

		for (int n12 = 0; n12 <= min; n12++) {
			if (((n1 - n12) % 2 == 0) && ((n2 - n12) % 2 == 0)) {
				double p = hwePn12(n1, n2, n12);
				if (p <= pstar) totP += p;
			}
		}

		return Math.min(1.0, totP);
	}

	/**
	 * Calculate goodness of fit using Chi square approximation
	 */
	public double hwePchi2(int n1, int n2, int n12) {
		if ((n1 - n12) % 2 != 0) throw new RuntimeException("Bad nuber combination: (n1-n12) must be even");
		if ((n2 - n12) % 2 != 0) throw new RuntimeException("Bad nuber combination: (n2-n12) must be even");

		int n = n1 + n2;
		int n11 = (n1 - n12) / 2;
		int n22 = (n2 - n12) / 2;

		int N = n / 2;

		if (n == 0) return 0;

		double p = ((double) n1) / n;
		double q = ((double) n2) / n;

		double expAA = N * p * p;
		double dAA = (n11 - expAA);

		double expAB = N * 2.0 * p * q;
		double dAB = (n12 - expAB);

		double expBB = N * q * q;
		double dBB = (n22 - expBB);

		double chi2 = (dAA * dAA) / expAA + (dAB * dAB) / expAB + (dBB * dBB) / expBB;

		double pChi2 = 1.0 - new ChiSquaredDistribution(1).cumulativeProbability(chi2);
		return pChi2;
	}

	/**
	 * Calculate exact probability of "P[ n12 | n1, n2]"
	 *
	 * References:
	 * 			"Principles of population genetics", Hartl & Clark, page 58 (formula 2.5)
	 * 			"A Note on Exact test of Hardy Weinbeg Equilibrium", G. Abecasis et. al. (formula 1)
	 *
	 * @param n1 : Number of 'A' alleles
	 * @param n2 : Number of 'B' alleles
	 * @param n12 : Number of 'AB' individuals
	 *
	 * Note: Total number of individuals (assuming they are diploid) is N = (n1 + n2) / 2
	 *
	 * @return pValue using exact test
	 */
	public double hwePn12(int n1, int n2, int n12) {
		double logp = hweLogPn12(n1, n2, n12);
		double p = Math.exp(logp);
		return p;
	}
}
