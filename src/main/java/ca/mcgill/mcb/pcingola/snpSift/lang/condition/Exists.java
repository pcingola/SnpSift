package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

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
			String value = expr.get(vcfEntry).toString();

			retVal = (value != null) // Not null?
					&& (!value.isEmpty()) // Not empty
					&& (!value.equals(".")) // Not missing?
					;
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
			String value = expr.get(vcfGenotype).toString();

			retVal = (value != null) // Not null?
					&& (!value.isEmpty()) // Not empty
					&& (!value.equals(".")) // Not missing?
					;
		} catch (Throwable t) {
			// Exception while trying to find it? => false
			if (debug) t.printStackTrace();
			retVal = false;
		}

		return negated ? !retVal : retVal;
	}

}
