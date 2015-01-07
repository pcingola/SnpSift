package ca.mcgill.mcb.pcingola.snpSift.lang;

import java.util.ArrayList;
import java.util.HashSet;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftLexer;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.CompilationUnitContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionCompContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionExistsContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionLogicContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionParenContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionPlusContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionSetContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionTimesContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.ExpressionUnaryContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.FunctionCallContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.LiteralBoolContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.LiteralFloatContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.LiteralIndexContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.LiteralIntContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.LiteralStringContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.VarReferenceContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.VarReferenceListContext;
import ca.mcgill.mcb.pcingola.snpSift.antlr.SnpSiftParser.VarReferenceListSubContext;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.And;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Div;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Eq;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Exists;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Field;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldConstant;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldEff;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldGenotype;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldGenotypeSub;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldLof;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldNmd;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldSub;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Ge;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Gt;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Le;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Literal;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Lt;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Match;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Minus;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Mod;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Na;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Neq;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Not;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.NotMatch;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Or;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Plus;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Times;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Xor;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountHet;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountHom;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountRef;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountVariant;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.Function;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.FunctionBoolGenotype;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.In;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsHet;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsHom;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsRef;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsVariant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.EffFormatVersion;

/**
 * Creates objects from an AST
 *
 * @author pcingola
 */
public class LangFactory {

	protected static boolean debug = false;

	ArrayList<HashSet<String>> sets = new ArrayList<HashSet<String>>();
	EffFormatVersion formatVersion;
	boolean exceptionIfNotFound = false;

	public LangFactory() {
		sets = new ArrayList<HashSet<String>>(); // No sets
	}

