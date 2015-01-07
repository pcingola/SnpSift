package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

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
