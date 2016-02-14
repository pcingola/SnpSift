package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;

public class Literal extends Expression {

	Value value;

	public Literal(Boolean b) {
		value = new Value(b);
	}

	public Literal(Double d) {
		value = new Value(d);
	}

	public Literal(long l) {
		value = new Value(l);
	}

	public Literal(String str) {
		value = new Value(str);
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		return value;
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
