package org.snpsift.snpSift.lang.expression;

import org.snpsift.snpSift.lang.Value;

/**
 * And expression
 *
 * @author pcingola
 */
public class And extends ExpressionBinary {

	public And(Expression left, Expression right) {
		super(left, right, "&");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (lval.isBool() && rval.isBool()) return new Value(lval.asBool() && rval.asBool());
		if (lval.canBeInt() && rval.canBeInt()) return new Value(lval.asInt() & rval.asInt());
		throw new RuntimeException("Cannot peroform '&' between " + lval.type() + " and " + rval.type());
	}

}
