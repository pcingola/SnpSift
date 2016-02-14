package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.lang.Value;

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
