package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Condition;

/**
 * A function that returns a bool type (i.e. true or false)
 * 
 * @author pablocingolani
 */
public abstract class FunctionBool extends Condition {

	public FunctionBool(String functionName) {
		super(functionName);
	}

}
