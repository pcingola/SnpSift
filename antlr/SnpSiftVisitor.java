// Generated from SnpSift.g by ANTLR 4.4
package ca.mcgill.mcb.pcingola.snpSift.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SnpSiftParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SnpSiftVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code expressionSet}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionSet(@NotNull SnpSiftParser.ExpressionSetContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalString}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralString(@NotNull SnpSiftParser.LiteralStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionUnary}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionUnary(@NotNull SnpSiftParser.ExpressionUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionComp}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionComp(@NotNull SnpSiftParser.ExpressionCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalBool}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralBool(@NotNull SnpSiftParser.LiteralBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varReferenceList}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarReferenceList(@NotNull SnpSiftParser.VarReferenceListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalFloat}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralFloat(@NotNull SnpSiftParser.LiteralFloatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalIndex}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralIndex(@NotNull SnpSiftParser.LiteralIndexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varReference}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarReference(@NotNull SnpSiftParser.VarReferenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varReferenceListSub}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarReferenceListSub(@NotNull SnpSiftParser.VarReferenceListSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionTimes}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionTimes(@NotNull SnpSiftParser.ExpressionTimesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionExists}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionExists(@NotNull SnpSiftParser.ExpressionExistsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnpSiftParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(@NotNull SnpSiftParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionPlus}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionPlus(@NotNull SnpSiftParser.ExpressionPlusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionLogic}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionLogic(@NotNull SnpSiftParser.ExpressionLogicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(@NotNull SnpSiftParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionParen}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionParen(@NotNull SnpSiftParser.ExpressionParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionCond}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionCond(@NotNull SnpSiftParser.ExpressionCondContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalInt}
	 * labeled alternative in {@link SnpSiftParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralInt(@NotNull SnpSiftParser.LiteralIntContext ctx);
}