package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Count number of ALT samples
 * 
 * @author pablocingolani
 */
public class CountVariant extends Function {

	public CountVariant() {
		super("countVariant", VcfInfoType.Integer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		int count = 0;
		for (VcfGenotype gen : vcfEntry)
			if (gen.isVariant()) count++;

		return new Long(count);
	}

}
