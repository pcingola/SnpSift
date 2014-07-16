package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * A field:
 * E.g.:  'GEN[2].GT'
 *
 * @author pablocingolani
 */
public class FieldGenotype extends FieldSub {

	int genotypeIndex = -1;

	public FieldGenotype(String name, int genotypeIndex) {
		super(name, -1);
		this.genotypeIndex = genotypeIndex;
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		String value = null;

		// Sanity check
		int maxIdx = vcfEntry.getVcfGenotypes().size() - 1;
		if (maxIdx < genotypeIndex) throw new RuntimeException("Error: Genotype numer '" + genotypeIndex + "' does not exists. Max genotype number is '" + maxIdx + "'\n" + vcfEntry);

		// Is this field 'iterable'?
		int idx = genotypeIndex;
		if (genotypeIndex < 0) {
			FieldIterator.get().setMax(IteratorType.GENOTYPE, vcfEntry.getVcfGenotypes().size() - 1);
			FieldIterator.get().setType(genotypeIndex);
			idx = FieldIterator.get().get(IteratorType.GENOTYPE);
		}

		// Genotype field => Look for genotype and then field
		VcfGenotype vcfGenotype = vcfEntry.getVcfGenotype(idx);
		value = vcfGenotype.get(name);

		// Not found? Should we raise an exception?
		if ((value == null) && exceptionIfNotFound) throw new RuntimeException("Error: Genotype field '" + name + "' not available in this entry.\n\t" + this);

		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "GEN[" + indexStr(genotypeIndex) + "]." + name;
	}
}
