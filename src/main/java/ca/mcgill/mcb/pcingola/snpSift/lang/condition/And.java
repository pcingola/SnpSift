package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

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

	@Override
	public boolean eval(VcfGenotype gt) {
		return left.eval(gt) && right.eval(gt);
	}

}
