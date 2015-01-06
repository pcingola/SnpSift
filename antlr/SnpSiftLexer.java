// Generated from SnpSift.g by ANTLR 4.4
package ca.mcgill.mcb.pcingola.snpSift.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SnpSiftLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__33=1, T__32=2, T__31=3, T__30=4, T__29=5, T__28=6, T__27=7, T__26=8, 
		T__25=9, T__24=10, T__23=11, T__22=12, T__21=13, T__20=14, T__19=15, T__18=16, 
		T__17=17, T__16=18, T__15=19, T__14=20, T__13=21, T__12=22, T__11=23, 
		T__10=24, T__9=25, T__8=26, T__7=27, T__6=28, T__5=29, T__4=30, T__3=31, 
		T__2=32, T__1=33, T__0=34, WS=35, COMMENT_SL=36, COMMENT_HASH=37, BOOL_LITERAL=38, 
		INT_LITERAL=39, FLOAT_LITERAL=40, STRING_LITERAL=41, ID=42;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'", "'*'"
	};
	public static final String[] ruleNames = {
		"T__33", "T__32", "T__31", "T__30", "T__29", "T__28", "T__27", "T__26", 
		"T__25", "T__24", "T__23", "T__22", "T__21", "T__20", "T__19", "T__18", 
		"T__17", "T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", 
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "DIGIT", "NUMBER", "LETTER", "LOWER", "UPPER", "NEWLINE", "ALPHANUM", 
		"WS", "COMMENT_SL", "COMMENT_HASH", "BOOL_LITERAL", "INT_LITERAL", "FLOAT_LITERAL", 
		"STRING_LITERAL", "ID"
	};


	public SnpSiftLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SnpSift.g"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 41: WS_action((RuleContext)_localctx, actionIndex); break;
		case 42: COMMENT_SL_action((RuleContext)_localctx, actionIndex); break;
		case 43: COMMENT_HASH_action((RuleContext)_localctx, actionIndex); break;
		case 47: STRING_LITERAL_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void COMMENT_HASH_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:  skip();  break;
		}
	}
	private void STRING_LITERAL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:  setText(getText().substring( 1, getText().length()-1 ) );  break;
		}
	}
	private void COMMENT_SL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:  skip();  break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2,\u012b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n"+
		"\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\3$\3$\3%\6%\u00c3\n%\r%\16%\u00c4"+
		"\3&\3&\5&\u00c9\n&\3\'\3\'\3(\3(\3)\6)\u00d0\n)\r)\16)\u00d1\3*\3*\5*"+
		"\u00d6\n*\3+\6+\u00d9\n+\r+\16+\u00da\3+\3+\3,\3,\3,\3,\7,\u00e3\n,\f"+
		",\16,\u00e6\13,\3,\3,\3,\3-\3-\7-\u00ed\n-\f-\16-\u00f0\13-\3-\3-\3-\3"+
		".\3.\3.\3.\3.\3.\3.\3.\3.\5.\u00fe\n.\3/\5/\u0101\n/\3/\3/\3\60\5\60\u0106"+
		"\n\60\3\60\3\60\3\60\5\60\u010b\n\60\3\60\3\60\5\60\u010f\n\60\3\60\5"+
		"\60\u0112\n\60\3\60\3\60\3\60\3\60\3\60\3\60\5\60\u011a\n\60\3\61\3\61"+
		"\7\61\u011e\n\61\f\61\16\61\u0121\13\61\3\61\3\61\3\61\3\62\3\62\6\62"+
		"\u0128\n\62\r\62\16\62\u0129\2\2\63\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G\2I\2K\2M\2O\2Q\2S\2U"+
		"%W&Y\'[(])_*a+c,\3\2\b\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2--//\4\2GG"+
		"gg\5\2\f\f\17\17))\4\2\60\60aa\u0135\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2"+
		"\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3"+
		"\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2C\3\2\2\2\2E\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2"+
		"\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\3e\3\2\2\2\5g\3\2\2\2\7j"+
		"\3\2\2\2\tm\3\2\2\2\13p\3\2\2\2\rr\3\2\2\2\17t\3\2\2\2\21v\3\2\2\2\23"+
		"y\3\2\2\2\25}\3\2\2\2\27\177\3\2\2\2\31\u0081\3\2\2\2\33\u0083\3\2\2\2"+
		"\35\u008a\3\2\2\2\37\u008c\3\2\2\2!\u008f\3\2\2\2#\u0092\3\2\2\2%\u0094"+
		"\3\2\2\2\'\u0097\3\2\2\2)\u0099\3\2\2\2+\u009b\3\2\2\2-\u009e\3\2\2\2"+
		"/\u00a0\3\2\2\2\61\u00a3\3\2\2\2\63\u00a5\3\2\2\2\65\u00a7\3\2\2\2\67"+
		"\u00a9\3\2\2\29\u00ac\3\2\2\2;\u00ae\3\2\2\2=\u00b1\3\2\2\2?\u00b5\3\2"+
		"\2\2A\u00b7\3\2\2\2C\u00b9\3\2\2\2E\u00bb\3\2\2\2G\u00bf\3\2\2\2I\u00c2"+
		"\3\2\2\2K\u00c8\3\2\2\2M\u00ca\3\2\2\2O\u00cc\3\2\2\2Q\u00cf\3\2\2\2S"+
		"\u00d5\3\2\2\2U\u00d8\3\2\2\2W\u00de\3\2\2\2Y\u00ea\3\2\2\2[\u00fd\3\2"+
		"\2\2]\u0100\3\2\2\2_\u0119\3\2\2\2a\u011b\3\2\2\2c\u0127\3\2\2\2ef\7\61"+
		"\2\2f\4\3\2\2\2gh\7#\2\2hi\7?\2\2i\6\3\2\2\2jk\7~\2\2kl\7~\2\2l\b\3\2"+
		"\2\2mn\7(\2\2no\7(\2\2o\n\3\2\2\2pq\7?\2\2q\f\3\2\2\2rs\7`\2\2s\16\3\2"+
		"\2\2tu\7A\2\2u\20\3\2\2\2vw\7>\2\2wx\7?\2\2x\22\3\2\2\2yz\7C\2\2z{\7N"+
		"\2\2{|\7N\2\2|\24\3\2\2\2}~\7(\2\2~\26\3\2\2\2\177\u0080\7*\2\2\u0080"+
		"\30\3\2\2\2\u0081\u0082\7,\2\2\u0082\32\3\2\2\2\u0083\u0084\7g\2\2\u0084"+
		"\u0085\7z\2\2\u0085\u0086\7k\2\2\u0086\u0087\7u\2\2\u0087\u0088\7v\2\2"+
		"\u0088\u0089\7u\2\2\u0089\34\3\2\2\2\u008a\u008b\7.\2\2\u008b\36\3\2\2"+
		"\2\u008c\u008d\7p\2\2\u008d\u008e\7c\2\2\u008e \3\2\2\2\u008f\u0090\7"+
		"_\2\2\u0090\u0091\7\60\2\2\u0091\"\3\2\2\2\u0092\u0093\7<\2\2\u0093$\3"+
		"\2\2\2\u0094\u0095\7@\2\2\u0095\u0096\7?\2\2\u0096&\3\2\2\2\u0097\u0098"+
		"\7]\2\2\u0098(\3\2\2\2\u0099\u009a\7>\2\2\u009a*\3\2\2\2\u009b\u009c\7"+
		"?\2\2\u009c\u009d\7?\2\2\u009d,\3\2\2\2\u009e\u009f\7~\2\2\u009f.\3\2"+
		"\2\2\u00a0\u00a1\7#\2\2\u00a1\u00a2\7\u0080\2\2\u00a2\60\3\2\2\2\u00a3"+
		"\u00a4\7_\2\2\u00a4\62\3\2\2\2\u00a5\u00a6\7@\2\2\u00a6\64\3\2\2\2\u00a7"+
		"\u00a8\7#\2\2\u00a8\66\3\2\2\2\u00a9\u00aa\7?\2\2\u00aa\u00ab\7\u0080"+
		"\2\2\u00ab8\3\2\2\2\u00ac\u00ad\7\'\2\2\u00ad:\3\2\2\2\u00ae\u00af\7k"+
		"\2\2\u00af\u00b0\7p\2\2\u00b0<\3\2\2\2\u00b1\u00b2\7C\2\2\u00b2\u00b3"+
		"\7P\2\2\u00b3\u00b4\7[\2\2\u00b4>\3\2\2\2\u00b5\u00b6\7+\2\2\u00b6@\3"+
		"\2\2\2\u00b7\u00b8\7-\2\2\u00b8B\3\2\2\2\u00b9\u00ba\7/\2\2\u00baD\3\2"+
		"\2\2\u00bb\u00bc\7U\2\2\u00bc\u00bd\7G\2\2\u00bd\u00be\7V\2\2\u00beF\3"+
		"\2\2\2\u00bf\u00c0\4\62;\2\u00c0H\3\2\2\2\u00c1\u00c3\5G$\2\u00c2\u00c1"+
		"\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"J\3\2\2\2\u00c6\u00c9\5M\'\2\u00c7\u00c9\5O(\2\u00c8\u00c6\3\2\2\2\u00c8"+
		"\u00c7\3\2\2\2\u00c9L\3\2\2\2\u00ca\u00cb\4c|\2\u00cbN\3\2\2\2\u00cc\u00cd"+
		"\4C\\\2\u00cdP\3\2\2\2\u00ce\u00d0\t\2\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2R\3\2\2\2\u00d3"+
		"\u00d6\5K&\2\u00d4\u00d6\5G$\2\u00d5\u00d3\3\2\2\2\u00d5\u00d4\3\2\2\2"+
		"\u00d6T\3\2\2\2\u00d7\u00d9\t\3\2\2\u00d8\u00d7\3\2\2\2\u00d9\u00da\3"+
		"\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc"+
		"\u00dd\b+\2\2\u00ddV\3\2\2\2\u00de\u00df\7\61\2\2\u00df\u00e0\7\61\2\2"+
		"\u00e0\u00e4\3\2\2\2\u00e1\u00e3\n\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6"+
		"\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6"+
		"\u00e4\3\2\2\2\u00e7\u00e8\5Q)\2\u00e8\u00e9\b,\3\2\u00e9X\3\2\2\2\u00ea"+
		"\u00ee\7%\2\2\u00eb\u00ed\n\2\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00f0\3\2"+
		"\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f1\3\2\2\2\u00f0"+
		"\u00ee\3\2\2\2\u00f1\u00f2\5Q)\2\u00f2\u00f3\b-\4\2\u00f3Z\3\2\2\2\u00f4"+
		"\u00f5\7v\2\2\u00f5\u00f6\7t\2\2\u00f6\u00f7\7w\2\2\u00f7\u00fe\7g\2\2"+
		"\u00f8\u00f9\7h\2\2\u00f9\u00fa\7c\2\2\u00fa\u00fb\7n\2\2\u00fb\u00fc"+
		"\7u\2\2\u00fc\u00fe\7g\2\2\u00fd\u00f4\3\2\2\2\u00fd\u00f8\3\2\2\2\u00fe"+
		"\\\3\2\2\2\u00ff\u0101\t\4\2\2\u0100\u00ff\3\2\2\2\u0100\u0101\3\2\2\2"+
		"\u0101\u0102\3\2\2\2\u0102\u0103\5I%\2\u0103^\3\2\2\2\u0104\u0106\t\4"+
		"\2\2\u0105\u0104\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0107\3\2\2\2\u0107"+
		"\u010a\5I%\2\u0108\u0109\7\60\2\2\u0109\u010b\5I%\2\u010a\u0108\3\2\2"+
		"\2\u010a\u010b\3\2\2\2\u010b\u0111\3\2\2\2\u010c\u010e\t\5\2\2\u010d\u010f"+
		"\t\4\2\2\u010e\u010d\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"\u0112\5I%\2\u0111\u010c\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u011a\3\2\2"+
		"\2\u0113\u0114\7P\2\2\u0114\u0115\7c\2\2\u0115\u011a\7P\2\2\u0116\u0117"+
		"\7P\2\2\u0117\u0118\7C\2\2\u0118\u011a\7P\2\2\u0119\u0105\3\2\2\2\u0119"+
		"\u0113\3\2\2\2\u0119\u0116\3\2\2\2\u011a`\3\2\2\2\u011b\u011f\7)\2\2\u011c"+
		"\u011e\n\6\2\2\u011d\u011c\3\2\2\2\u011e\u0121\3\2\2\2\u011f\u011d\3\2"+
		"\2\2\u011f\u0120\3\2\2\2\u0120\u0122\3\2\2\2\u0121\u011f\3\2\2\2\u0122"+
		"\u0123\7)\2\2\u0123\u0124\b\61\5\2\u0124b\3\2\2\2\u0125\u0128\5S*\2\u0126"+
		"\u0128\t\7\2\2\u0127\u0125\3\2\2\2\u0127\u0126\3\2\2\2\u0128\u0129\3\2"+
		"\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012ad\3\2\2\2\24\2\u00c4"+
		"\u00c8\u00d1\u00d5\u00da\u00e4\u00ee\u00fd\u0100\u0105\u010a\u010e\u0111"+
		"\u0119\u011f\u0127\u0129\6\3+\2\3,\3\3-\4\3\61\5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}