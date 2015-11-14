// Generated from SnpSift.g by ANTLR 4.5.1
package ca.mcgill.mcb.pcingola.snpSift.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SnpSiftParser}.
 */
public interface SnpSiftListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SnpSiftParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(SnpSiftParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnpSiftParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(SnpSiftParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionSet}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionSet(SnpSiftParser.ExpressionSetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionSet}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionSet(SnpSiftParser.ExpressionSetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalString}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralString(SnpSiftParser.LiteralStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalString}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralString(SnpSiftParser.LiteralStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionUnary}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionUnary(SnpSiftParser.ExpressionUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionUnary}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionUnary(SnpSiftParser.ExpressionUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionComp}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionComp(SnpSiftParser.ExpressionCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionComp}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionComp(SnpSiftParser.ExpressionCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalBool}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralBool(SnpSiftParser.LiteralBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalBool}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralBool(SnpSiftParser.LiteralBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReferenceList}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReferenceList(SnpSiftParser.VarReferenceListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReferenceList}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReferenceList(SnpSiftParser.VarReferenceListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalFloat}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralFloat(SnpSiftParser.LiteralFloatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalFloat}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralFloat(SnpSiftParser.LiteralFloatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalIndex}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralIndex(SnpSiftParser.LiteralIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalIndex}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralIndex(SnpSiftParser.LiteralIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReference}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReference(SnpSiftParser.VarReferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReference}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReference(SnpSiftParser.VarReferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReferenceListSub}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReferenceListSub(SnpSiftParser.VarReferenceListSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReferenceListSub}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReferenceListSub(SnpSiftParser.VarReferenceListSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionTimes}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionTimes(SnpSiftParser.ExpressionTimesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionTimes}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionTimes(SnpSiftParser.ExpressionTimesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionExists}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionExists(SnpSiftParser.ExpressionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionExists}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionExists(SnpSiftParser.ExpressionExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionPlus}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionPlus(SnpSiftParser.ExpressionPlusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionPlus}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionPlus(SnpSiftParser.ExpressionPlusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionLogic}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionLogic(SnpSiftParser.ExpressionLogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionLogic}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionLogic(SnpSiftParser.ExpressionLogicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(SnpSiftParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(SnpSiftParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionParen}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionParen(SnpSiftParser.ExpressionParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionParen}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionParen(SnpSiftParser.ExpressionParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionCond}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionCond(SnpSiftParser.ExpressionCondContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionCond}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionCond(SnpSiftParser.ExpressionCondContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalInt}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralInt(SnpSiftParser.LiteralIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalInt}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralInt(SnpSiftParser.LiteralIntContext ctx);
}