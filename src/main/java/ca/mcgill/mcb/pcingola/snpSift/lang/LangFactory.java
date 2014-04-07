package ca.mcgill.mcb.pcingola.snpSift.lang;

import java.util.ArrayList;
import java.util.HashSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import ca.mcgill.mcb.pcingola.snpSift.antlr.VcfFilterLexer;
import ca.mcgill.mcb.pcingola.snpSift.antlr.VcfFilterParser;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.And;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Condition;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Eq;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Exists;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Ge;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Gt;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Le;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Lt;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Match;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Na;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Neq;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Not;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.NotMatch;
import ca.mcgill.mcb.pcingola.snpSift.lang.condition.Or;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Field;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldConstant;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldEff;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldGenotype;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldGenotypeSub;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator.IteratorType;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldLof;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldNmd;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldSub;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Literal;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountHet;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountHom;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountRef;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.CountVariant;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.FunctionBool;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.FunctionBoolGenotype;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.In;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsHet;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsHom;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsRef;
import ca.mcgill.mcb.pcingola.snpSift.lang.function.IsVariant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;

/**
 * Creates objects from an AST
 * 
 * @author pcingola
 */
public class LangFactory {

	protected static boolean debug = false;
	ArrayList<HashSet<String>> sets = new ArrayList<HashSet<String>>();
	VcfEffect.FormatVersion formatVersion;
	boolean exceptionIfNotFound = false;

	public LangFactory() {
		sets = new ArrayList<HashSet<String>>(); // No sets
	}

