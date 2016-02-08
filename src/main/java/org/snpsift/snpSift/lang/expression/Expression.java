package org.snpsift.snpSift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;

/**
 * A generic expresion
 * Expressions have values (VcfInfoType)
 *
 * @author pcingola
 */
public abstract class Expression {

	public static boolean debug = false;

	protected boolean negated;
	protected String operator;

	public Expression() {
		operator = "";
		negated = false;
	}

	public Expression(String operator) {
		this.operator = operator;
		negated = false;
	}

	/**
	 * Evaluate expression using VcfEntry
	 */
	public abstract Value eval(VcfEntry vcfEntry);

	/**
	 * Evaluate expression using genotype
	 */
	public abstract Value eval(VcfGenotype vcfGenotype);

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

}
