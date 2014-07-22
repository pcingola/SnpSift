package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfLof;

/**
 * A LOF field form SnpEff:
 * 
 * E.g.:  'LOF[2].GENE'
 * 
 * @author pablocingolani
 */
public class FieldLof extends FieldSub {

	int fieldNum = -1;

	public FieldLof(String name, int index) {
		super("LOF." + name, index); // Add an 'LOF.' at the beginning
		fieldNum = fieldNum(this.name);
		if (fieldNum < 0) throw new RuntimeException("No such LOF subfield '" + name + "'");
	}

	/**
	 * Get field number by name
	 */
	int fieldNum(String name) {
		return VcfLof.fieldNum(name);
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Genotype field => Look for genotype and then field
		String lofStr = vcfEntry.getInfo("LOF");
		if (lofStr == null) return (String) fieldNotFound(vcfEntry);


		// Find field
		String lofEntries[] = lofStr.split(",");
		if (index >= lofEntries.length) return null;

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.LOF, lofEntries.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.LOF);
		}

		// Find sub-field
		String lof = lofEntries[idx];
		if (lof.startsWith("(")) lof = lof.substring(1);
		if (lof.endsWith(")")) lof = lof.substring(0, lof.length() - 1);
		String subField[] = lof.split("\\|");

		if (fieldNum >= subField.length) return null;
		return subField[fieldNum];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "LOF[" + indexStr(index) + "]." + name;
	}
}
