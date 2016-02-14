package org.snpsift.lang.expression;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.FieldIterator.IteratorType;

/**
 * A field that has sub fields (e.g. comma separated list of parameters):
 * E.g.:  'AF1[2]'
 *
 * @author pablocingolani
 */
public class FieldSub extends Field {

	Expression indexExpr;

	public FieldSub(String name, Expression indexExpr) {
		super(name);
		this.indexExpr = indexExpr;
	}

	/**
	 * Evaluate index expression
	 */
	protected int evalIndex(VcfEntry vcfEntry) {
		return evalIndex(vcfEntry, indexExpr);
	}

	/**
	 * Evaluate index expression (VCF entry)
	 */
	protected int evalIndex(VcfEntry vcfEntry, Expression idxExpr) {
		// Find index value
		Value idxVal = idxExpr.eval(vcfEntry);
		int index = (int) (idxVal.isString() ? parseIndexField(idxVal.asString()) : idxVal.asInt());
		return index;
	}

	/**
	 * Evaluate index expression (VCF genotype)
	 */
	protected int evalIndex(VcfGenotype vcfGenotype) {
		return evalIndex(vcfGenotype, indexExpr);
	}

	/**
	 * Evaluate index expression (VCF genotype)
	 */
	protected int evalIndex(VcfGenotype vcfGenotype, Expression idxExpr) {
		// Find index value
		Value idxVal = idxExpr.eval(vcfGenotype);
		int index = (int) (idxVal.isString() ? parseIndexField(idxVal.asString()) : idxVal.asInt());
		return index;
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfEntry vcfEntry) {
		String value = super.getFieldString(vcfEntry);

		// Can this be split?
		if (value == null) return (String) fieldNotFound(vcfEntry);
		String sub[] = value.split(",");

		// Find index value
		int index = evalIndex(vcfEntry);

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	/**
	 * Get a field from VcfEntry
	 */
	@Override
	public String getFieldString(VcfGenotype vcfGenotype) {
		String value = super.getFieldString(vcfGenotype);

		// Can this be split?
		if (value == null) return (String) gtFieldNotFound(vcfGenotype);
		String sub[] = value.split(",");

		// Find index value
		Value idxVal = indexExpr.eval(vcfGenotype);
		int index = (int) (idxVal.isString() ? parseIndexField(idxVal.asString()) : idxVal.asInt());

		// Is this field 'iterable'?
		int idx = index;
		if (index < 0) {
			FieldIterator.get().setMax(IteratorType.GENOTYPE_VAR, sub.length - 1);
			FieldIterator.get().setType(index);
			idx = FieldIterator.get().get(IteratorType.VAR);
		}

		if (sub.length <= idx) return "";
		return sub[idx];
	}

	@Override
	protected boolean isSub() {
		return true;
	}

	@Override
	public String toString() {
		return name + "[" + indexExpr + "]";
	}
}
