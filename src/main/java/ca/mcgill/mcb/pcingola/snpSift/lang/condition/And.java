package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * And expression
 * 
 * @author pcingola
 */
public class And extends ConditionBinary {

	public And(Condition left, Condition right) {
		super(left, right, "&");
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		return left.eval(vcfEntry) && right.eval(vcfEntry);
	}

}
