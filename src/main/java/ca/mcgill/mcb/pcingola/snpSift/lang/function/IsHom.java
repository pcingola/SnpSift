package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Is 'genotypeNum' homozygous?
 *
 * @author pablocingolani
 */
public class IsHom extends FunctionBoolGenotype {

	public IsHom(int genotypNum) {
		super("isHom", genotypNum);
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		VcfGenotype gen = vcfEntry.getVcfGenotype(genotypeNum);
		return gen.isHomozygous();
	}

	@Override
	public boolean eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isHomozygous();
	}

}
