package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;

/**
 * And expression
 *
 * @author pcingola
 */
public class Times extends ExpressionBinary {

	public Times(Expression left, Expression right) {
		super(left, right, "*");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (lval.isFloat() || rval.isFloat()) return new Value(lval.asFloat() * rval.asFloat());
		if (lval.isInt() && rval.isInt()) return new Value(lval.asInt() * rval.asInt());
		throw new RuntimeException("Cannot peroform '*' between " + lval.type() + " and " + rval.type());
	}

}
