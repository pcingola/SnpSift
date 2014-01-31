package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * An expression that can be negated
 * 
 * @author pcingola
 */
public abstract class OpBinary extends Condition {

	protected Expression left;
	protected Expression right;

	public OpBinary(Expression left, Expression right, String operator) {
		super(operator);
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	@Override
	public String toString() {
		return "( " + left + " " + operator + " " + right + " )";
	}

}
