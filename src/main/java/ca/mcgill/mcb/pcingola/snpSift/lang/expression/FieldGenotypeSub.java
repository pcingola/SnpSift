package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * A field:
 * E.g.:  'GEN[2].PL[3]'
 *
 * @author pablocingolani
 */
public class FieldGenotypeSub extends FieldGenotype {

	public FieldGenotypeSub(String name, int genotypeIndex, int index) {
		super(name, genotypeIndex);
		this.index = index;
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 * @return
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		String value = super.getFieldString(vcfEntry);

		String sub[] = value.split(",");

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.GENOTYPE_VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.GENOTYPE_VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 * @return
	 */
	@Override
	public String getFieldString(VcfGenotype vcfGenotype) {
		String value = super.getFieldString(vcfGenotype);
		String sub[] = value.split(",");

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.GENOTYPE_VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.GENOTYPE_VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	@Override
	public String toString() {
		return "GEN[" + indexStr(genotypeIndex) + "]." + name + "[" + indexStr(index) + "]";
	}
}
