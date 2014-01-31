package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Is 'genotypeNum' heterozygous?
 * 
 * @author pablocingolani
 */
public class IsHet extends FunctionBoolGenotype {

	public IsHet(int genotypNum) {
		super("isHet", genotypNum);
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		VcfGenotype gen = vcfEntry.getVcfGenotype(genotypeNum);
		return gen.isHeterozygous();
	}

}
