package org.snpsift;

import java.util.Iterator;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;

/**
 * Calculate Linkage Disequilibrium
 * 
 * Reference: "Principles of population genetics (4th edition)" Hartl & Clark, pages 73 to 81
 * Note: I try to follow the same notation as the book.
 * 
 * WARNING: I assume that the organism is diploid (i.e. it has two chromosomes)
 * 
 * @author pablocingolani
 */
public class VcfLd {

	public static boolean debug = false;

	int countAB, countAb, countaB, countab;
	int countA, counta, countB, countb;
	double D, Dmin, Dmax;
	double pA, pB, qa, qb;
	double rSquare;
	double Dprime;

	public VcfLd() {
	}

	/** 
	 * Count genotypes
	 * @param genotypeA : Genotype number at location 'A' ( either '0' or '1', representing 'A' or 'a' respectively) 
	 * @param genotypeB : Genotype number at location 'B' ( either '0' or '1', representing 'B' or 'b' respectively) 
	 */
	void countGenotypes(int genotypeA, int genotypeB) {
		if (genotypeA == 0) {
			countA++;

			if (genotypeB == 0) {
				countAB++;
				countB++;
			} else {
				countAb++;
				countb++;
			}
		} else {
			counta++;

			if (genotypeB == 0) {
				countaB++;
				countB++;
			} else {
				countab++;
				countb++;
			}
		}
	}

	public double getD() {
		return D;
	}

	public double getDmax() {
		return Dmax;
	}

	public double getDmin() {
		return Dmin;
	}

	public double getDprime() {
		return Dprime;
	}

	public double getpA() {
		return pA;
	}

	public double getpB() {
		return pB;
	}

	public double getQa() {
		return qa;
	}

	public double getQb() {
		return qb;
	}

	public double getrSquare() {
		return rSquare;
	}

	/**
	 * Calculate linkage disequilibrium parameters
	 * 
	 * @return linkage disequilibrium between 'A' and 'B'
	 */
	public double ld() {
		//---
		// Calculate probabilities
		//---
		double countXX = countAB + countAb + countaB + countab;
		double pAB = countAB / countXX;
		double pAb = countAb / countXX;
		double paB = countaB / countXX;
		double pab = countab / countXX;

		double countX = countA + counta;
		pA = countA / countX;
		qa = counta / countX;
		pB = countB / countX;
		qb = countb / countX;

		if (countX != (countB + countb)) throw new RuntimeException("Counts do not math.\n\tcountA  : " + countA + "\tcounta : " + counta + "\tTotal: " + (countA + counta) + "\n\tcountB : " + countB + "\tcountb : " + countb + "\tTotal : " + (countB + countb));

		// Calculate 'D' (linkage disequilibrium parameter)
		D = pAB * pab - pAb * paB;
		Dmin = Math.max(-pA * pB, -qa * qb);
		Dmax = Math.min(pA * qb, qa * pB);

		// Calculate rSquare (see page 81)
		rSquare = (D * D) / (pA * qa * pB * qb);

		// Calculate rSquare (see page 81)
		Dprime = (D >= 0 ? D / Dmax : D / Dmin);

		if (debug) Gpr.debug("\n\tD    : " + D //
				+ "\n\tDmin : " + Dmin //
				+ "\n\tDmax : " + Dmax //
				+ "\n\tD'   : " + Dprime //
				+ "\n\tr^2  : " + rSquare //
				+ "\n" //
				+ "\n\tcountAB : " + countAB + "\tcountAb: " + countAb + "\tcountaB: " + countaB + "\tcountab: " + countab + "\tcountXX: " + countXX //
				+ "\n\tpAB : " + pAB + "\tpAb : " + pAb + "\tpaB : " + paB + "\tpab : " + pab//
				+ "\n" //
				+ "\n\tcountA: " + countA + "\tcounta: " + counta + "\tTotal: " + (countA + counta) //
				+ "\n\tpA : " + pA + "\tqa : " + qa//
				+ "\n" //
				+ "\n\tcountB: " + countB + "\tcountb: " + countb + "\tTotal: " + (countB + countb) //
				+ "\n\tpB : " + pB + "\tqb : " + qb//
		);

		if ((D < Dmin) || (D > Dmax)) throw new RuntimeException("D out of range. This should never happen!" + "\n\tD    : " + D + "\n\tDmin : " + Dmin + "\n\tDmax : " + Dmax);

		return D;
	}

	/**
	 * Calculate linkage disequilibrium parameter
	 * 
	 * @param vcfEntryA : Vcf entry at locus 'A'
	 * @param vcfEntryB : Vcf entry at locus 'B'
	 * 
	 * @return linkage disequilibrium between 'A' and 'B'
	 */
	public double ld(VcfEntry vcfEntryA, VcfEntry vcfEntryB) {
		// Sanity check
		String sanity = sanityCheck(vcfEntryA, vcfEntryB);
		if (!sanity.isEmpty()) throw new RuntimeException(sanity);

		// Reset counters
		countAB = countAb = countaB = countab = 0;
		countA = counta = countB = countb = 0;

		// Count all genotype pairs (on both chromosomes)
		Iterator<VcfGenotype> ita = vcfEntryA.iterator();
		Iterator<VcfGenotype> itb = vcfEntryB.iterator();
		while (ita.hasNext() && itb.hasNext()) {
			VcfGenotype ga = ita.next();
			VcfGenotype gb = itb.next();

			// Sanity check
			if (!ga.isPhased()) throw new RuntimeException("Only phased genotypes can be used\n\tvcfEntryA:\t" + vcfEntryA);
			if (!gb.isPhased()) throw new RuntimeException("Only phased genotypes can be used\n\tvcfEntryB:\t" + vcfEntryB);

			// Count genotypes on both strands
			int genA[] = ga.getGenotype();
			int genB[] = gb.getGenotype();
			countGenotypes(genA[0], genB[0]); // One strand (remember that the vcfEntries are phased)
			countGenotypes(genA[1], genB[1]); // Another strand
			Gpr.debug(ga + "\tgenA: " + genA[0] + " , " + genA[1] + "\tcountA: " + countA);
		}

		// Calculate parameters
		return ld();
	}

	/**
	 * Return an error message if any problem is found or empty string if it is OK.
	 * @return
	 */
	public String sanityCheck(VcfEntry vcfEntryA, VcfEntry vcfEntryB) {
		if (vcfEntryA.getAlts().length != 1) return "Only entries with one ALT are accepted:\n\tvcfEntryA:\t" + vcfEntryA;
		if (vcfEntryB.getAlts().length != 1) return "Only entries with one ALT are accepted:\n\tvcfEntryB:\t" + vcfEntryB;
		if (vcfEntryA.getVcfGenotypes().size() != vcfEntryB.getVcfGenotypes().size()) return "Number of genotypes do not match\n\tvcfEntryA:\t" + vcfEntryA + "\n\tvcfEntryB:\t" + vcfEntryB;
		return "";
	}

	/**
	 * Set counters in order to calculate LD parameters
	 * @param countAB
	 * @param countAb
	 * @param countaB
	 * @param countab
	 * @param countA
	 * @param counta
	 * @param countB
	 * @param countb
	 */
	public void setCount(int countAB, int countAb, int countaB, int countab, int countA, int counta, int countB, int countb) {
		this.countAB = countAB;
		this.countAb = countAb;
		this.countaB = countaB;
		this.countab = countab;
		this.countA = countA;
		this.counta = counta;
		this.countB = countB;
		this.countb = countb;
	}
}
