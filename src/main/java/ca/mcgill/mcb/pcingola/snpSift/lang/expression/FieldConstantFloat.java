package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * A 'constant' field: e.g. 'NaN', 'Inf'
 *
 * @author pablocingolani
 */
public class FieldConstantFloat extends FieldConstant {

	Value value;

	public FieldConstantFloat(String name, double value) {
		super(name);
		this.value = new Value(value);
		returnType = VcfInfoType.Float;
	}

	@Override
	public Value eval(VcfEntry vcfEntry) {
		return value;
	}

	@Override
	public Double getFieldFloat(VcfEntry vcfEntry) {
		return value.asFloat();
	}

}
