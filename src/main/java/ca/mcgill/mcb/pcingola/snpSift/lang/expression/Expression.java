package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * A generic expresion
 * Expressions have values (VcfInfoType)
 *
 * @author pcingola
 */
public abstract class Expression {

	protected static boolean debug = false;

	protected VcfInfoType returnType = VcfInfoType.UNKNOWN; // Default type is INTEGER

	@SuppressWarnings("rawtypes")
	public boolean canCompareTo(Expression expr, VcfEntry vcfEntry) {
		Comparable o1 = get(vcfEntry);
		Comparable o2 = expr.get(vcfEntry);
		return (o1 != null) && (o2 != null);
	}

	public boolean canCompareTo(Expression expr, VcfGenotype vcfGenotype) {
		Comparable o1 = get(vcfGenotype);
		Comparable o2 = expr.get(vcfGenotype);
		return (o1 != null) && (o2 != null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(Expression expr, VcfEntry vcfEntry) {
		VcfInfoType exprType = expr.getReturnType(vcfEntry);

		// Same data type? Just compare them
		Comparable o1 = get(vcfEntry);
		Comparable o2 = expr.get(vcfEntry);
		if ((o1 == null) && (o2 == null)) return 0;
		if ((o1 == null) && (o2 != null)) return -1;
		if ((o1 != null) && (o2 == null)) return 1;
		if (returnType == exprType) return o1.compareTo(o2);

		// One Integer one Float?
		if (((returnType == VcfInfoType.Integer) || (returnType == VcfInfoType.Float)) //
				&& ((exprType == VcfInfoType.Integer) || (exprType == VcfInfoType.Float)) //
				) {
			// Convert to Float and compare
			double d1 = getFloat(vcfEntry);
			double d2 = expr.getFloat(vcfEntry);

			if (d1 == d2) return 0;
			if (d1 < d2) return -1;
			return 1;
		} else if (((returnType == VcfInfoType.String) || (returnType == VcfInfoType.Character)) //
				&& ((exprType == VcfInfoType.Character) || (exprType == VcfInfoType.String)) //
				) {
			String s1 = getString(vcfEntry);
			String s2 = expr.getString(vcfEntry);

			return s1.compareTo(s2);
		}

		// Not comparable types
		throw new RuntimeException("Cannot compare '" + returnType + "' to '" + exprType + "'");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(Expression expr, VcfGenotype vcfGenotype) {
		VcfInfoType exprType = expr.getReturnType(vcfGenotype);

		// Same data type? Just compare them
		Comparable o1 = get(vcfGenotype);
		Comparable o2 = expr.get(vcfGenotype);
		if ((o1 == null) && (o2 == null)) return 0;
		if ((o1 == null) && (o2 != null)) return -1;
		if ((o1 != null) && (o2 == null)) return 1;
		if (returnType == exprType) return o1.compareTo(o2);

		// One Integer one Float?
		if (((returnType == VcfInfoType.Integer) || (returnType == VcfInfoType.Float)) //
				&& ((exprType == VcfInfoType.Integer) || (exprType == VcfInfoType.Float)) //
				) {
			// Convert to Float and compare
			double d1 = getFloat(vcfGenotype);
			double d2 = expr.getFloat(vcfGenotype);

			if (d1 == d2) return 0;
			if (d1 < d2) return -1;
			return 1;
		} else if (((returnType == VcfInfoType.String) || (returnType == VcfInfoType.Character)) //
				&& ((exprType == VcfInfoType.Character) || (exprType == VcfInfoType.String)) //
				) {
			String s1 = getString(vcfGenotype);
			String s2 = expr.getString(vcfGenotype);

			return s1.compareTo(s2);
		}

		// Not comparable types
		throw new RuntimeException("Cannot compare '" + returnType + "' to '" + exprType + "'");
	}

	/**
	 * Get expression value
	 */
	@SuppressWarnings("rawtypes")
	public abstract Comparable get(VcfEntry vcfEntry);

	public abstract Comparable get(VcfGenotype vcfGenotype);

	/**
	 * Get expression value as a 'Float'
	 */
	public double getFloat(VcfEntry vcfEntry) {
		Object o = get(vcfEntry);

		switch (returnType) {
		case Float:
			return (Double) o;
		case Integer:
			return (Long) o;
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to FLOAT");
		}
	}

	public double getFloat(VcfGenotype vcfGenotype) {
		Object o = get(vcfGenotype);

		switch (returnType) {
		case Float:
			return (Double) o;
		case Integer:
			return (Long) o;
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to FLOAT");
		}
	}

	/**
	 * Get expression value as an 'Int'
	 */
	public long getInt(VcfEntry vcfEntry) {
		Object o = get(vcfEntry);

		switch (returnType) {
		case Integer:
			return ((Long) o);
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to INT");
		}
	}

	public long getInt(VcfGenotype vcfGenotype) {
		Object o = get(vcfGenotype);

		switch (returnType) {
		case Integer:
			return (Long) o;
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to INT");
		}
	}

	/**
	 * Get expression value as a 'Flag'
	 */
	public boolean getFlag(VcfEntry vcfEntry) {
		Object o = get(vcfEntry);

		switch (returnType) {
		case Flag:
			if( o == null ) return false;
			return (Boolean) o;
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to FLAG");
		}
	}

	public boolean getFlag(VcfGenotype vcfGenotype) {
		Object o = get(vcfGenotype);

		switch (returnType) {
		case Flag:
			if( o == null ) return false;
			return (Boolean) o;
		default:
			throw new RuntimeException("Cannot cast '" + returnType + "' to FLAG");
		}
	}

	public VcfInfoType getReturnType(VcfEntry vcfEntry) {
		return returnType;
	}

	public VcfInfoType getReturnType(VcfGenotype vcfGenotype) {
		return returnType;
	}

	/**
	 * Get expression value as a String
	 * @param vcfEntry
	 * @return
	 */
	public String getString(VcfEntry vcfEntry) {
		return get(vcfEntry).toString();
	}

	public String getString(VcfGenotype vcfGenotype) {
		return get(vcfGenotype).toString();
	}

}
