package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Count number of homozygous samples
 *
 * @author pablocingolani
 */
public class CountHom extends Function {

	public CountHom() {
		super("countHom", VcfInfoType.Integer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		int count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isHomozygous()) count++;

		return new Long(count);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfGenotype vcfGenotype) {
		return vcfGenotype.isHomozygous() ? 1 : 0;
	}

}
