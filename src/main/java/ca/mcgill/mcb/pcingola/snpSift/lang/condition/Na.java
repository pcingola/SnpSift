package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * Exists operator (true if a field exists)
 * 
 * @author pcingola
 */
public class Na extends Exists {

	public Na(Expression expr) {
		super(expr);
		operator = "na";
		negated = true;
	}

}
