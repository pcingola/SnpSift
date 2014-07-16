package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * A condition has a boolean result.
 *
 * @author pcingola
 */
public abstract class Condition {

	public static boolean debug = false;

	protected boolean negated = false;
	protected String operator;

	public Condition(String operator) {
		this.operator = operator;
	}

	/**
	 * Evaluate this condition
	 */
	public abstract boolean eval(VcfEntry vcfEntry);

	/**
	 * Evaluate this condition
	 */
	public abstract boolean eval(VcfGenotype vcfGenotype);

}
