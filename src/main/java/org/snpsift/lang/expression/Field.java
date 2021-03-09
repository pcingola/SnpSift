package org.snpsift.lang.expression;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderFormat;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.FieldConstant.FieldConstantNames;

/**
 * A field:
 * E.g.:  'DP', 'CHROM'
 *
 * @author pablocingolani
 */
public class Field extends Expression {

	public static final int TYPE_ALL = -2;
	public static final int TYPE_ANY = -1;

	protected String name;
	protected VcfInfoType returnType;
	protected boolean exceptionIfNotFound = true;
	protected VcfHeaderInfo vcfInfo;
	protected int number = -1;
	protected VcfInfoNumber vcfInfoNumber;

	public Field(String name) {
		this.name = name;

		// Field from first 10 columns => Set returnType
		if (name == null) returnType = VcfInfoType.String;
		else if (name.equals("CHROM") //
				|| name.equals("ID") //
				|| name.equals("REF") //
				|| name.equals("ALT") //
				|| name.equals("FILTER") //
				|| name.equals("FORMAT") //
		) returnType = VcfInfoType.String;
		else if (name.equals("QUAL")) returnType = VcfInfoType.Float;
		else if (name.equals("POS")) returnType = VcfInfoType.Integer;
		else returnType = VcfInfoType.UNKNOWN;
	}

	//	protected VcfInfoType calcReturnType(VcfHeaderInfo vcfInfo) {
	//		return vcfInfo.getVcfInfoType();
	//		//		if (isSub()) return vcfInfo.getVcfInfoType();
	//		//
	//		// Not a sub-field?
	//		// Check 'number'
	//		//		Log.debug("CHECK THIS CONDITION!");
	//		//		if (vcfInfo.getNumber() <= 1) return vcfInfo.getVcfInfoType();
	//		//		return VcfInfoType.String;
	//	}

	@Override
	public Value eval(VcfEntry vcfEntry) {

		switch (getReturnType(vcfEntry)) {

		case Integer:
			return new Value(getFieldInt(vcfEntry));

		case Float:
			return new Value(getFieldFloat(vcfEntry));

		case Flag:
			return new Value(getFieldFlag(vcfEntry));

		case Character:
		case String:
			return new Value(getFieldString(vcfEntry));

		default:
			throw new RuntimeException("Unknown return type '" + returnType + "'");
		}
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		switch (getReturnType(vcfGenotype)) {

		case Integer:
			return new Value(getFieldInt(vcfGenotype));

		case Float:
			return new Value(getFieldFloat(vcfGenotype));

		case Flag:
			return new Value(getFieldString(vcfGenotype) != null);

		case Character:
		case String:
			return new Value(getFieldString(vcfGenotype));

		default:
			throw new RuntimeException("Unknown return type '" + returnType + "'");
		}
	}

	protected Object fieldHeaderNotFound(VcfEntry vcfEntry) {
		if (exceptionIfNotFound) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF entry.\n\t" + vcfEntry);
		return null;
	}

	protected Object fieldNotFound(VcfEntry vcfEntry) {
		if (exceptionIfNotFound) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF entry.\n\t" + vcfEntry);
		return null;
	}

