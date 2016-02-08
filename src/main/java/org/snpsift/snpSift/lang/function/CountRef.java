package org.snpsift.snpSift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;

/**
 * Count number of refernces samples
 *
 * @author pablocingolani
 */
public class CountRef extends Function {

	public CountRef() {
		super("countRef");
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		long count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (!gen.isVariant()) count++;

		return new Value(count);
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return new Value(vcfGenotype.isRef() ? 1L : 0L);
	}

}
