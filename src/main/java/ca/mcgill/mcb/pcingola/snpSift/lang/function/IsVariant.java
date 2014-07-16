package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Is 'genotypeNum' reference?
 *
 * @author pablocingolani
 */
public class IsVariant extends FunctionBoolGenotype {

	public IsVariant(int genotypNum) {
		super("isVariant", genotypNum);
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		VcfGenotype gen = vcfEntry.getVcfGenotype(genotypeNum);
		return gen.isVariant();
	}

	@Override
	public boolean eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isVariant();
	}

}
