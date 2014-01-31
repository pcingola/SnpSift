package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * Match a regular expression (string)
 * 
 * @author pcingola
 */
public class NotMatch extends Match {

	public NotMatch(Expression left, Expression right) {
		super(left, right);
		operator = "~!";
		negated = true;
	}
}
