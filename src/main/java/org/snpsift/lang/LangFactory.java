package org.snpsift.lang;

import java.util.ArrayList;
import java.util.HashSet;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.EffFormatVersion;
import org.snpsift.antlr.SnpSiftLexer;
import org.snpsift.antlr.SnpSiftParser;
import org.snpsift.antlr.SnpSiftParser.CompilationUnitContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionCompContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionExistsContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionLogicContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionParenContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionPlusContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionSetContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionTimesContext;
import org.snpsift.antlr.SnpSiftParser.ExpressionUnaryContext;
import org.snpsift.antlr.SnpSiftParser.FunctionCallContext;
import org.snpsift.antlr.SnpSiftParser.LiteralBoolContext;
import org.snpsift.antlr.SnpSiftParser.LiteralFloatContext;
import org.snpsift.antlr.SnpSiftParser.LiteralIndexContext;
import org.snpsift.antlr.SnpSiftParser.LiteralIntContext;
import org.snpsift.antlr.SnpSiftParser.LiteralStringContext;
import org.snpsift.antlr.SnpSiftParser.VarReferenceContext;
import org.snpsift.antlr.SnpSiftParser.VarReferenceListContext;
import org.snpsift.antlr.SnpSiftParser.VarReferenceListSubContext;
import org.snpsift.lang.expression.And;
import org.snpsift.lang.expression.Div;
import org.snpsift.lang.expression.Eq;
import org.snpsift.lang.expression.Exists;
import org.snpsift.lang.expression.Expression;
import org.snpsift.lang.expression.Field;
import org.snpsift.lang.expression.FieldConstant;
import org.snpsift.lang.expression.FieldEff;
import org.snpsift.lang.expression.FieldGenotype;
import org.snpsift.lang.expression.FieldGenotypeSub;
import org.snpsift.lang.expression.FieldLof;
import org.snpsift.lang.expression.FieldNmd;
import org.snpsift.lang.expression.FieldSub;
import org.snpsift.lang.expression.Ge;
import org.snpsift.lang.expression.Gt;
import org.snpsift.lang.expression.Has;
import org.snpsift.lang.expression.Le;
import org.snpsift.lang.expression.Literal;
import org.snpsift.lang.expression.Lt;
import org.snpsift.lang.expression.Match;
import org.snpsift.lang.expression.Minus;
import org.snpsift.lang.expression.Mod;
import org.snpsift.lang.expression.Na;
import org.snpsift.lang.expression.Neq;
import org.snpsift.lang.expression.Not;
import org.snpsift.lang.expression.NotMatch;
import org.snpsift.lang.expression.Or;
import org.snpsift.lang.expression.Plus;
import org.snpsift.lang.expression.Times;
import org.snpsift.lang.expression.Xor;
import org.snpsift.lang.function.CountHet;
import org.snpsift.lang.function.CountHom;
import org.snpsift.lang.function.CountRef;
import org.snpsift.lang.function.CountVariant;
import org.snpsift.lang.function.Function;
import org.snpsift.lang.function.FunctionBoolGenotype;
import org.snpsift.lang.function.In;
import org.snpsift.lang.function.IsHet;
import org.snpsift.lang.function.IsHom;
import org.snpsift.lang.function.IsRef;
import org.snpsift.lang.function.IsVariant;

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
		if (debug) Log.debug("Creating AST: " + expression);

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
		if (debug) Log.debug("Tree: " + tree.toStringTree());
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
			Log.debug("\n\tLeaveClassName : " + leaveName //
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
			if (debug) Log.debug("Logic operator: '" + op + "'");

			// Parse expressions
			Expression l = expressionFactory(tree.getChild(0));
			Expression r = expressionFactory(tree.getChild(2));

			if (op.equals("&") || op.equals("&&")) expression = new And(l, r);
			else if (op.equals("|") || op.equals("||")) expression = new Or(l, r);
			else if (op.equals("^") || op.equals("~")) expression = new Xor(l, r);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionTimesContext.class) { // Multiplication operators
			String op = ((ExpressionTimesContext) tree).op.getText();
			if (debug) Log.debug("Multiplication operator: '" + op + "'");

			Expression left = expressionFactory(tree.getChild(0));
			Expression right = expressionFactory(tree.getChild(2));

			if (op.equals("*")) expression = new Times(left, right);
			else if (op.equals("/")) expression = new Div(left, right);
			else if (op.equals("%")) expression = new Mod(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionPlusContext.class) { // Addition operators
			String op = ((ExpressionPlusContext) tree).op.getText();
			if (debug) Log.debug("Plus operator: '" + op + "'");

			Expression left = expressionFactory(tree.getChild(0));
			Expression right = expressionFactory(tree.getChild(2));

			if (op.equals("+")) expression = new Plus(left, right);
			else if (op.equals("-")) expression = new Minus(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionCompContext.class) { // Comparison operators
			String op = ((ExpressionCompContext) tree).op.getText();
			if (debug) Log.debug("Compare operator: '" + op + "'");

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
			else if (op.equals("has")) expression = new Has(left, right);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionExistsContext.class) { // Exists operators
			String op = ((ExpressionExistsContext) tree).op.getText();
			Expression expr = expressionFactory(tree.getChild(1));

			if (op.equalsIgnoreCase("exists")) expression = new Exists(expr);
			else if (op.equalsIgnoreCase("na")) expression = new Na(expr);
			else throw new RuntimeException("Unknown operator '" + op + "'");

		} else if (leaveClass == ExpressionUnaryContext.class) { // Unary operators
			String op = ((ExpressionUnaryContext) tree).op.getText();
			if (debug) Log.debug("Unary operator: '" + op + "'");

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

		if (debug) Log.debug("Expression: " + expression);
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

			Log.debug("\n\tLeaveClassName : " + leaveName //
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
			Log.debug("\n\tLeaveClassName : " + leaveName //
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
			else if (name.equalsIgnoreCase("ann") || name.equalsIgnoreCase("eff")) field = new FieldEff(null, idxExpr, formatVersion, name);
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
				else if (name.equalsIgnoreCase("ann") || name.equalsIgnoreCase("eff")) field = new FieldEff(subName, idxExpr, formatVersion, name);
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
			Log.debug("\n\tLeaveClassName : " + leaveName //
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
			Log.debug("\n\tLeaveClassName : " + leaveName //
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

		if (debug) Log.debug("vcfExpression: " + func);
		return func;
	}

}
