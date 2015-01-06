package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Count number of heterozygous samples
 *
 * @author pablocingolani
 */
public class CountHet extends Function {

	public CountHet() {
		super("countHet");
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		long count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isHeterozygous()) count++;

		return new Value(count);
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return new Value(vcfGenotype.isHeterozygous() ? 1L : 0L);
	}

}
