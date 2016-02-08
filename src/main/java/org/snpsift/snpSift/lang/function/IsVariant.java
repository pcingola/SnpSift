package org.snpsift.snpSift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;
import org.snpsift.snpSift.lang.expression.Expression;

/**
 * Is 'genotypeNum' reference?
 *
 * @author pablocingolani
 */
public class IsVariant extends FunctionBoolGenotype {

	public IsVariant(Expression exprIdx) {
		super("isVariant", exprIdx);
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		VcfGenotype gen = evalGenotype(vcfEntry);
		return gen.isVariant() ? Value.TRUE : Value.FALSE;
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isVariant() ? Value.TRUE : Value.FALSE;
	}

}
