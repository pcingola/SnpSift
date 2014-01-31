package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * An expression that can be negated
 * 
 * @author pcingola
 */
public abstract class OpUnary extends Condition {

	protected Expression expr;

	public OpUnary(Expression expr, String operator) {
		super(operator);
		this.expr = expr;
		this.operator = operator;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	@Override
	public String toString() {
		return "( " + operator + " " + expr + " )";
	}

}
