package ca.mcgill.mcb.pcingola.snpSift.lang.expression;


/**
 * Match a regular expression (string)
 * 
 * @author pcingola
 */
public class NotMatch extends Match {

	public NotMatch(Expression left, Expression right) {
		super(left, right);
		operator = "~!";
		negated = true;
	}
}
