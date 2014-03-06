package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfo;
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

	public VcfInfoType calcReturnType(VcfEntry vcfEntry) {
		if (returnType == VcfInfoType.UNKNOWN) {
			// Is there a filed 'name'
			VcfInfo vcfInfo = vcfEntry.getVcfFileIterator().getVcfHeader().getVcfInfo(name);
			if (vcfInfo == null) throw new RuntimeException("No such field '" + name + "'");
			returnType = vcfInfo.getVcfInfoType();
		}

		return returnType;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		calcReturnType(vcfEntry);
		switch (returnType) {

		case Integer:
			return getFieldInt(vcfEntry);

		case Float:
			return getFieldFloat(vcfEntry);

		case Character:
		case String:
		case Flag:
			return getFieldString(vcfEntry);

		default:
			throw new RuntimeException("Unknow return type '" + returnType + "'");
		}
	}

	/**
	 * Get a field (as a Float) from VcfEntry
	 * 
	 * Note: 'Float' according to VCF spec., not according to java (that is why it returns a double)
	 * 
	 * @param vcfEntry
	 * @param field
	 * @return
	 */
	public Double getFieldFloat(VcfEntry vcfEntry) {
		if (name.equals("QUAL")) return vcfEntry.getQuality();

		String value = getFieldString(vcfEntry);
		if (value == null) return null;
		return Gpr.parseDoubleSafe(value);
	}

	/**
	 * Get a field (as an Integer) from VcfEntry
	 * 
	 * Note: 'Int' according to VCF spec., not according to java (that is why it returns a long)
	 * 
	 * @param vcfEntry
	 * @param key
	 * @return
	 */
	public Long getFieldInt(VcfEntry vcfEntry) {
		if (name.equals("POS")) return vcfEntry.getStart() + 1L;

		String value = getFieldString(vcfEntry);
		if (value == null) return null;

		return Gpr.parseLongSafe(value);
	}

	/**
	 * Get a field (as a String) from VcfEntry
	 * @param vcfEntry
	 * @param field
	 * @return
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

		// Get field from INFO field
		String value = null;

		// Is there a filed 'name'
		VcfInfo vcfInfo = vcfEntry.getVcfFileIterator().getVcfHeader().getVcfInfo(name);
		if ((vcfInfo == null) && exceptionIfNotFound) throw new RuntimeException("No such field '" + name + "'");

		// Get field and parse it
		value = vcfEntry.getInfo(name);

		// Not found? Should we raise an exception?
		if ((value == null) && exceptionIfNotFound) throw new RuntimeException("Error: Info field '" + name + "' not available in this entry.\n\t" + this);

		return value;
	}

	public String getName() {
		return name;
	}

	/**
	 * Convert and index to a string
	 * @param index
	 * @return
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
