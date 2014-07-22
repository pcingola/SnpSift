package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Exists operator (true if a field exists)
 *
 * @author pcingola
 */
public class Exists extends OpUnary {

	public Exists(Expression expr) {
		super(expr, "exists");
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		boolean retVal = true;

		try {
			if( expr.getReturnType(vcfEntry) == VcfInfoType.Flag) {
				retVal = expr.getFlag(vcfEntry);
			} else {
				String value = expr.getString(vcfEntry);
				retVal = (value != null) // Not null?
						&& (!value.isEmpty()) // Not empty
						&& (!value.equals(".")) // Not missing?
						;
			}
		} catch (Throwable t) {
			// Exception while trying to find it? => false
			if (debug) t.printStackTrace();
			retVal = false;
		}

		return negated ? !retVal : retVal;
	}

	@Override
	public boolean eval(VcfGenotype vcfGenotype) {
		boolean retVal = true;

		try {
			if( expr.getReturnType(vcfGenotype) == VcfInfoType.Flag) {
				retVal = expr.getFlag(vcfGenotype);
			} else {
				String value = expr.getString(vcfGenotype);
				retVal = (value != null) // Not null?
						&& (!value.isEmpty()) // Not empty
						&& (!value.equals(".")) // Not missing?
						;
			}
		} catch (Throwable t) {
			// Exception while trying to find it? => false
			if (debug) t.printStackTrace();
			retVal = false;
		}

		return negated ? !retVal : retVal;
	}

}