	public LangFactory(ArrayList<HashSet<String>> sets, VcfEffect.FormatVersion formatVersion, boolean exceptionIfNotFound) {
		this.sets = sets;
		this.formatVersion = formatVersion;
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	/**
	 * Create 'Conditions' from Tree
	 * @param tree
	 * @return
	 */
	public Condition conditionFactory(Tree tree) {
		String leaveName = tree.getText();

		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\n\tTree      : " + tree.toStringTree());

		Condition condition = null;

		if (leaveName.equalsIgnoreCase("CONDITION")) {
			condition = conditionFactory(tree.getChild(0));
		} else if (leaveName.equalsIgnoreCase("&")) { // Logical operators
			Condition l = conditionFactory(tree.getChild(0));
			Condition r = conditionFactory(tree.getChild(1));
			condition = new And(l, r);
		} else if (leaveName.equalsIgnoreCase("|")) {
			Condition l = conditionFactory(tree.getChild(0));
			Condition r = conditionFactory(tree.getChild(1));
			condition = new Or(l, r);
		} else if (leaveName.equalsIgnoreCase("!")) {
			Condition v = conditionFactory(tree.getChild(0));
			condition = new Not(v);
		} else if (leaveName.equalsIgnoreCase("OP_BINARY")) { // Numerical operators
			leaveName = tree.getChild(0).getText();

			Expression left = expressionFactory(tree.getChild(1));
			Expression right = expressionFactory(tree.getChild(2));

			if (leaveName.equalsIgnoreCase("<")) condition = new Lt(left, right);
			else if (leaveName.equalsIgnoreCase("<=")) condition = new Le(left, right);
			else if (leaveName.equalsIgnoreCase(">")) condition = new Gt(left, right);
			else if (leaveName.equalsIgnoreCase(">=")) condition = new Ge(left, right);
			else if (leaveName.equalsIgnoreCase("=")) condition = new Eq(left, right);
			else if (leaveName.equalsIgnoreCase("!=")) condition = new Neq(left, right);
			else if (leaveName.equalsIgnoreCase("=~")) condition = new Match(left, right);
			else if (leaveName.equalsIgnoreCase("!~")) condition = new NotMatch(left, right);
			else throw new RuntimeException("Unknown operator '" + leaveName + "'");

		} else if (leaveName.equalsIgnoreCase("OP_UNARY")) { // String operators
			leaveName = tree.getChild(0).getText();

			Expression expr = expressionFactory(tree.getChild(1));

			if (leaveName.equalsIgnoreCase("na")) condition = new Na(expr);
			else if (leaveName.equalsIgnoreCase("exists")) condition = new Exists(expr);
			else throw new RuntimeException("Unknown operator '" + leaveName + "'");

		} else if (leaveName.equalsIgnoreCase("FUNCTION_BOOL_GENOTYPE")) { // Genotype functions (that return a boolean)
			condition = functionBoolGenotypeFactory(tree);
		} else if (leaveName.equalsIgnoreCase("FUNCTION_BOOL_SET")) { // Set functions (that return a boolean)
			condition = functionBoolSetFactory(tree);
		} else throw new RuntimeException("Unknown operator '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression: " + condition);
		return condition;
	}

	/**
	 * Create a field using lexer
	 * @param lexer
	 * @param verbose
	 * @return
	 * @throws RecognitionException
	 */
	private Field createFieldFromLexer(VcfFilterLexer lexer, boolean verbose) throws RecognitionException {
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		VcfFilterParser parser = new VcfFilterParser(tokens);

		// Parse 'var' (field)
		VcfFilterParser.var_return root;
		root = parser.var();
		Tree parseTree = (Tree) root.getTree();

		// Error creating?
		if (parseTree == null) {
			System.err.println("Can't create field tree");
			return null;
		}

		if (debug) Gpr.debug("Field Tree: " + parseTree.toStringTree());

		// Create a language factory
		LangFactory langFactory = new LangFactory(sets, formatVersion, exceptionIfNotFound);
		return langFactory.fieldFactory(parseTree);
	}

	/**
	 * Create 'Expression' from Tree
	 * @param tree
	 * @return
	 */
	public Expression expressionFactory(Tree tree) {
		String leaveName = tree.getText();

		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\n\tTree      : " + tree.toStringTree());

		Expression expr = null;

		if (leaveName.equalsIgnoreCase("VAR_FIELD") //
				|| leaveName.equalsIgnoreCase("VAR_SUBFIELD") //
				|| leaveName.equalsIgnoreCase("VAR_GENOTYPE_SUB") //
				|| leaveName.equalsIgnoreCase("VAR_EFF_SUB") //
				|| leaveName.equalsIgnoreCase("VAR_LOF_SUB") //
				|| leaveName.equalsIgnoreCase("VAR_NMD_SUB") //
				|| leaveName.equalsIgnoreCase("VAR_GENOTYPE_SUB_ARRAY")) {
			// Is it a field?
			expr = fieldFactory(tree);
		} else if (leaveName.equalsIgnoreCase("LITERAL_NUMBER")) {
			String num = tree.getChild(0).getText();
			expr = new Literal(num, true);
		} else if (leaveName.equalsIgnoreCase("LITERAL_STRING")) {
			String str = tree.getChild(0).getText();
			expr = new Literal(str);
		} else if (leaveName.equalsIgnoreCase("FUNCTION_ENTRY")) { // Functions
			expr = functionEntryFactory(tree);
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression [" + expr.getClass().getSimpleName() + "]: " + expr);
		return expr;
	}

	/**
	 * Create 'Expression' from Tree
	 * @param tree
	 * @return
	 */
	public Field fieldFactory(Tree tree) {
		String leaveName = tree.getText();

		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\n\tTree      : " + tree.toStringTree());

		Field field = null;

		if (leaveName.equalsIgnoreCase("VAR_FIELD")) {
			String name = tree.getChild(0).getText();
			if (FieldConstant.isConstantField(name)) field = FieldConstant.factory(name);
			else field = new Field(name);
		} else if (leaveName.equalsIgnoreCase("VAR_SUBFIELD")) {
			String name = tree.getChild(0).getText();
			int index = parseIndexField(tree.getChild(1).getText(), IteratorType.VAR);
			field = new FieldSub(name, index);
		} else if (leaveName.equalsIgnoreCase("VAR_GENOTYPE_SUB")) {
			int genotypeIndex = parseIndexField(tree.getChild(0).getText(), IteratorType.GENOTYPE);
			String name = tree.getChild(1).getText();
			field = new FieldGenotype(name, genotypeIndex);
		} else if (leaveName.equalsIgnoreCase("VAR_EFF_SUB")) {
			int effIndex = parseIndexField(tree.getChild(0).getText(), IteratorType.EFFECT);
			String name = tree.getChild(1).getText();
			field = new FieldEff(name, effIndex, formatVersion);
		} else if (leaveName.equalsIgnoreCase("VAR_LOF_SUB")) {
			int lofIndex = parseIndexField(tree.getChild(0).getText(), IteratorType.LOF);
			String name = tree.getChild(1).getText();
			field = new FieldLof(name, lofIndex);
		} else if (leaveName.equalsIgnoreCase("VAR_NMD_SUB")) {
			int nmdIndex = parseIndexField(tree.getChild(0).getText(), IteratorType.NMD);
			String name = tree.getChild(1).getText();
			field = new FieldNmd(name, nmdIndex);
		} else if (leaveName.equalsIgnoreCase("VAR_GENOTYPE_SUB_ARRAY")) {
			int genotypeIndex = parseIndexField(tree.getChild(0).getText(), IteratorType.GENOTYPE);
			String name = tree.getChild(1).getText();
			int index = parseIndexField(tree.getChild(2).getText(), IteratorType.GENOTYPE_VAR);
			field = new FieldGenotypeSub(name, genotypeIndex, index);
		} else throw new RuntimeException("Unknown field '" + leaveName + "'");

		field.setExceptionIfNotFound(exceptionIfNotFound);
		return field;
	}

	/**
	 * Create FunctionBoolGenotype from AST
	 * @param tree
	 * @return
	 */
	public FunctionBoolGenotype functionBoolGenotypeFactory(Tree tree) {
		String leaveName = tree.getChild(0).getText();

		int genotypeNum = genotypeNum(tree.getChild(1));
		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\tGenotypeNum: " + genotypeNum + "\n\tTree      : " + tree.toStringTree());

		FunctionBoolGenotype fun = null;

		if (leaveName.equalsIgnoreCase("isHom")) {
			fun = new IsHom(genotypeNum);
		} else if (leaveName.equalsIgnoreCase("isHet")) {
			fun = new IsHet(genotypeNum);
		} else if (leaveName.equalsIgnoreCase("isRef")) {
			fun = new IsRef(genotypeNum);
		} else if (leaveName.equalsIgnoreCase("isVariant")) {
			fun = new IsVariant(genotypeNum);
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression: " + fun);
		return fun;
	}

	/**
	 * Create FunctionBoolSet from AST
	 * @param tree
	 * @return
	 */
	public FunctionBool functionBoolSetFactory(Tree tree) {
		String leaveName = tree.getChild(0).getText();
		int setNum = Gpr.parseIntSafe(tree.getChild(1).getText());
		Expression left = expressionFactory(tree.getChild(2));
		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\tsetNum: " + setNum + "\n\tTree      : " + tree.toStringTree());

		FunctionBool fun = null;

		if (leaveName.equalsIgnoreCase("in")) {
			HashSet<String> set = sets.get(setNum);
			if (set == null) throw new RuntimeException("Could not find set number '" + tree.getChild(1).getText() + "' (" + setNum + ")");
			fun = new In(setNum, set, left);
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression: " + fun);
		return fun;
	}

	/**
	 * Create Functions from AST
	 * Note: These functions that are calculated on the whole VcfEntry.
	 * 
	 * @param tree
	 * @return
	 */
	public Expression functionEntryFactory(Tree tree) {
		String leaveName = tree.getChild(0).getText();

		if (debug) Gpr.debug("\n\tLeaveName : " + leaveName + "\n\tTree      : " + tree.toStringTree());

		Expression expr = null;

		if (leaveName.equalsIgnoreCase("countHom")) {
			expr = new CountHom();
		} else if (leaveName.equalsIgnoreCase("countHet")) {
			expr = new CountHet();
		} else if (leaveName.equalsIgnoreCase("countRef")) {
			expr = new CountRef();
		} else if (leaveName.equalsIgnoreCase("countVariant")) {
			expr = new CountVariant();
		} else throw new RuntimeException("Unknown expression '" + leaveName + "'");

		if (debug) Gpr.debug("vcfExpression: " + expr);
		return expr;
	}

	/**
	 * Get genotype number
	 * @return
	 */
	int genotypeNum(Tree tree) {
		if (!tree.getText().equals("VAR_GENOTYPE")) throw new RuntimeException("Expecting genotype (VAR_GENOTYPE) , but got: " + tree.getText());
		return Gpr.parseIntSafe(tree.getChild(0).getText());
	}

	/**
	 * Parse a field
	 * @param fieldStr
	 * @return
	 * @throws Exception
	 */
	public Field parseField(String fieldStr) throws Exception {
		if (debug) Gpr.debug("Parse field: \"" + fieldStr + "\"");
		VcfFilterLexer lexer = new VcfFilterLexer(new ANTLRStringStream(fieldStr)); // Parse string (lexer first, then parser)
		Field field = createFieldFromLexer(lexer, true); // Parse tree and create expression

		if (field == null) {
			System.err.println("Fatal error: Cannot build field tree.");
			System.exit(-1);
		}

		if (debug) Gpr.debug("Field: " + field);
		return field;
	}

	/**
	 * Convert an index into a number
	 * @param text
	 * @return
	 */
	int parseIndexField(String text, IteratorType starType) {
		if (text.equals("*")) return -1;
		if (text.equals("ANY")) return -1;
		if (text.equals("?")) return -2;
		if (text.equals("ALL")) return -2;
		return Gpr.parseIntSafe(text);
	}
}
