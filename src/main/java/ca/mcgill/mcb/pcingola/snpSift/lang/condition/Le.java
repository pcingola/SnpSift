package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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

}
