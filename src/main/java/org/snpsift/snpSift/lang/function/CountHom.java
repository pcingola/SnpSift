package org.snpsift.snpSift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;

/**
 * Count number of homozygous samples
 *
 * @author pablocingolani
 */
public class CountHom extends Function {

	public CountHom() {
		super("countHom");
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		long count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isHomozygous()) count++;

		return new Value(count);
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return new Value(vcfGenotype.isHomozygous() ? 1L : 0L);
	}

}
