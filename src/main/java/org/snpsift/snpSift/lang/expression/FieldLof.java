package org.snpsift.snpSift.lang.expression;

import org.snpeff.snpEffect.LossOfFunction;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpeff.vcf.VcfLof;
import org.snpsift.snpSift.lang.expression.FieldIterator.IteratorType;

/**
 * A LOF field form SnpEff:
 *
 * E.g.:  'LOF[2].GENE'
 *
 * @author pablocingolani
 */
public class FieldLof extends FieldSub {

	int fieldNum = -1;
	String infoFieldName;

	public FieldLof(String name, Expression indexExpr) {
		super(name, indexExpr); // Add an 'LOF.' at the beginning
		init();
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Genotype field => Look for genotype and then field
		String infoStr = vcfEntry.getInfo(infoFieldName);
		if (infoStr == null) return (String) fieldNotFound(vcfEntry);

		// Find index value
		int index = evalIndex(vcfEntry);

		// Find field
		String lofEntries[] = infoStr.split(",");
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
		return getSubField(lof);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public VcfInfoType getReturnType(VcfEntry vcfEntry) {
		if (name == null) return VcfInfoType.String;
		if (returnType != VcfInfoType.UNKNOWN) return returnType;

		VcfHeader vcfHeader = vcfEntry.getVcfFileIterator().getVcfHeader();

		// Is there a field 'name'
		String headerName = infoFieldName + "." + name;
		VcfHeaderInfo vcfInfo = vcfHeader.getVcfInfo(headerName);
		if (vcfInfo != null) returnType = vcfInfo.getVcfInfoType();
		else throw new RuntimeException("Sub-field '" + headerName + "' not found in VCF header");

		return returnType;
	}

	/**
	 * Parse sub-field
	 */
	String getSubField(String lof) {
		if (name == null) return lof;

		// Find sub-field
		if (lof.startsWith("(")) lof = lof.substring(1);
		if (lof.endsWith(")")) lof = lof.substring(0, lof.length() - 1);
		String subField[] = lof.split("\\|");

		if (fieldNum >= subField.length) return null;
		return subField[fieldNum];
	}

	protected void init() {
		infoFieldName = LossOfFunction.VCF_INFO_LOF_NAME;

		if (name != null) {
			String headerName = infoFieldName + "." + name;
			fieldNum = VcfLof.fieldNum(headerName);
			if (fieldNum < 0) { throw new RuntimeException("No such " + infoFieldName + " subfield '" + headerName + "'"); }
		}
	}

	@Override
	public String toString() {
		return infoFieldName + "[" + indexExpr + "]" + (name != null ? "." + name : "");
	}
}
