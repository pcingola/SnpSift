package org.snpsift.snpSift.lang.expression;

import org.snpsift.snpSift.lang.Value;

/**
 * Greater equal
 *
 * @author pcingola
 */
public class Ge extends ExpressionBinary {

	public Ge(Expression left, Expression right) {
		super(left, right, ">=");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		// Cannot be compared? Return false
		if (!lval.canCompare(rval)) return Value.FALSE;

		boolean retVal = (lval.compareTo(rval) >= 0);
		return new Value(negated ? !retVal : retVal);
	}

}
