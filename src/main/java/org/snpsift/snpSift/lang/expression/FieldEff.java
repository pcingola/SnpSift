package org.snpsift.snpSift.lang.expression;

import java.util.List;

import org.snpeff.vcf.EffFormatVersion;
import org.snpeff.vcf.VcfEffect;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.snpSift.lang.expression.FieldIterator.IteratorType;

/**
 * An 'EFF' field form SnpEff:
 *
 * E.g.:  'EFF[2].GENE'
 *
 * @author pablocingolani
 */
public class FieldEff extends FieldSub {

	String fieldName;
	EffFormatVersion formatVersion = null;

	/**
	 * Constructor
	 * @param formatVersion : Can be null (it will be guessed)
	 */
	public FieldEff(String name, Expression idxExpr, EffFormatVersion formatVersion, String fieldName) {
		super(name, idxExpr);
		this.formatVersion = formatVersion;
		this.fieldName = fieldName.toUpperCase();
	}

	/**
	 * Should this be 'EFF' or 'ANN'?
	 */
	String annEff() {
		if (formatVersion == null) return fieldName;
		return (formatVersion.isAnn() ? "ANN" : "EFF");
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		// Get all effects
		List<VcfEffect> effects = vcfEntry.getVcfEffects(formatVersion);
		if (effects.size() <= 0) return null;

		// Find index value
		int index = evalIndex(vcfEntry);

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
		String value = getSubField(eff, vcfEntry);
		return value;
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
		String headerName = annEff() + "." + name;
		VcfHeaderInfo vcfInfo = vcfHeader.getVcfInfo(headerName);
		if (vcfInfo != null) returnType = vcfInfo.getVcfInfoType();
		else throw new RuntimeException("Sub-field '" + headerName + "' not found in VCF header");

		return returnType;
	}

	/**
	 * Find sub-field
	 */
	String getSubField(VcfEffect eff, VcfEntry vcfEntry) {
		if (eff == null) return (String) fieldNotFound(vcfEntry);

		// No sub-field? => Use the whole field
		if (name == null) return eff.getVcfFieldString();

		// Find sub-field
		String value = eff.getFieldByName(name);
		if (value == null) return (String) fieldNotFound(vcfEntry);
		return value;
	}

	@Override
	public String toString() {
		return annEff() + "[" + indexExpr + "]" + (name != null ? "." + name : "");
	}
}
