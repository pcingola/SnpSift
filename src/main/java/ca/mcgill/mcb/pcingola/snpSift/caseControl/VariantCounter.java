package ca.mcgill.mcb.pcingola.snpSift.caseControl;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * A counnter for genotypes
 * 
 * @author pcingola
 */
public class VariantCounter {

	int genotypes[];
	StringBuilder effects;

	public VariantCounter(int size) {
		genotypes = new int[size];
		effects = new StringBuilder();
	}

	public void addEffect(String effStr) {
		if (effects.length() > 0) effects.append("\t");
		effects.append(effStr);
	}

	public int countNonZeroGenotypes() {
		int count = 0;
		for (int i = 0; i < genotypes.length; i++)
			if (genotypes[i] > 0) count++;
		return count;
	}

	public int getGenotype(int idx) {
		return genotypes[idx];
	}

	public void incGenotype(int idx) {
		genotypes[idx]++;
	}

	public void incGenotype(int idx, int value) {
		genotypes[idx] += value;
	}

	/**
	 * Update genotype counts
	 * @param ve
	 */
	public void parseGenotypes(VcfEntry ve) {
		int idx = 0;
		for (VcfGenotype gen : ve.getVcfGenotypes()) {
			if (gen.isVariant()) genotypes[idx]++;
			idx++;
		}
	}

	public void setGenotype(int idx, int value) {
		genotypes[idx] = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(countNonZeroGenotypes());

		// Show genotypes
		for (int i = 0; i < genotypes.length; i++)
			sb.append("\t" + genotypes[i]);

		sb.append("\t");
		sb.append(effects);

		return sb.toString();
	}

}
