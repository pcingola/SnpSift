package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;

/**
 * And expression
 *
 * @author pcingola
 */
public class Mod extends ExpressionBinary {

	public Mod(Expression left, Expression right) {
		super(left, right, "%");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (lval.isInt() && rval.isInt()) return new Value(lval.asInt() % rval.asInt());
		throw new RuntimeException("Cannot peroform '%' between " + lval.type() + " and " + rval.type());
	}

}
