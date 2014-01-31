package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * A function that returns an expression (i.e. a value with a type)
 * 
 * @author pablocingolani
 */
public abstract class Function extends Expression {

	String functionName;

	public Function(String functionName, VcfInfoType returnType) {
		this.functionName = functionName;
		this.returnType = returnType;
	}

	public String toString() {
		return functionName + "()";
	}
}
