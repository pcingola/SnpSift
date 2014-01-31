package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Or expression
 * 
 * @author pcingola
 */
public class Or extends ConditionBinary {

	public Or(Condition left, Condition right) {
		super(left, right, "|");
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		return left.eval(vcfEntry) || right.eval(vcfEntry);
	}

}
