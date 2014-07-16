package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Count number of heterozygous samples
 *
 * @author pablocingolani
 */
public class CountHet extends Function {

	public CountHet() {
		super("countHet", VcfInfoType.Integer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		int count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isHeterozygous()) count++;

		return new Long(count);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfGenotype vcfGenotype) {
		return vcfGenotype.isHeterozygous() ? 1 : 0;
	}

}
