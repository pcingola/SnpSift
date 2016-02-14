package org.snpsift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.Expression;

/**
 * A function that returns a bool type (i.e. true or false).
 * The function is evaluated on a genotype
 *
 * @author pablocingolani
 */
public abstract class FunctionBoolGenotype extends Function {

	// int genotypeNum;
	Expression exprIdx;

	public FunctionBoolGenotype(String functionName, Expression exprIdx) {
		super(functionName);
		this.exprIdx = exprIdx;
	}

	/**
	 * Evaluate index expression and obtein genotype
	 */
	protected VcfGenotype evalGenotype(VcfEntry vcfEntry) {
		Value val = exprIdx.eval(vcfEntry);
		int idx = (int) val.asInt();
		VcfGenotype gen = vcfEntry.getVcfGenotype(idx);
		return gen;
	}

	@Override
	public String toString() {
		return operator + "( GEN[" + exprIdx + "] )";
	}

}
