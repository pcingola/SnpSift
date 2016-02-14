package org.snpsift.lang.expression;

import org.snpsift.lang.Value;

/**
 * Not expression
 *
 * @author pcingola
 */
public class Not extends ExpressionUnary {

	public Not(Expression expr) {
		super(expr, "!");
	}

	@Override
	public Value evalOp(Value val) {
		return new Value(!val.asBool());
	}

}
