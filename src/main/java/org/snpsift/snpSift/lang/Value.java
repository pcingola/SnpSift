package org.snpsift.snpSift.lang;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfInfoType;

public class Value {

	public static boolean debug = false;

	public static final Value FALSE = new Value(false);
	public static final Value TRUE = new Value(true);
	public static final Value NULL = new Value(null);

	final Object value;

	public Value(Object value) {
		this.value = value;
		sanityCheck();
	}

	public boolean asBool() {
		if (value == null) return false;
		if (isBool()) return (Boolean) value;
		if (isInt()) return (asInt() != 0L);
		if (isFloat()) return (asFloat() != 0.0);
		return !asString().isEmpty();
	}

	public double asFloat() {
		if (value == null) return 0.0;
		if (isBool()) return asBool() ? 1.0 : 0.0;
		if (isInt()) return asInt();
		if (isFloat()) return (Double) value;
		return Gpr.parseDoubleSafe(value.toString());
	}

	public long asInt() {
		if (value == null) return 0L;
		if (isBool()) return asBool() ? 1L : 0L;
		if (isInt()) return (Long) value;
		if (isFloat()) return ((long) asFloat());
		return Gpr.parseIntSafe(value.toString());
	}

	public String asString() {
		if (value == null) return "";
		return value.toString();
	}

	public boolean canBeFloat() {
		return isFloat() || isInt() || isBool();
	}

	public boolean canBeInt() {
		return isInt() || isBool();
	}

	/**
	 * Can these values be compared?
	 */
	public boolean canCompare(Value val) {
		return !(isNull() || val.isNull());
	}

	public int compareTo(Value val) {
		// Compare null
		if (isNull() && val.isNull()) return 0;
		if (isNull() && !val.isNull()) return -1;
		if (!isNull() && val.isNull()) return 1;

		// One of them is float?
		if (canBeFloat() || val.canBeFloat()) return ((Double) asFloat()).compareTo(val.asFloat());
		if (canBeInt() || val.canBeInt()) return ((Long) asInt()).compareTo(val.asInt());
		if (isBool() || val.isBool()) return ((Boolean) asBool()).compareTo(val.asBool());
		return asString().compareTo(val.asString());
	}

	@Override
	public boolean equals(Object o) {
		if (value == o) return true;

		Value val = (Value) o;

		// Compare null
		if (isNull() && val.isNull()) return true;
		if (isNull() && !val.isNull()) return false;
		if (!isNull() && val.isNull()) return false;

		return value.equals(val.value);
	}

	@Override
	public int hashCode() {
		if (value == null) return 0;
		return value.hashCode();
	}

	public boolean isBool() {
		return value instanceof Boolean;
	}

	public boolean isFloat() {
		return value instanceof Double;
	}

	public boolean isInt() {
		return value instanceof Long || value instanceof Integer;
	}

	public boolean isNull() {
		return value == null;
	}

	public boolean isString() {
		return value instanceof String;
	}

	void sanityCheck() {
		if (value == null //
				|| value instanceof Boolean //
				|| value instanceof Long //
				|| value instanceof Double //
				|| value instanceof String //
		) return;

		throw new RuntimeException("Cannot assign value type '" + value.getClass().getSimpleName() + "'");
	}

	@Override
	public String toString() {
		if (debug) {
			if (isNull()) return "null";
			return "(" + type() + ")" + (isString() ? "'" + asString() + "'" : asString());
		}

		return asString();
	}

	public VcfInfoType type() {
		if (isBool()) return VcfInfoType.Flag;
		if (isInt()) return VcfInfoType.Integer;
		if (isFloat()) return VcfInfoType.Float;
		return VcfInfoType.String;
	}

}
