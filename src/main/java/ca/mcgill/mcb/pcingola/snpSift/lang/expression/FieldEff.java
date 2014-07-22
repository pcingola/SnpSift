package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import java.util.List;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect.FormatVersion;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * An 'EFF' field form SnpEff:
 * 
 * E.g.:  'EFF[2].GENE'
 * 
 * @author pablocingolani
 */
public class FieldEff extends FieldSub {

	int fieldNum = -1;
	FormatVersion formatVersion = null;

	/**
	 * Constructor
	 * @param formatVersion : Can be null (it will be guessed)
	 */
	public FieldEff(String name, int index, FormatVersion formatVersion) {
		super("EFF." + name, index); // Add an 'EFF.' at the beginning
		this.formatVersion = formatVersion;
	}

	/**
	 * Get field number by name
	 */
	int fieldNum(String name, VcfEffect eff) {
		if (formatVersion == null) formatVersion = eff.formatVersion();
		return VcfEffect.fieldNum(name, formatVersion);
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Get all effects
		List<VcfEffect> effects = vcfEntry.parseEffects(formatVersion);
		if (effects.size() <= 0) return null;

		// Find field
		if (index >= effects.size()) return null;

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.EFFECT, effects.size() - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.EFFECT);
		}

		// Find sub-field
		VcfEffect eff = effects.get(idx);
		if (eff == null) return (String) fieldNotFound(vcfEntry);

		// Field number not set? Try to guess it
		if (fieldNum < 0) {
			fieldNum = fieldNum(name, eff);
			if (fieldNum < 0) throw new RuntimeException("No such EFF subfield '" + name + "'");
		}

		eff.formatVersion();

		String value = eff.get(fieldNum);
		if (value == null) return (String) fieldNotFound(vcfEntry);
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "EFF[" + indexStr(index) + "]." + name;
	}
}
