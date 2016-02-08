package org.snpsift.snpSift.lang.expression;

import org.snpsift.snpSift.lang.Value;

/**
 * And expression
 *
 * @author pcingola
 */
public class Minus extends ExpressionBinary {

	public Minus(Expression left) {
		super(left, null, "-");
	}

	public Minus(Expression left, Expression right) {
		super(left, right, "-");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (rval != null) {
			if (lval.isFloat() || rval.isFloat()) return new Value(lval.asFloat() - rval.asFloat());
			if (lval.isInt() && rval.isInt()) return new Value(lval.asInt() - rval.asInt());
			throw new RuntimeException("Cannot peroform '-' between " + lval.type() + " and " + rval.type());
		}

		if (lval.isFloat()) return new Value(-lval.asFloat());
		if (lval.isInt()) return new Value(-lval.asInt());
		throw new RuntimeException("Cannot peroform '-' between on " + lval.type());
	}

}
