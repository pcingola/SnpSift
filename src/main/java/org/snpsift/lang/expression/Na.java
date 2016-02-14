package org.snpsift.lang.expression;


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
