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
public class IsRef extends FunctionBoolGenotype {

	public IsRef(Expression exprIdx) {
		super("isRef", exprIdx);
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		VcfGenotype gen = evalGenotype(vcfEntry);
		return !gen.isVariant() ? Value.TRUE : Value.FALSE;
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isRef() ? Value.TRUE : Value.FALSE;
	}

}
