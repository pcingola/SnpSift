package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Unary condition
 * 
 * @author pcingola
 */
public class ConditionUnary extends Condition {

	Condition condition;

	public ConditionUnary(Condition condition, String operator) {
		super(operator);
		this.condition = condition;
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		return !condition.eval(vcfEntry);
	}

	@Override
	public String toString() {
		return "! ( " + condition + " )";
	}

}
