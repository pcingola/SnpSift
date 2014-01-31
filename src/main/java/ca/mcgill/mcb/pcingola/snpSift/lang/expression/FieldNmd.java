package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfNmd;

/**
 * A NMD field form SnpEff:
 * 
 * E.g.:  'NMD[2].GENE'
 * 
 * @author pablocingolani
 */
public class FieldNmd extends FieldSub {

	int fieldNum = -1;

	public FieldNmd(String name, int index) {
		super("NMD." + name, index); // Add an 'NMD.' at the beginning
		fieldNum = fieldNum(this.name);
		if (fieldNum < 0) throw new RuntimeException("No such NMD subfield '" + name + "'");
	}

	/**
	 * Get field number by name
	 * @param name
	 * @return
	 */
	int fieldNum(String name) {
		return VcfNmd.fieldNum(name);
	}

	/**
	 * Get a field from VcfEntry
	 * @param vcfEntry
	 * @param field
	 * @return
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Genotype field => Look for genotype and then field
		String nmdStr = vcfEntry.getInfo("NMD");
		if (nmdStr == null) return null;

		// Find field
		String effects[] = nmdStr.split(",");
		if (index >= effects.length) return null;

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.NMD, effects.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.NMD);
		}

		// Find sub-field
		String eff = effects[idx];
		String subField[] = eff.split("\\|");

		if (fieldNum >= subField.length) return null;
		return subField[fieldNum];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "NMD[" + indexStr(index) + "]." + name;
	}
}
