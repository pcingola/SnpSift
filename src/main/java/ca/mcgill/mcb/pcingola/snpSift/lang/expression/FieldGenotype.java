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

	public FieldGenotype(String name, Expression indexExpr) {
		super(name, indexExpr);
	}

	/**
	 * Evaluate expressions and return VcfGenotype
	 */
	protected VcfGenotype evalGenotype(VcfEntry vcfEntry) {
		// Find index value
		int genotypeIndex = evalIndex(vcfEntry);

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
		return vcfGenotype;
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Genotype field => Look for genotype and then field
		VcfGenotype vcfGenotype = evalGenotype(vcfEntry);

		// Find value
		String value = getValue(vcfGenotype);

		// Not found? Should we raise an exception?
		if ((value == null) && exceptionIfNotFound) throw new RuntimeException("Error: Genotype field '" + name + "' not available in this entry.\n\t" + this);

		return value;
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 */
	@Override
	public String getFieldString(VcfGenotype vcfGenotype) {
		// Find value
		String value = getValue(vcfGenotype);

		// Not found? Should we raise an exception?
		if (value == null) return (String) gtFieldNotFound(vcfGenotype);

		return value;
	}

	/**
	 * Find value within genotype
	 */
	String getValue(VcfGenotype vcfGenotype) {
		if (name == null) return vcfGenotype.toString(); // No sub-field? Use whole genotype
		return vcfGenotype.get(name);
	}

	@Override
	protected boolean isSub() {
		return false;
	}

	@Override
	public String toString() {
		return "GEN[" + indexExpr + "]" + (name != null ? "." + name : "");
	}
}
