package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;

/**
 * Less or equal than
 *
 * @author pcingola
 */
public class Le extends ExpressionBinary {

	public Le(Expression left, Expression right) {
		super(left, right, "<=");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		// Cannot be compared? Return false
		if (!lval.canCompare(rval)) return Value.FALSE;

		boolean retVal = (lval.compareTo(rval) <= 0);
		return new Value(negated ? !retVal : retVal);
	}

}
