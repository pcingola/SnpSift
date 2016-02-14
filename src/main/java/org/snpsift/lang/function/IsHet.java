package org.snpsift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.Expression;

/**
 * Is 'genotypeNum' heterozygous?
 *
 * @author pablocingolani
 */
public class IsHet extends FunctionBoolGenotype {

	public IsHet(Expression exprIdx) {
		super("isHet", exprIdx);
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		VcfGenotype gen = evalGenotype(vcfEntry);
		return gen.isHeterozygous() ? Value.TRUE : Value.FALSE;
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isHeterozygous() ? Value.TRUE : Value.FALSE;
	}

}
