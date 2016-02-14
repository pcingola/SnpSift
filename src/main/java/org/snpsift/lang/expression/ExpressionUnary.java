package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;

/**
 * An expression that can be negated
 *
 * @author pcingola
 */
public abstract class ExpressionUnary extends Expression {

	protected Expression expr;

	public ExpressionUnary(Expression expr, String operator) {
		super(operator);
		this.expr = expr;
		this.operator = operator;
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		Value val = expr.eval(vcfEntry);
		return evalOp(val);
	}

	@Override
	public Value eval(VcfGenotype gt) {
		Value val = expr.eval(gt);
		return evalOp(val);
	}

	protected abstract Value evalOp(Value val);

	@Override
	public String toString() {
		return "( " + operator + " " + expr + " )";
	}

}
