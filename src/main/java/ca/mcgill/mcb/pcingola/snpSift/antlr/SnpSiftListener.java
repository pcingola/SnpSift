// Generated from SnpSift.g by ANTLR 4.4
package ca.mcgill.mcb.pcingola.snpSift.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SnpSiftParser}.
 */
public interface SnpSiftListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code expressionSet}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionSet(@NotNull SnpSiftParser.ExpressionSetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionSet}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionSet(@NotNull SnpSiftParser.ExpressionSetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalString}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralString(@NotNull SnpSiftParser.LiteralStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalString}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralString(@NotNull SnpSiftParser.LiteralStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionUnary}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionUnary(@NotNull SnpSiftParser.ExpressionUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionUnary}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionUnary(@NotNull SnpSiftParser.ExpressionUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionComp}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionComp(@NotNull SnpSiftParser.ExpressionCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionComp}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionComp(@NotNull SnpSiftParser.ExpressionCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalBool}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralBool(@NotNull SnpSiftParser.LiteralBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalBool}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralBool(@NotNull SnpSiftParser.LiteralBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReferenceList}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReferenceList(@NotNull SnpSiftParser.VarReferenceListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReferenceList}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReferenceList(@NotNull SnpSiftParser.VarReferenceListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalFloat}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralFloat(@NotNull SnpSiftParser.LiteralFloatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalFloat}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralFloat(@NotNull SnpSiftParser.LiteralFloatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalIndex}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralIndex(@NotNull SnpSiftParser.LiteralIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalIndex}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralIndex(@NotNull SnpSiftParser.LiteralIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReference}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReference(@NotNull SnpSiftParser.VarReferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReference}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReference(@NotNull SnpSiftParser.VarReferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varReferenceListSub}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVarReferenceListSub(@NotNull SnpSiftParser.VarReferenceListSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varReferenceListSub}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVarReferenceListSub(@NotNull SnpSiftParser.VarReferenceListSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionTimes}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionTimes(@NotNull SnpSiftParser.ExpressionTimesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionTimes}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionTimes(@NotNull SnpSiftParser.ExpressionTimesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionExists}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionExists(@NotNull SnpSiftParser.ExpressionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionExists}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionExists(@NotNull SnpSiftParser.ExpressionExistsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnpSiftParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(@NotNull SnpSiftParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnpSiftParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(@NotNull SnpSiftParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionPlus}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionPlus(@NotNull SnpSiftParser.ExpressionPlusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionPlus}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionPlus(@NotNull SnpSiftParser.ExpressionPlusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionLogic}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionLogic(@NotNull SnpSiftParser.ExpressionLogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionLogic}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionLogic(@NotNull SnpSiftParser.ExpressionLogicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(@NotNull SnpSiftParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(@NotNull SnpSiftParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionParen}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionParen(@NotNull SnpSiftParser.ExpressionParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionParen}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionParen(@NotNull SnpSiftParser.ExpressionParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionCond}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionCond(@NotNull SnpSiftParser.ExpressionCondContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionCond}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionCond(@NotNull SnpSiftParser.ExpressionCondContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalInt}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralInt(@NotNull SnpSiftParser.LiteralIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalInt}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralInt(@NotNull SnpSiftParser.LiteralIntContext ctx);
}