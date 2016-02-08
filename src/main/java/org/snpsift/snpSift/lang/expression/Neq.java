package org.snpsift.snpSift.lang.expression;


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
