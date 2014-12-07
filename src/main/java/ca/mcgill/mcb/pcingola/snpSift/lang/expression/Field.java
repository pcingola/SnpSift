package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

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

	String name;
	boolean exceptionIfNotFound = true;

	public Field(String name) {
		this.name = name;

		// Field from first 10 columns => Set returnType
		if (name.equals("CHROM") //
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

	/**
	 * Calculate return 'type' for this field
	 */
	public VcfInfoType getReturnType(VcfEntry vcfEntry) {
		if (returnType == VcfInfoType.UNKNOWN) {
			VcfHeader vcfHeader = vcfEntry.getVcfFileIterator().getVcfHeader();

			// Is there a filed 'name'
			VcfHeaderInfo vcfInfo = vcfHeader.getVcfInfo(name);
			if (vcfInfo != null) returnType = vcfInfo.getVcfInfoType();
			else {
				// Is there a genotype 'name'
				VcfHeaderInfoGenotype vcfInfoGenotype = vcfHeader.getVcfInfoGenotype(name);
				if (vcfInfoGenotype != null)	returnType = vcfInfoGenotype.getVcfInfoType();
				else {
					// Is this a special field name?
					if (FieldConstant.isConstantField(name)) returnType = FieldConstantNames.valueOf(name).getType();
					else throw new RuntimeException("INFO field '" + name + "' not found in VCF header");
				}
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
				if (FieldConstant.isConstantField(name)) return FieldConstantNames.valueOf(name).getType();

				// Error: Not found
				throw new RuntimeException("Genotype field '" + name + "' not found in VCF header");
			}
			returnType = vcfInfoGenotype.getVcfInfoType();
		}

		return returnType;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		switch ( getReturnType(vcfEntry) ) {

		case Integer:
			return getFieldInt(vcfEntry);

		case Float:
			return getFieldFloat(vcfEntry);

		case Flag:
			return getFieldString(vcfEntry) != null;

		case Character:
		case String:
			return getFieldString(vcfEntry);

		default:
			throw new RuntimeException("Unknow return type '" + returnType + "'");
		}
	}

	@Override
	public Comparable get(VcfGenotype vcfGenotype) {

		switch ( getReturnType(vcfGenotype) ) {

		case Integer:
			return getFieldInt(vcfGenotype);

		case Float:
			return getFieldFloat(vcfGenotype);

		case Flag:
			return getFieldString(vcfGenotype) != null;

		case Character:
		case String:
			return getFieldString(vcfGenotype);

		default:
			throw new RuntimeException("Unknow return type '" + returnType + "'");
		}
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
	public Double getFieldFloat(VcfGenotype vcfGenotype) {
		String value = getFieldString(vcfGenotype);
		if (value == null) return (Double) gtFieldNotFound(vcfGenotype);
		return Gpr.parseDoubleSafe(value);
	}

	protected Object fieldNotFound(VcfEntry vcfEntry) {
		if( exceptionIfNotFound ) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF entry.\n\t" + vcfEntry);
		return null;
	}
	
	protected Object fieldHeaderNotFound(VcfEntry vcfEntry) {
		if( exceptionIfNotFound ) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF entry.\n\t" + vcfEntry);
		return null;
	}

	protected Object gtFieldNotFound(VcfGenotype vcfGenotype) {
		if( exceptionIfNotFound ) throw new RuntimeException("Error: Field '" + this + "' not available in this VCF genotype.\n\tGenotype : " + vcfGenotype + "\n\tLine     : " + vcfGenotype.getVcfEntry());
		return null;
	}
	
	/**
	 * Get a field (as an Integer) from VcfEntry
	 *
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 */
	public Long getFieldInt(VcfEntry vcfEntry) {
		if (name.equals("POS")) return vcfEntry.getStart() + 1L;

		String value = getFieldString(vcfEntry);
		if (value == null) return (Long) fieldNotFound(vcfEntry);
		return Gpr.parseLongSafe(value);
	}

	/**
	 * Get a field (as an Integer) from VcfEntry
	 *
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 */
	public Long getFieldInt(VcfGenotype vcfGenotype) {
		if (name.equals("GT")) return (long) vcfGenotype.getGenotypeCode();

		String value = getFieldString(vcfGenotype);
		if (value == null) return (Long) gtFieldNotFound(vcfGenotype);
		return Gpr.parseLongSafe(value);
	}

	/**
	 * Get a field (as a String) from VcfEntry
	 */
	public String getFieldString(VcfEntry vcfEntry) {
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
	 * Convert and index to a string
	 */
	public String indexStr(int index) {
		if (index == TYPE_ANY) return "*";
		if (index == TYPE_ALL) return "ALL";
		return Integer.toString(index);
	}

	public void setExceptionIfNotFound(boolean exceptionIfNotFound) {
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	@Override
	public String toString() {
		return name;
	}
}
