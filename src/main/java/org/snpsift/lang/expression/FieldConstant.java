package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfInfoType;

/**
 * A 'constant' field: e.g. 'NaN', 'Inf'
 *
 * @author pablocingolani
 */
public class FieldConstant extends Field {

	public enum FieldConstantNames {
		NaN, Inf;

		public VcfInfoType getType() {
			// So far these constants are Float
			return VcfInfoType.Float;
		}
	};

	/**
	 * Create fields from constant names
	 */
	public static Field factory(String name) {
		if (!isConstantField(name)) throw new RuntimeException("Unknown constant name '" + name + "'");

		FieldConstantNames fcn = FieldConstantNames.valueOf(name);
		switch (fcn) {
		case NaN:
			return new FieldConstantFloat("NaN", Double.NaN);

		case Inf:
			return new FieldConstantFloat("Inf", Double.POSITIVE_INFINITY);

		default:
			throw new RuntimeException("Unknown constant name '" + fcn + "'");
		}
	}

	/**
	 * Is this string a constant's name?
	 */
	public static boolean isConstantField(String name) {
		for (FieldConstantNames fcn : FieldConstantNames.values())
			if (name.equals(fcn.toString())) return true;

		return false;

	}

	public FieldConstant(String name) {
		super(name);
	}

}
