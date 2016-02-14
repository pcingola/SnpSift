package org.snpsift.lang.expression;

import org.snpsift.lang.Value;

/**
 * And expression
 *
 * @author pcingola
 */
public class Plus extends ExpressionBinary {

	public Plus(Expression left) {
		super(left, null, "+");
	}

	public Plus(Expression left, Expression right) {
		super(left, right, "+");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (rval != null) {
			if (lval.isFloat() || rval.isFloat()) return new Value(lval.asFloat() + rval.asFloat());
			if (lval.isInt() && rval.isInt()) return new Value(lval.asInt() + rval.asInt());
			return new Value(lval.asString() + rval.asString());
		}

		// Unary plus
		return lval;
	}

}
