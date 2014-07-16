package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Less or equal than
 *
 * @author pcingola
 */
public class Le extends OpBinary {

	public Le(Expression left, Expression right) {
		super(left, right, "<=");
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		if (!left.canCompareTo(right, vcfEntry)) return false;
		boolean retVal = (left.compareTo(right, vcfEntry) <= 0);
		return negated ? !retVal : retVal;
	}

	@Override
	public boolean eval(VcfGenotype vcfGenotype) {
		if (!left.canCompareTo(right, vcfGenotype)) return false;
		boolean retVal = (left.compareTo(right, vcfGenotype) <= 0);
		return negated ? !retVal : retVal;
	}

}
