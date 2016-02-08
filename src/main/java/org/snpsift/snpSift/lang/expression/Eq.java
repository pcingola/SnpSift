package org.snpsift.snpSift.lang.expression;

import org.snpsift.snpSift.lang.Value;

/**
 * Equal
 *
 * @author pcingola
 */
public class Eq extends ExpressionBinary {

	public Eq(Expression left, Expression right) {
		super(left, right, "==");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		// Cannot be compared? Return false
		if (!lval.canCompare(rval)) return Value.FALSE;

		boolean eq = lval.equals(rval);
		return new Value(negated ? !eq : eq);
	}
}
