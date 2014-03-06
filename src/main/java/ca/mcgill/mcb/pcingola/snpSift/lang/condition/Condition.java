package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
	 * @param vcfEntry
	 * @return
	 */
	public abstract boolean eval(VcfEntry vcfEntry);

}
