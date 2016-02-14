package org.snpsift.lang.expression;

import org.snpsift.lang.Value;

/**
 * Exists operator (true if a field exists)
 *
 * @author pcingola
 */
public class Exists extends ExpressionUnary {

	public Exists(Expression expr) {
		super(expr, "exists");
	}

	@Override
	protected Value evalOp(Value val) {
		if (val.isBool()) return val; // A 'flag' only exists (i.e. is present) in the VCF INFO field when it's set to 'true'

		boolean ret = !val.asString().isEmpty();
		return new Value(negated ? !ret : ret);
	}
}
