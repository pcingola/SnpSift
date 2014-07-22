package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * A field that has sub fields (e.g. comma separated list of parameters):
 * E.g.:  'AF1[2]'
 * 
 * @author pablocingolani
 */
public class FieldSub extends Field {

	int index = -1;

	public FieldSub(String name, int index) {
		super(name);
		this.index = index;
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		String value = super.getFieldString(vcfEntry);

		if (value == null) return (String) fieldNotFound(vcfEntry);
		String sub[] = value.split(",");

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfGenotype vcfGenotype) {
		String value = super.getFieldString(vcfGenotype);

		if (value == null) return (String) gtFieldNotFound(vcfGenotype);
		String sub[] = value.split(",");

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.GENOTYPE_VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return name + "[" + indexStr(index) + "]";
	}
}
