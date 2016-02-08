package org.snpsift.snpSift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;

/**
 * Count number of ALT samples
 *
 * @author pablocingolani
 */
public class CountVariant extends Function {

	public CountVariant() {
		super("countVariant");
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		long count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isVariant()) count++;

		return new Value(count);
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return new Value(vcfGenotype.isVariant() ? 1L : 0L);
	}

}