	/**
	 * Get a field (as an Integer) from VcfEntry
	 *
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 */
	Boolean getFieldFlag(VcfEntry vcfEntry) {
		String value = getFieldString(vcfEntry);
		return value != null && !value.isEmpty();
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 *
	 * Note: 'Float' according to VCF spec., not according to java (that is why it returns a double)
	 */
	Double getFieldFloat(VcfEntry vcfEntry) {
		if (name.equals("QUAL")) return vcfEntry.getQuality();

		String value = getFieldString(vcfEntry);
		if (value == null) return (Double) fieldNotFound(vcfEntry);
		return Gpr.parseDoubleSafe(value);
	}

	/**
	 * Get a field (as a Float) from VcfGenotype
	 *
	 * Note: 'Float' according to VCF spec., not according to java (that is why it returns a double)
	 */
	Double getFieldFloat(VcfGenotype vcfGenotype) {
		String value = getFieldString(vcfGenotype);
		if (value == null) return (Double) gtFieldNotFound(vcfGenotype);
		return Gpr.parseDoubleSafe(value);
	}

	/**
	 * Get a field (as an Integer) from VcfEntry
	 *
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 */
	Long getFieldInt(VcfEntry vcfEntry) {
		if (name.equals("POS")) return vcfEntry.getStart() + 1L;

		if (isSampleName(vcfEntry, name)) return (long) getSampleNum(vcfEntry, name);

		String value = getFieldString(vcfEntry);
		if (value == null) return (Long) fieldNotFound(vcfEntry);
		return Gpr.parseLongSafe(value);
	}

	/**
	 * Get a field (as an Integer) from VcfEntry
	 *
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 */
	Long getFieldInt(VcfGenotype vcfGenotype) {
		if (name.equals("GT")) return (long) vcfGenotype.getGenotypeCode();

		String value = getFieldString(vcfGenotype);
		if (value == null) return (Long) gtFieldNotFound(vcfGenotype);
		return Gpr.parseLongSafe(value);
	}

	/**
	 * Get a field (as a String) from VcfEntry
	 */
	String getFieldString(VcfEntry vcfEntry) {
		// Field from first 10 columns
		// if (name.equals("CHROM")) return vcfEntry.getChromosomeName();
		if (name.equals("CHROM")) return vcfEntry.getChromosomeNameOri();
		if (name.equals("ID")) return vcfEntry.getId();
		if (name.equals("REF")) return vcfEntry.getRef();
		if (name.equals("ALT")) return vcfEntry.getAltsStr();
		if (name.equals("FILTER")) return vcfEntry.getFilter();
		if (name.equals("FORMAT")) return vcfEntry.getFormat();
		if (name.equals("POS")) return "" + (vcfEntry.getStart() + 1);
		if (name.equals("QUAL")) return "" + vcfEntry.getQuality();

		// Is there a filed 'name'
		if (vcfInfo == null) {
			vcfInfo = vcfEntry.getVcfFileIterator().getVcfHeader().getVcfHeaderInfo(name);
			if (vcfInfo == null) return (String) fieldHeaderNotFound(vcfEntry);
		}

		// Get field value
		String value = vcfEntry.getInfo(name);
		if (value == null) return (String) fieldNotFound(vcfEntry);

		return value;
	}

	public String getFieldString(VcfGenotype vcfGenotype) {
		// Special fields
		if (name.equals("GT")) return vcfGenotype.getGenotypeStr();

		// Find field
		String value = vcfGenotype.get(name);
		if (value == null) return (String) gtFieldNotFound(vcfGenotype);
		return value;
	}

	public String getName() {
		return name;
	}

	/**
	 * Calculate return 'type' for this field
	 */
	VcfInfoType getReturnType(VcfEntry vcfEntry) {
		if (returnType != VcfInfoType.UNKNOWN) return returnType(vcfEntry);

		VcfHeader vcfHeader = vcfEntry.getVcfFileIterator().getVcfHeader();

		// Is there a filed 'name'
		vcfInfo = vcfHeader.getVcfHeaderInfo(name);
		if (vcfInfo != null) {
			returnType = vcfInfo.getVcfInfoType();
			number = vcfInfo.getNumber();
			vcfInfoNumber = vcfInfo.getVcfInfoNumber();
		} else {
			// Is there a genotype 'name'
			VcfHeaderFormat vcfFormat = vcfHeader.getVcfHeaderFormat(name);
			if (vcfFormat != null) {
				returnType = vcfFormat.getVcfInfoType();
			} else {
				// Is this a special field name?
				if (FieldConstant.isConstantField(name)) returnType = FieldConstantNames.valueOf(name).getType();
				else if (isSampleName(vcfEntry, name)) returnType = VcfInfoType.Integer;
				else throw new RuntimeException("INFO field '" + name + "' not found in VCF header");
			}
		}

		return returnType(vcfEntry);
	}

	public VcfInfoType getReturnType(VcfGenotype vcfGenotype) {
		if (returnType == VcfInfoType.UNKNOWN) {
			// Is there a field 'name'
			VcfHeader vcfHeader = vcfGenotype.getVcfEntry().getVcfFileIterator().getVcfHeader();
			vcfInfo = vcfHeader.getVcfHeaderFormat(name);
			if (vcfInfo == null) {
				// Is this a special field name?
				if (FieldConstant.isConstantField(name)) returnType = FieldConstantNames.valueOf(name).getType();
				else if (isSampleName(vcfGenotype.getVcfEntry(), name)) returnType = VcfInfoType.Integer;
				else throw new RuntimeException("Genotype field '" + name + "' not found in VCF header"); // Error: Not found
			}

			returnType = vcfInfo.getVcfInfoType();
		}

		return returnType(vcfGenotype);
	}

	protected int getSampleNum(VcfEntry vcfEntry, String name) {
		return vcfEntry.getVcfFileIterator().getVcfHeader().getSampleNum(name);
	}

	protected Object gtFieldNotFound(VcfGenotype vcfGenotype) {
		if (exceptionIfNotFound) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF genotype.\n\tGenotype : " + vcfGenotype + "\n\tLine     : " + vcfGenotype.getVcfEntry());
		return null;
	}

	/**
	 * Convert and index to a string
	 */
	public String indexStr(int index) {
		if (index == TYPE_ANY) return "*";
		if (index == TYPE_ALL) return "ALL";
		return Integer.toString(index);
	}

	/**
	 * Does this field contain many values (e.g. 'Number' in VCF header)
	 */
	boolean isMultiple(String val) {
		if (val == null) return false;
		return val.indexOf(VcfEntry.WITHIN_FIELD_SEP) >= 0;
	}

	protected boolean isSampleName(VcfEntry vcfEntry, String name) {
		return vcfEntry.getVcfFileIterator().getVcfHeader().getSampleNum(name) >= 0;
	}

	protected boolean isSub() {
		return false;
	}

	/**
	 * Convert an index into a number
	 */
	int parseIndexField(String text) {
		if (text.equals("*")) return -1;
		if (text.equals("ANY")) return -1;
		if (text.equals("?")) return -2;
		if (text.equals("ALL")) return -2;
		return Gpr.parseIntSafe(text);
	}

	public VcfInfoType returnType(VcfEntry vcfEntry) {
		if (returnType == null || returnType == VcfInfoType.String) return VcfInfoType.String;
		if (number == 0 || number == 1) return returnType;

		// If there can be multiple items, but there is only one, then
		// it's a 'returnType'. Otherwise it's a String
		String val = getFieldString(vcfEntry);
		if (isMultiple(val)) return VcfInfoType.String;

		return returnType;
	}

	public VcfInfoType returnType(VcfGenotype vcfGenotype) {
		if (returnType == null || returnType == VcfInfoType.String) return VcfInfoType.String;
		if (number == 0 || number == 1) return returnType;

		// If there can be multiple items, but there is only one, then
		// it's a 'returnType'. Otherwise it's a String
		String val = getFieldString(vcfGenotype);
		if (isMultiple(val)) return VcfInfoType.String;

		return returnType;
	}

	public void setExceptionIfNotFound(boolean exceptionIfNotFound) {
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	@Override
	public String toString() {
		return name;
	}
}
