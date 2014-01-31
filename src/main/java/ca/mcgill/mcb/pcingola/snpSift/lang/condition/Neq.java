package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * Not equal
 * 
 * @author pcingola
 */
public class Neq extends Eq {

	public Neq(Expression left, Expression right) {
		super(left, right);
		operator = "!=";
		negated = true;
	}
}
