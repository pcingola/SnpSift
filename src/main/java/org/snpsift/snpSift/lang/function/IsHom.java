package org.snpsift.snpSift.lang.function;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.snpSift.lang.Value;
import org.snpsift.snpSift.lang.expression.Expression;

/**
 * Is 'genotypeNum' homozygous?
 *
 * @author pablocingolani
 */
public class IsHom extends FunctionBoolGenotype {

	public IsHom(Expression exprIdx) {
		super("isHom", exprIdx);
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		VcfGenotype gen = evalGenotype(vcfEntry);
		return gen.isHomozygous() ? Value.TRUE : Value.FALSE;
	}

	@Override
	public Value eval(VcfGenotype vcfGenotype) {
		return vcfGenotype.isHomozygous() ? Value.TRUE : Value.FALSE;
	}

}
