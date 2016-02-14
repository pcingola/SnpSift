package org.snpsift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.Expression;

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
