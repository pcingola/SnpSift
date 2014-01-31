package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Match a regular expression (string)
 * 
 * @author pcingola
 */
public class Match extends OpBinary {

	public Match(Expression left, Expression right) {
		super(left, right, "~=");
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		String value = left.get(vcfEntry).toString();

		boolean retVal = false;

		if (value.isEmpty()) {
			// Empty doesn't match anything
			retVal = false;
		} else {
			String regexp = right.get(vcfEntry).toString();

			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(value);
			retVal = matcher.find();
			// Gpr.debug("MATCH ( '" + value + "' , '" + regexp + "' ) : " + retVal);
		}

		return negated ? !retVal : retVal;
	}

}
