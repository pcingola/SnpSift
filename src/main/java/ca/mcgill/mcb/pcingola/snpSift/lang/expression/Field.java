package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldConstant.FieldConstantNames;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfHeader;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfoGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

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

	protected VcfInfoType calcReturnType(VcfHeaderInfo vcfInfo) {
		if (isSub()) return vcfInfo.getVcfInfoType();

		// Not a sub-field?
		// Check 'number'
		if (vcfInfo.getNumber() == 1) return vcfInfo.getVcfInfoType();
		return VcfInfoType.String;
	}

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
			throw new RuntimeException("Unknow return type '" + returnType + "'");
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
			throw new RuntimeException("Unknow return type '" + returnType + "'");
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
	public Double getFieldFloat(VcfEntry vcfEntry) {
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
		if (name.equals("FILTER")) return vcfEntry.getFilterPass();
		if (name.equals("FORMAT")) return vcfEntry.getFormat();
		if (name.equals("POS")) return "" + (vcfEntry.getStart() + 1);
		if (name.equals("QUAL")) return "" + vcfEntry.getQuality();

		// Is there a filed 'name'
		VcfHeaderInfo vcfInfo = vcfEntry.getVcfFileIterator().getVcfHeader().getVcfInfo(name);
		if (vcfInfo == null) return (String) fieldHeaderNotFound(vcfEntry);

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
	public VcfInfoType getReturnType(VcfEntry vcfEntry) {
		if (returnType != VcfInfoType.UNKNOWN) return returnType;

		VcfHeader vcfHeader = vcfEntry.getVcfFileIterator().getVcfHeader();

		// Is there a filed 'name'
		VcfHeaderInfo vcfInfo = vcfHeader.getVcfInfo(name);
		if (vcfInfo != null) {
			returnType = calcReturnType(vcfInfo);
		} else {
			// Is there a genotype 'name'
			VcfHeaderInfoGenotype vcfInfoGenotype = vcfHeader.getVcfInfoGenotype(name);
			if (vcfInfoGenotype != null) {
				returnType = calcReturnType(vcfInfoGenotype);
			} else {
				// Is this a special field name?
				if (FieldConstant.isConstantField(name)) returnType = FieldConstantNames.valueOf(name).getType();
				else if (isSampleName(vcfEntry, name)) returnType = VcfInfoType.Integer;
				else throw new RuntimeException("INFO field '" + name + "' not found in VCF header");
			}
		}

		return returnType;
	}

	public VcfInfoType getReturnType(VcfGenotype vcfGenotype) {
		if (returnType == VcfInfoType.UNKNOWN) {
			// Is there a field 'name'
			VcfHeader vcfHeader = vcfGenotype.getVcfEntry().getVcfFileIterator().getVcfHeader();
			VcfHeaderInfoGenotype vcfInfoGenotype = vcfHeader.getVcfInfoGenotype(name);
			if (vcfInfoGenotype == null) {
				// Is this a special field name?
				if (FieldConstant.isConstantField(name)) returnType = FieldConstantNames.valueOf(name).getType();
				else if (isSampleName(vcfGenotype.getVcfEntry(), name)) returnType = VcfInfoType.Integer;
				else throw new RuntimeException("Genotype field '" + name + "' not found in VCF header"); // Error: Not found
			}

			returnType = calcReturnType(vcfInfoGenotype);
		}

		return returnType;
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

	public void setExceptionIfNotFound(boolean exceptionIfNotFound) {
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	@Override
	public String toString() {
		return name;
	}
}
