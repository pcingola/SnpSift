package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

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
