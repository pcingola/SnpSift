package ca.mcgill.mcb.pcingola.snpSift.lang.expression;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * A 'constant' field: e.g. 'NaN', 'Inf'
 * 
 * @author pablocingolani
 */
public class FieldConstantFloat extends FieldConstant {

	double value;

	public FieldConstantFloat(String name, double value) {
		super(name);
		this.value = value;
		returnType = VcfInfoType.Float;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable get(VcfEntry vcfEntry) {
		return value;
	}

	@Override
	public Double getFieldFloat(VcfEntry vcfEntry) {
		return value;
	}

}
