package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Not expression
 * 
 * @author pcingola
 */
public class Not extends ConditionUnary {

	public Not(Condition condition) {
		super(condition, "!");
	}

	public boolean eval(VcfEntry vcfEntry) {
		return !condition.eval(vcfEntry);
	}

	@Override
	public String toString() {
		return "! ( " + condition + " )";
	}

}