	public LangFactory(ArrayList<HashSet<String>> sets, EffFormatVersion formatVersion, boolean exceptionIfNotFound) {
		this.sets = sets;
		this.formatVersion = formatVersion;
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	/**
	 * Create an AST from a program (using ANTLR lexer & parser)
	 * Returns null if error
	 * Use 'alreadyIncluded' to keep track of from 'include' statements
	 */
	public Expression compile(String expression) {
		if (debug) Gpr.debug("Creating AST: " + expression);

		SnpSiftLexer lexer = null;
		SnpSiftParser parser = null;
		ParseTree tree = null;

		try {
			// Create a CharStream that reads from standard input
			ANTLRInputStream input = new ANTLRInputStream(expression);

			// Create a lexer that feeds off of input CharStream
			lexer = new SnpSiftLexer(input) {
				@Override
				public void recover(LexerNoViableAltException e) {
					throw new RuntimeException(e); // Bail out
				}
			};

			CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new SnpSiftParser(tokens);
			// parser.setErrorHandler(new CompileErrorStrategy()); // Bail out with exception if errors in parser

			tree = parser.compilationUnit(); // Begin parsing at main rule
			if (tree == null) return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Done parsing tree. Create expression
		if (debug) Gpr.debug("Tree: " + tree.toStringTree());
		return expressionFactory(tree);
	}

	/**
	 * Create 'Expressions' from Tree
	 */
	@SuppressWarnings("rawtypes")
	public Expression expressionFactory(ParseTree tree) {
		Class leaveClass = tree.getClass();
		String leaveName = leaveClass.getSimpleName(); // tree.getText();

		if (debug) {
			Gpr.debug("\n\tLeaveClassName : " + leaveName //
					+ "\n\tTxt            : " + tree.getText() //
					+ "\n\tTree           : " + tree.toStringTree() //
			);
			for (int i = 0; i < tree.getChildCount(); i++)
				System.err.println("\t\tChild[" + i + "] : " + tree.getChild(i).getText() + "\t\t" + tree.getChild(i).getClass().getSimpleName());
		}

		Expression expression = null;

		if (leaveClass == CompilationUnitContext.class) {
			expression = expressionFactory(tree.getChild(0));
		} else if (leaveClass == ExpressionParenContext.class) {
			expression = expressionFactory(tree.getChild(1));
		} else if (leaveClass == ExpressionLogicContext.class) { // Logical operators
			String op = ((ExpressionLogicContext) tree).op.getText();
			if (debug) Gpr.debug("Logic operator: '" + op + "'");

			// Parse expressions
			Expression l = expressionFactory(tree.getChild(0));
			Expression r = expressionFactory(tree.getChild(2));

			if (op.equals("&") || op.equals("&&")) expression = new And(l, r);
			else if (op.equals("|") || op.equals("||")) expression = new Or(l, r);
			else if (op.equals("^") || op.equals("~")) expression = new Xor(l, r);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionTimesContext.class) { // Multiplication operators
			String op = ((ExpressionTimesContext) tree).op.getText();
			if (debug) Gpr.debug("Multiplication operator: '" + op + "'");

			Expression left = expressionFactory(tree.getChild(0));
			Expression right = expressionFactory(tree.getChild(2));

			if (op.equals("*")) expression = new Times(left, right);
			else if (op.equals("/")) expression = new Div(left, right);
			else if (op.equals("%")) expression = new Mod(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionPlusContext.class) { // Addition operators
			String op = ((ExpressionPlusContext) tree).op.getText();
			if (debug) Gpr.debug("Plus operator: '" + op + "'");

			Expression left = expressionFactory(tree.getChild(0));
			Expression right = expressionFactory(tree.getChild(2));

			if (op.equals("+")) expression = new Plus(left, right);
			else if (op.equals("-")) expression = new Minus(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionCompContext.class) { // Comparison operators
			String op = ((ExpressionCompContext) tree).op.getText();
			if (debug) Gpr.debug("Compare operator: '" + op + "'");

			Expression left = expressionFactory(tree.getChild(0));
			Expression right = expressionFactory(tree.getChild(2));

			if (op.equals("<")) expression = new Lt(left, right);
			else if (op.equals("<=")) expression = new Le(left, right);
			else if (op.equals(">")) expression = new Gt(left, right);
			else if (op.equals(">=")) expression = new Ge(left, right);
			else if (op.equals("=") || op.equals("==")) expression = new Eq(left, right);
			else if (op.equals("!=")) expression = new Neq(left, right);
			else if (op.equals("=~")) expression = new Match(left, right);
			else if (op.equals("!~")) expression = new NotMatch(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionExistsContext.class) { // Exists operators
			String op = ((ExpressionExistsContext) tree).op.getText();
			Expression expr = expressionFactory(tree.getChild(1));

			if (op.equalsIgnoreCase("exists")) expression = new Exists(expr);
			else if (op.equalsIgnoreCase("na")) expression = new Na(expr);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionUnaryContext.class) { // Unary operators
			String op = ((ExpressionUnaryContext) tree).op.getText();
			if (debug) Gpr.debug("Unary operator: '" + op + "'");

			Expression expr = expressionFactory(tree.getChild(1));

			if (op.equals("!")) expression = new Not(expr);
			else if (op.equals("+")) expression = new Plus(expr);
			else if (op.equals("-")) expression = new Minus(expr);
			else throw new RuntimeException("Unknown operator '" + leaveName + "'");

		} else if (leaveClass == VarReferenceContext.class // All fields are preocessed here
				|| leaveClass == VarReferenceListContext.class //
				|| leaveClass == VarReferenceListSubContext.class //
		) {
			// Is it a field?
			expression = fieldFactory(tree);
		} else if (leaveClass == LiteralBoolContext.class) { // Literals
			String str = tree.getChild(0).getText();
			expression = new Literal(Gpr.parseBoolSafe(str));
		} else if (leaveClass == LiteralIntContext.class) {
			String str = tree.getChild(0).getText();
			expression = new Literal(Gpr.parseLongSafe(str));
		} else if (leaveClass == LiteralFloatContext.class) {
			String str = tree.getChild(0).getText();
			expression = new Literal(Gpr.parseDoubleSafe(str));
		} else if (leaveClass == LiteralStringContext.class) {
			String str = tree.getChild(0).getText();
			expression = new Literal(str);
		} else if (leaveClass == FunctionCallContext.class) { // Functions
			expression = functionFactory(tree);
		} else if (leaveClass == ExpressionSetContext.class) { // 'in SET' operations
			expression = expressionSetFactory(tree);
		} else if (leaveClass == LiteralIndexContext.class) {
			String txt = tree.getText();
			expression = new Literal(txt);
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("Expression: " + expression);
		return expression;
	}

	/**
	 * Create FunctionBoolSet from AST
	 */
	@SuppressWarnings("rawtypes")
	public Function expressionSetFactory(ParseTree tree) {
		if (debug) {
			Class leaveClass = tree.getClass();
			String leaveName = leaveClass.getSimpleName(); // tree.getText();

			Gpr.debug("\n\tLeaveClassName : " + leaveName //
					+ "\n\tTxt            : " + tree.getText() //
					+ "\n\tTree           : " + tree.toStringTree() //
			);
			for (int i = 0; i < tree.getChildCount(); i++)
				System.err.println("\t\tChild[" + i + "] : " + tree.getChild(i).getText() + "\t\t" + tree.getChild(i).getClass().getSimpleName());
		}

		// Expression
		Expression expr = expressionFactory(tree.getChild(0));

		// Set index
		Expression exprSetIdx = expressionFactory(tree.getChild(4));
		Function fun = null;

		fun = new In(sets, expr, exprSetIdx);
		return fun;
	}

	/**
	 * Create 'Expression' from Tree
	 */
	@SuppressWarnings("rawtypes")
	public Field fieldFactory(ParseTree tree) {
		Class leaveClass = tree.getClass();
		String leaveName = leaveClass.getSimpleName(); // tree.getText();

		if (debug) {
			Gpr.debug("\n\tLeaveClassName : " + leaveName //
					+ "\n\tTxt            : " + tree.getText() //
					+ "\n\tTree           : " + tree.toStringTree() //
			);
			for (int i = 0; i < tree.getChildCount(); i++)
				System.err.println("\t\tChild[" + i + "] : " + tree.getChild(i).getText() + "\t\t" + tree.getChild(i).getClass().getSimpleName());
		}

		Field field = null;

		if (leaveClass == VarReferenceContext.class) {
			String name = tree.getChild(0).getText();
			if (FieldConstant.isConstantField(name)) field = FieldConstant.factory(name);
			else field = new Field(name);
		} else if (leaveClass == VarReferenceListContext.class) {
			String name = tree.getChild(0).getText();
			Expression idxExpr = expressionFactory(tree.getChild(2));

			if (name.equalsIgnoreCase("gen")) field = new FieldGenotype(null, idxExpr);
			else if (name.equalsIgnoreCase("ann") || name.equalsIgnoreCase("eff")) field = new FieldEff(null, idxExpr, formatVersion);
			else if (name.equalsIgnoreCase("lof")) field = new FieldLof(null, idxExpr);
			else if (name.equalsIgnoreCase("nmd")) field = new FieldNmd(null, idxExpr);
			else field = new FieldSub(name, idxExpr);
		} else if (leaveClass == VarReferenceListSubContext.class) {
			String name = tree.getChild(0).getText();
			Expression idxExpr = expressionFactory(tree.getChild(2));

			// Subtree
			ParseTree subTree = tree.getChild(4);
			String subName = subTree.getChild(0).getText();

			if (tree.getChild(4).getClass() == VarReferenceListContext.class) {
				// Element is also an array/list? => This is a "List.Sub.Sub" field
				Expression idxExpr2 = expressionFactory(subTree.getChild(2));
				field = new FieldGenotypeSub(subName, idxExpr, idxExpr2);
			} else {
				// This is a 'normal' list.sub field
				if (name.equalsIgnoreCase("gen")) field = new FieldGenotype(subName, idxExpr);
				else if (name.equalsIgnoreCase("ann") || name.equalsIgnoreCase("eff")) field = new FieldEff(subName, idxExpr, formatVersion);
				else if (name.equalsIgnoreCase("lof")) field = new FieldLof(subName, idxExpr);
				else if (name.equalsIgnoreCase("nmd")) field = new FieldNmd(subName, idxExpr);
				else throw new RuntimeException("Could not create field '" + tree.getText() + "'");
			}
		} else throw new RuntimeException("Unknown field '" + leaveName + "'");

		field.setExceptionIfNotFound(exceptionIfNotFound);
		return field;
	}

	/**
	 * Create FunctionBoolGenotype from AST
	 */
	public FunctionBoolGenotype functionBoolGenotypeFactory(ParseTree tree) {
		String leaveName = tree.getChild(0).getText();

		if (debug) {
			Gpr.debug("\n\tLeaveClassName : " + leaveName //
					+ "\n\tTxt            : " + tree.getText() //
					+ "\n\tTree           : " + tree.toStringTree() //
			);
			for (int i = 0; i < tree.getChildCount(); i++)
				System.err.println("\t\tChild[" + i + "] : " + tree.getChild(i).getText());
		}

		FunctionBoolGenotype fun = null;

		// Gneotype index is in subtree
		ParseTree subTree = tree.getChild(2);
		if (!subTree.getChild(0).getText().equalsIgnoreCase("GEN")) throw new RuntimeException("Function '" + leaveName + "' only acceps a genotype as index");
		Expression exprGenIdx = expressionFactory(subTree.getChild(2));

		if (leaveName.equalsIgnoreCase("isHom")) {
			fun = new IsHom(exprGenIdx);
		} else if (leaveName.equalsIgnoreCase("isHet")) {
			fun = new IsHet(exprGenIdx);
		} else if (leaveName.equalsIgnoreCase("isRef")) {
			fun = new IsRef(exprGenIdx);
		} else if (leaveName.equalsIgnoreCase("isVariant")) {
			fun = new IsVariant(exprGenIdx);
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		return fun;
	}

	/**
	 * Create function
	 */
	public Function functionFactory(ParseTree tree) {
		String leaveName = tree.getChild(0).getText();

		// Genotype-based functions
		if (leaveName.equalsIgnoreCase("isHom") //
				|| leaveName.equalsIgnoreCase("isHet") //
				|| leaveName.equalsIgnoreCase("isRef") //
				|| leaveName.equalsIgnoreCase("isVariant") //
		) {
			return functionBoolGenotypeFactory(tree);
		} else if (leaveName.equalsIgnoreCase("countHom") //
				|| leaveName.equalsIgnoreCase("countHet") //
				|| leaveName.equalsIgnoreCase("countRef") //
				|| leaveName.equalsIgnoreCase("countVariant") //
		) {
			return functionVcfEntryFactory(tree);
		} else {
			throw new RuntimeException("Unknown function '" + leaveName + "'");
		}
	}

	/**
	 * Create Functions from tree
	 * Note: These functions that are calculated on the whole VcfEntry.
	 */
	public Function functionVcfEntryFactory(ParseTree tree) {
		String leaveName = tree.getChild(0).getText();

		if (debug) {
			Gpr.debug("\n\tLeaveClassName : " + leaveName //
					+ "\n\tTxt            : " + tree.getText() //
					+ "\n\tTree           : " + tree.toStringTree() //
			);
			for (int i = 0; i < tree.getChildCount(); i++)
				System.err.println("\t\tChild[" + i + "] : " + tree.getChild(i).getText());
		}

		Function func = null;

		if (leaveName.equalsIgnoreCase("countHom")) {
			func = new CountHom();
		} else if (leaveName.equalsIgnoreCase("countHet")) {
			func = new CountHet();
		} else if (leaveName.equalsIgnoreCase("countRef")) {
			func = new CountRef();
		} else if (leaveName.equalsIgnoreCase("countVariant")) {
			func = new CountVariant();
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression: " + func);
		return func;
	}

}
