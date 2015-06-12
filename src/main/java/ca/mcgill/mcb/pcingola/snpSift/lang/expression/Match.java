package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;

/**
 * Match a regular expression (string)
 *
 * @author pcingola
 */
public class Match extends ExpressionBinary {

	public Match(Expression left, Expression right) {
		super(left, right, "~=");
	}

	@Override
	protected Value evalOp(Value lval, Value rval) {
		if (lval.isNull() || rval.isNull()) return Value.FALSE;

		String value = lval.asString();

		boolean retVal = false;

		if (value.isEmpty()) {
			// Empty doesn't match anything
			retVal = false;
		} else {
			String regexp = rval.asString();

			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(value);
			retVal = matcher.find();
		}

		return new Value(negated ? !retVal : retVal);
	}

}
