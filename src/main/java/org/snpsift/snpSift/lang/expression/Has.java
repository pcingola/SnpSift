package org.snpsift.snpSift.lang.expression;

import org.snpsift.snpSift.lang.Value;

/**
 * Equal
 *
 * @author pcingola
 */
public class Has extends ExpressionBinary {

	public static final String SPLIT_REGEX = "[\\&\\+\\|,;:\\(\\)\\[\\]]";

	public Has(Expression left, Expression right) {
		super(left, right, "==");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		String lstr = lval.asString();
		String rstr = rval.asString();

		// Split right hand side
		String ls[] = lstr.split(SPLIT_REGEX);

		// Any of the split values is equal to left hand side?
		for (String l : ls)
			if (l.equals(rstr)) return Value.TRUE;

		return Value.FALSE;
	}
}
