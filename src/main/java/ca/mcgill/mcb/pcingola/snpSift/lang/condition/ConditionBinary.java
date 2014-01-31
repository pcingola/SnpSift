package ca.mcgill.mcb.pcingola.snpSift.lang.condition;

/**
 * Binary condition
 * 
 * @author pcingola
 */
public abstract class ConditionBinary extends Condition {

	protected Condition left;
	protected Condition right;

	public ConditionBinary(Condition left, Condition right, String operator) {
		super(operator);
		this.right = right;
		this.left = left;
	}

	@Override
	public String toString() {
		return "( " + left + " " + operator + " " + right + " )";
	}

}
