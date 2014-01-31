package ca.mcgill.mcb.pcingola.snpSift.lang.function;

import java.util.HashSet;

import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Is an expression in a set?
 * 
 * @author pablocingolani
 */
public class In extends FunctionBool {

	int setNum;
	HashSet<String> set;
	Expression expression;

	public In(int setNum, HashSet<String> set, Expression expression) {
		super("in");
		this.set = set;
		this.expression = expression;
	}

	@Override
	public boolean eval(VcfEntry vcfEntry) {
		String res = expression.getString(vcfEntry);
		return set.contains(res);
	}

	@Override
	public String toString() {
		return expression + " " + operator + " SET[" + setNum + "]";
	}

}
