package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;

/**
 * A function that returns an expression (i.e. a value with a type)
 *
 * @author pablocingolani
 */
public abstract class Function extends Expression {

	String functionName;

	public Function(String functionName) {
		this.functionName = functionName;
	}

	@Override
	public String toString() {
		return functionName + "()";
	}
}
