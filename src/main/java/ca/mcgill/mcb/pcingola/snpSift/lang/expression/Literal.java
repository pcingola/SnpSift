package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

public class Literal extends Expression {

	String str;
	double d = Double.NaN;
	long l = 0;

	public Literal(String str) {
		this.str = str;
		returnType = VcfInfoType.String;
	}

	public Literal(String str, boolean asNumber) {
		this.str = str;
		try {
			returnType = VcfInfoType.Integer;
			l = Long.parseLong(str);
			d = l;
		} catch (Exception e) {
			returnType = VcfInfoType.Float;
			d = Double.parseDouble(str);
			l = (long) d;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		switch (returnType) {
		case Integer:
			return l;
		case Float:
			return d;
		case String:
			return str;
		default:
			throw new RuntimeException("Unknwon return type '" + returnType + "'");
		}
	}

	public String getStr() {
		switch (returnType) {
		case String:
			return str;
		case Float:
			return "" + d;
		case Integer:
			return "" + l;
		default:
			throw new RuntimeException("Unknown type '" + returnType + "'");
		}

	}

	@Override
	public String toString() {
		switch (returnType) {
		case String:
			return "'" + str + "'";
		case Float:
			return "" + d;
		case Integer:
			return "" + l;
		default:
			throw new RuntimeException("Unknown type '" + returnType + "'");
		}
	}
}
