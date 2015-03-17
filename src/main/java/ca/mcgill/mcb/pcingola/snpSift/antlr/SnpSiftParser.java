// Generated from SnpSift.g by ANTLR 4.4
package ca.mcgill.mcb.pcingola.snpSift.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SnpSiftParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__34=1, T__33=2, T__32=3, T__31=4, T__30=5, T__29=6, T__28=7, T__27=8, 
		T__26=9, T__25=10, T__24=11, T__23=12, T__22=13, T__21=14, T__20=15, T__19=16, 
		T__18=17, T__17=18, T__16=19, T__15=20, T__14=21, T__13=22, T__12=23, 
		T__11=24, T__10=25, T__9=26, T__8=27, T__7=28, T__6=29, T__5=30, T__4=31, 
		T__3=32, T__2=33, T__1=34, T__0=35, WS=36, COMMENT_SL=37, COMMENT_HASH=38, 
		BOOL_LITERAL=39, INT_LITERAL=40, FLOAT_LITERAL=41, STRING_LITERAL=42, 
		ID=43;
	public static final String[] tokenNames = {
		"<INVALID>", "'/'", "'!='", "'||'", "'&&'", "'='", "'^'", "'?'", "'<='", 
		"'ALL'", "'&'", "'('", "'*'", "'has'", "'exists'", "','", "'na'", "'].'", 
		"':'", "'>='", "'['", "'<'", "'=='", "'|'", "'!~'", "']'", "'>'", "'!'", 
		"'=~'", "'%'", "'in'", "'ANY'", "')'", "'+'", "'-'", "'SET'", "WS", "COMMENT_SL", 
		"COMMENT_HASH", "BOOL_LITERAL", "INT_LITERAL", "FLOAT_LITERAL", "STRING_LITERAL", 
		"ID"
	};
	public static final int
		RULE_compilationUnit = 0, RULE_expression = 1;
	public static final String[] ruleNames = {
		"compilationUnit", "expression"
	};

	@Override
	public String getGrammarFileName() { return "SnpSift.g"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SnpSiftParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CompilationUnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SnpSiftParser.EOF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterCompilationUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitCompilationUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitCompilationUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilationUnit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(4); expression(0);
			setState(5); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExpressionSetContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionSetContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionSet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionSet(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralStringContext extends ExpressionContext {
		public TerminalNode STRING_LITERAL() { return getToken(SnpSiftParser.STRING_LITERAL, 0); }
		public LiteralStringContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterLiteralString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitLiteralString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitLiteralString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionUnaryContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionUnaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionCompContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionCompContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionComp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionComp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionComp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralBoolContext extends ExpressionContext {
		public TerminalNode BOOL_LITERAL() { return getToken(SnpSiftParser.BOOL_LITERAL, 0); }
		public LiteralBoolContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterLiteralBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitLiteralBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitLiteralBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarReferenceListContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public VarReferenceListContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterVarReferenceList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitVarReferenceList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitVarReferenceList(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralFloatContext extends ExpressionContext {
		public TerminalNode FLOAT_LITERAL() { return getToken(SnpSiftParser.FLOAT_LITERAL, 0); }
		public LiteralFloatContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterLiteralFloat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitLiteralFloat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitLiteralFloat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralIndexContext extends ExpressionContext {
		public Token idx;
		public LiteralIndexContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterLiteralIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitLiteralIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitLiteralIndex(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarReferenceContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(SnpSiftParser.ID, 0); }
		public VarReferenceContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterVarReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitVarReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitVarReference(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarReferenceListSubContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public VarReferenceListSubContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterVarReferenceListSub(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitVarReferenceListSub(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitVarReferenceListSub(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionTimesContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionTimesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionTimes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionTimes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionTimes(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionExistsContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionExistsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionExists(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionPlusContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionPlusContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionPlus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionPlus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionPlus(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionLogicContext extends ExpressionContext {
		public Token op;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionLogicContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionLogic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionLogic(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(SnpSiftParser.ID, 0); }
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public FunctionCallContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionParenContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionParenContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionCondContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionCondContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterExpressionCond(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitExpressionCond(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitExpressionCond(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralIntContext extends ExpressionContext {
		public TerminalNode INT_LITERAL() { return getToken(SnpSiftParser.INT_LITERAL, 0); }
		public LiteralIntContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).enterLiteralInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnpSiftListener ) ((SnpSiftListener)listener).exitLiteralInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnpSiftVisitor ) return ((SnpSiftVisitor<? extends T>)visitor).visitLiteralInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new ExpressionUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(8);
				((ExpressionUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__2) | (1L << T__1))) != 0)) ) {
					((ExpressionUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(9); expression(5);
				}
				break;
			case 2:
				{
				_localctx = new ExpressionExistsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(10);
				((ExpressionExistsContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__22) | (1L << T__21) | (1L << T__19))) != 0)) ) {
					((ExpressionExistsContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(11); expression(4);
				}
				break;
			case 3:
				{
				_localctx = new LiteralBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(12); match(BOOL_LITERAL);
				}
				break;
			case 4:
				{
				_localctx = new LiteralIntContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(13); match(INT_LITERAL);
				}
				break;
			case 5:
				{
				_localctx = new LiteralFloatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(14); match(FLOAT_LITERAL);
				}
				break;
			case 6:
				{
				_localctx = new LiteralStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15); match(STRING_LITERAL);
				}
				break;
			case 7:
				{
				_localctx = new LiteralIndexContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(16);
				((LiteralIndexContext)_localctx).idx = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__26) | (1L << T__23) | (1L << T__4))) != 0)) ) {
					((LiteralIndexContext)_localctx).idx = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 8:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(17); match(ID);
				setState(18); match(T__24);
				setState(27);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__26) | (1L << T__24) | (1L << T__23) | (1L << T__22) | (1L << T__21) | (1L << T__19) | (1L << T__8) | (1L << T__4) | (1L << T__2) | (1L << T__1) | (1L << BOOL_LITERAL) | (1L << INT_LITERAL) | (1L << FLOAT_LITERAL) | (1L << STRING_LITERAL) | (1L << ID))) != 0)) {
					{
					setState(19); expression(0);
					setState(24);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__20) {
						{
						{
						setState(20); match(T__20);
						setState(21); expression(0);
						}
						}
						setState(26);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(29); match(T__3);
				}
				break;
			case 9:
				{
				_localctx = new VarReferenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30); match(ID);
				}
				break;
			case 10:
				{
				_localctx = new ExpressionParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(31); match(T__24);
				setState(32); expression(0);
				setState(33); match(T__3);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(80);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(78);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new VarReferenceListSubContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(37);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(38); match(T__15);
						setState(39); expression(0);
						setState(40); match(T__18);
						setState(41); expression(12);
						}
						break;
					case 2:
						{
						_localctx = new VarReferenceListSubContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(43);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(44); match(T__15);
						setState(45);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__26) | (1L << T__23) | (1L << T__4))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						setState(46); match(T__18);
						setState(47); expression(11);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionLogicContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(48);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(49);
						((ExpressionLogicContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__31) | (1L << T__29) | (1L << T__25) | (1L << T__12))) != 0)) ) {
							((ExpressionLogicContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(50); expression(10);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionTimesContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(51);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(52);
						((ExpressionTimesContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__23) | (1L << T__6))) != 0)) ) {
							((ExpressionTimesContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(53); expression(9);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionPlusContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(54);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(55);
						((ExpressionPlusContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__2 || _la==T__1) ) {
							((ExpressionPlusContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(56); expression(8);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionCompContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(57);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(58);
						((ExpressionCompContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__33) | (1L << T__30) | (1L << T__27) | (1L << T__16) | (1L << T__14) | (1L << T__13) | (1L << T__11) | (1L << T__9) | (1L << T__7))) != 0)) ) {
							((ExpressionCompContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(59); expression(7);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionCondContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(60);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(61); match(T__28);
						setState(62); expression(0);
						setState(63); match(T__17);
						setState(64); expression(2);
						}
						break;
					case 8:
						{
						_localctx = new VarReferenceListContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(66);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(67); match(T__15);
						setState(68); expression(0);
						setState(69); match(T__10);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionSetContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(71);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(72); match(T__5);
						setState(73); match(T__0);
						setState(74); match(T__15);
						setState(75); expression(0);
						setState(76); match(T__10);
						}
						break;
					}
					} 
				}
				setState(82);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 11);
		case 1: return precpred(_ctx, 10);
		case 2: return precpred(_ctx, 9);
		case 3: return precpred(_ctx, 8);
		case 4: return precpred(_ctx, 7);
		case 5: return precpred(_ctx, 6);
		case 6: return precpred(_ctx, 1);
		case 7: return precpred(_ctx, 12);
		case 8: return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3-V\4\2\t\2\4\3\t\3"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\7\3\31\n\3\f\3\16\3\34\13\3\5\3\36\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"&\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3Q\n\3\f\3\16\3T\13\3\3\3\2\3\4\4\2\4"+
		"\2\t\4\2\35\35#$\4\2\17\20\22\22\6\2\t\t\13\13\16\16!!\6\2\5\6\b\b\f\f"+
		"\31\31\5\2\3\3\16\16\37\37\3\2#$\n\2\4\4\7\7\n\n\25\25\27\30\32\32\34"+
		"\34\36\36g\2\6\3\2\2\2\4%\3\2\2\2\6\7\5\4\3\2\7\b\7\2\2\3\b\3\3\2\2\2"+
		"\t\n\b\3\1\2\n\13\t\2\2\2\13&\5\4\3\7\f\r\t\3\2\2\r&\5\4\3\6\16&\7)\2"+
		"\2\17&\7*\2\2\20&\7+\2\2\21&\7,\2\2\22&\t\4\2\2\23\24\7-\2\2\24\35\7\r"+
		"\2\2\25\32\5\4\3\2\26\27\7\21\2\2\27\31\5\4\3\2\30\26\3\2\2\2\31\34\3"+
		"\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2\33\36\3\2\2\2\34\32\3\2\2\2\35\25\3"+
		"\2\2\2\35\36\3\2\2\2\36\37\3\2\2\2\37&\7\"\2\2 &\7-\2\2!\"\7\r\2\2\"#"+
		"\5\4\3\2#$\7\"\2\2$&\3\2\2\2%\t\3\2\2\2%\f\3\2\2\2%\16\3\2\2\2%\17\3\2"+
		"\2\2%\20\3\2\2\2%\21\3\2\2\2%\22\3\2\2\2%\23\3\2\2\2% \3\2\2\2%!\3\2\2"+
		"\2&R\3\2\2\2\'(\f\r\2\2()\7\26\2\2)*\5\4\3\2*+\7\23\2\2+,\5\4\3\16,Q\3"+
		"\2\2\2-.\f\f\2\2./\7\26\2\2/\60\t\4\2\2\60\61\7\23\2\2\61Q\5\4\3\r\62"+
		"\63\f\13\2\2\63\64\t\5\2\2\64Q\5\4\3\f\65\66\f\n\2\2\66\67\t\6\2\2\67"+
		"Q\5\4\3\1389\f\t\2\29:\t\7\2\2:Q\5\4\3\n;<\f\b\2\2<=\t\b\2\2=Q\5\4\3\t"+
		">?\f\3\2\2?@\7\t\2\2@A\5\4\3\2AB\7\24\2\2BC\5\4\3\4CQ\3\2\2\2DE\f\16\2"+
		"\2EF\7\26\2\2FG\5\4\3\2GH\7\33\2\2HQ\3\2\2\2IJ\f\5\2\2JK\7 \2\2KL\7%\2"+
		"\2LM\7\26\2\2MN\5\4\3\2NO\7\33\2\2OQ\3\2\2\2P\'\3\2\2\2P-\3\2\2\2P\62"+
		"\3\2\2\2P\65\3\2\2\2P8\3\2\2\2P;\3\2\2\2P>\3\2\2\2PD\3\2\2\2PI\3\2\2\2"+
		"QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2S\5\3\2\2\2TR\3\2\2\2\7\32\35%PR";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}