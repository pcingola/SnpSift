package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpsift.lang.Value;

/**
 * Or expression
 *
 * @author pcingola
 */
public class Or extends ExpressionBinary {

	public Or(Expression left, Expression right) {
		super(left, right, "|");
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		Value lval = left.eval(vcfEntry);

		// Boolean? Try short-circuit operator
		if (lval.isBool()) {
			if (lval.asBool()) return Value.TRUE;
		}

		Value rval = right != null ? right.eval(vcfEntry) : null;

		return evalOp(lval, rval);
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (lval.isBool() && rval.isBool()) return new Value(lval.asBool() || rval.asBool());
		if (lval.canBeInt() && rval.canBeInt()) return new Value(lval.asInt() | rval.asInt());
		throw new RuntimeException("Cannot peroform '|' between " + lval.type() + " and " + rval.type());
	}
}
