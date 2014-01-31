package ca.mcgill.mcb.pcingola.snpSift.lang.function;

/**
 * A function that returns a bool type (i.e. true or false). 
 * The function is evaluated on a genotype
 * 
 * @author pablocingolani
 */
public abstract class FunctionBoolGenotype extends FunctionBool {

	int genotypeNum;

	public FunctionBoolGenotype(String functionName, int genotypeNum) {
		super(functionName);
		this.genotypeNum = genotypeNum;
	}

	@Override
	public String toString() {
		return operator + "( GEN[" + genotypeNum + "] )";
	}

}
