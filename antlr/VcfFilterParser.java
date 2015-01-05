// $ANTLR 3.5.2 VcfFilter.g 2015-01-05 11:30:15

package ca.mcgill.mcb.pcingola.snpSift.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class VcfFilterParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALPHANUM", "COMMENT_SL", "CONDITION", 
		"DIGIT", "FLOAT", "FUNCTION_BOOL_GENOTYPE", "FUNCTION_BOOL_SET", "FUNCTION_ENTRY", 
		"ID", "LETTER", "LITERAL_NUMBER", "LITERAL_STRING", "LOWER", "NEWLINE", 
		"NUMBER", "OP_BINARY", "OP_UNARY", "STRING", "UPPER", "VAR_EFF_SUB", "VAR_FIELD", 
		"VAR_GENOTYPE", "VAR_GENOTYPE_SUB", "VAR_GENOTYPE_SUB_ARRAY", "VAR_LOF_SUB", 
		"VAR_NMD_SUB", "VAR_SUBFIELD", "WS", "'!'", "'!='", "'!~'", "'&'", "'('", 
		"')'", "'*'", "'<'", "'<='", "'='", "'=~'", "'>'", "'>='", "'?'", "'ALL'", 
		"'ANY'", "'EFF'", "'GEN'", "'LOF'", "'NMD'", "'SET'", "'['", "']'", "'].'", 
		"'countHet'", "'countHom'", "'countRef'", "'countVariant'", "'exists'", 
		"'in'", "'isHet'", "'isHom'", "'isRef'", "'isVariant'", "'na'", "'|'"
	};
	public static final int EOF=-1;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int T__34=34;
	public static final int T__35=35;
	public static final int T__36=36;
	public static final int T__37=37;
	public static final int T__38=38;
	public static final int T__39=39;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int T__51=51;
	public static final int T__52=52;
	public static final int T__53=53;
	public static final int T__54=54;
	public static final int T__55=55;
	public static final int T__56=56;
	public static final int T__57=57;
	public static final int T__58=58;
	public static final int T__59=59;
	public static final int T__60=60;
	public static final int T__61=61;
	public static final int T__62=62;
	public static final int T__63=63;
	public static final int T__64=64;
	public static final int T__65=65;
	public static final int T__66=66;
	public static final int T__67=67;
	public static final int ALPHANUM=4;
	public static final int COMMENT_SL=5;
	public static final int CONDITION=6;
	public static final int DIGIT=7;
	public static final int FLOAT=8;
	public static final int FUNCTION_BOOL_GENOTYPE=9;
	public static final int FUNCTION_BOOL_SET=10;
	public static final int FUNCTION_ENTRY=11;
	public static final int ID=12;
	public static final int LETTER=13;
	public static final int LITERAL_NUMBER=14;
	public static final int LITERAL_STRING=15;
	public static final int LOWER=16;
	public static final int NEWLINE=17;
	public static final int NUMBER=18;
	public static final int OP_BINARY=19;
	public static final int OP_UNARY=20;
	public static final int STRING=21;
	public static final int UPPER=22;
	public static final int VAR_EFF_SUB=23;
	public static final int VAR_FIELD=24;
	public static final int VAR_GENOTYPE=25;
	public static final int VAR_GENOTYPE_SUB=26;
	public static final int VAR_GENOTYPE_SUB_ARRAY=27;
	public static final int VAR_LOF_SUB=28;
	public static final int VAR_NMD_SUB=29;
	public static final int VAR_SUBFIELD=30;
	public static final int WS=31;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public VcfFilterParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public VcfFilterParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return VcfFilterParser.tokenNames; }
	@Override public String getGrammarFileName() { return "VcfFilter.g"; }


	public static class main_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "main"
	// VcfFilter.g:87:1: main : f= condition -> ^( CONDITION $f) ;
	public final VcfFilterParser.main_return main() throws RecognitionException {
		VcfFilterParser.main_return retval = new VcfFilterParser.main_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope f =null;

		RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");

		try {
			// VcfFilter.g:87:7: (f= condition -> ^( CONDITION $f) )
			// VcfFilter.g:87:9: f= condition
			{
			pushFollow(FOLLOW_condition_in_main375);
			f=condition();
			state._fsp--;

			stream_condition.add(f.getTree());
			// AST REWRITE
			// elements: f
			// token labels: 
			// rule labels: f, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_f=new RewriteRuleSubtreeStream(adaptor,"rule f",f!=null?f.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 87:21: -> ^( CONDITION $f)
			{
				// VcfFilter.g:87:24: ^( CONDITION $f)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONDITION, "CONDITION"), root_1);
				adaptor.addChild(root_1, stream_f.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "main"


	public static class condition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "condition"
	// VcfFilter.g:89:1: condition : subcondition ( boolOperator ^ subcondition )* ;
	public final VcfFilterParser.condition_return condition() throws RecognitionException {
		VcfFilterParser.condition_return retval = new VcfFilterParser.condition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope subcondition1 =null;
		ParserRuleReturnScope boolOperator2 =null;
		ParserRuleReturnScope subcondition3 =null;


		try {
			// VcfFilter.g:89:11: ( subcondition ( boolOperator ^ subcondition )* )
			// VcfFilter.g:89:13: subcondition ( boolOperator ^ subcondition )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_subcondition_in_condition392);
			subcondition1=subcondition();
			state._fsp--;

			adaptor.addChild(root_0, subcondition1.getTree());

			// VcfFilter.g:89:26: ( boolOperator ^ subcondition )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==35||LA1_0==67) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// VcfFilter.g:89:27: boolOperator ^ subcondition
					{
					pushFollow(FOLLOW_boolOperator_in_condition395);
					boolOperator2=boolOperator();
					state._fsp--;

					root_0 = (Object)adaptor.becomeRoot(boolOperator2.getTree(), root_0);
					pushFollow(FOLLOW_subcondition_in_condition398);
					subcondition3=subcondition();
					state._fsp--;

					adaptor.addChild(root_0, subcondition3.getTree());

					}
					break;

				default :
					break loop1;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "condition"


	public static class subcondition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "subcondition"
	// VcfFilter.g:90:1: subcondition : ( '!' ^)? ( bare | paren ) ;
	public final VcfFilterParser.subcondition_return subcondition() throws RecognitionException {
		VcfFilterParser.subcondition_return retval = new VcfFilterParser.subcondition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token char_literal4=null;
		ParserRuleReturnScope bare5 =null;
		ParserRuleReturnScope paren6 =null;

		Object char_literal4_tree=null;

		try {
			// VcfFilter.g:90:14: ( ( '!' ^)? ( bare | paren ) )
			// VcfFilter.g:90:16: ( '!' ^)? ( bare | paren )
			{
			root_0 = (Object)adaptor.nil();


			// VcfFilter.g:90:16: ( '!' ^)?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==32) ) {
				switch ( input.LA(2) ) {
					case 32:
					case 36:
					case 60:
					case 62:
					case 63:
					case 64:
					case 65:
					case 66:
						{
						alt2=1;
						}
						break;
					case ID:
						{
						int LA2_4 = input.LA(3);
						if ( (LA2_4==53) ) {
							int LA2_12 = input.LA(4);
							if ( (LA2_12==FLOAT||LA2_12==38||(LA2_12 >= 45 && LA2_12 <= 47)) ) {
								int LA2_18 = input.LA(5);
								if ( (LA2_18==54) ) {
									int LA2_24 = input.LA(6);
									if ( ((LA2_24 >= 33 && LA2_24 <= 34)||(LA2_24 >= 39 && LA2_24 <= 44)||LA2_24==61) ) {
										alt2=1;
									}
								}
							}
						}
						else if ( ((LA2_4 >= 33 && LA2_4 <= 34)||(LA2_4 >= 39 && LA2_4 <= 44)||LA2_4==61) ) {
							alt2=1;
						}
						}
						break;
					case 48:
						{
						int LA2_5 = input.LA(3);
						if ( (LA2_5==53) ) {
							int LA2_13 = input.LA(4);
							if ( (LA2_13==FLOAT||LA2_13==38||(LA2_13 >= 45 && LA2_13 <= 47)) ) {
								int LA2_19 = input.LA(5);
								if ( (LA2_19==55) ) {
									int LA2_25 = input.LA(6);
									if ( (LA2_25==ID) ) {
										int LA2_29 = input.LA(7);
										if ( ((LA2_29 >= 33 && LA2_29 <= 34)||(LA2_29 >= 39 && LA2_29 <= 44)||LA2_29==61) ) {
											alt2=1;
										}
									}
								}
							}
						}
						else if ( ((LA2_5 >= 33 && LA2_5 <= 34)||(LA2_5 >= 39 && LA2_5 <= 44)||LA2_5==61) ) {
							alt2=1;
						}
						}
						break;
					case 50:
						{
						int LA2_6 = input.LA(3);
						if ( (LA2_6==53) ) {
							int LA2_14 = input.LA(4);
							if ( (LA2_14==FLOAT||LA2_14==38||(LA2_14 >= 45 && LA2_14 <= 47)) ) {
								int LA2_20 = input.LA(5);
								if ( (LA2_20==55) ) {
									int LA2_26 = input.LA(6);
									if ( (LA2_26==ID) ) {
										int LA2_30 = input.LA(7);
										if ( ((LA2_30 >= 33 && LA2_30 <= 34)||(LA2_30 >= 39 && LA2_30 <= 44)||LA2_30==61) ) {
											alt2=1;
										}
									}
								}
							}
						}
						else if ( ((LA2_6 >= 33 && LA2_6 <= 34)||(LA2_6 >= 39 && LA2_6 <= 44)||LA2_6==61) ) {
							alt2=1;
						}
						}
						break;
					case 51:
						{
						int LA2_7 = input.LA(3);
						if ( (LA2_7==53) ) {
							int LA2_15 = input.LA(4);
							if ( (LA2_15==FLOAT||LA2_15==38||(LA2_15 >= 45 && LA2_15 <= 47)) ) {
								int LA2_21 = input.LA(5);
								if ( (LA2_21==55) ) {
									int LA2_27 = input.LA(6);
									if ( (LA2_27==ID) ) {
										int LA2_31 = input.LA(7);
										if ( ((LA2_31 >= 33 && LA2_31 <= 34)||(LA2_31 >= 39 && LA2_31 <= 44)||LA2_31==61) ) {
											alt2=1;
										}
									}
								}
							}
						}
						else if ( ((LA2_7 >= 33 && LA2_7 <= 34)||(LA2_7 >= 39 && LA2_7 <= 44)||LA2_7==61) ) {
							alt2=1;
						}
						}
						break;
					case 49:
						{
						int LA2_8 = input.LA(3);
						if ( (LA2_8==53) ) {
							int LA2_16 = input.LA(4);
							if ( (LA2_16==FLOAT||LA2_16==38||(LA2_16 >= 45 && LA2_16 <= 47)) ) {
								int LA2_22 = input.LA(5);
								if ( (LA2_22==55) ) {
									int LA2_28 = input.LA(6);
									if ( (LA2_28==ID) ) {
										int LA2_32 = input.LA(7);
										if ( (LA2_32==53) ) {
											int LA2_33 = input.LA(8);
											if ( (LA2_33==FLOAT||LA2_33==38||(LA2_33 >= 45 && LA2_33 <= 47)) ) {
												int LA2_34 = input.LA(9);
												if ( (LA2_34==54) ) {
													int LA2_35 = input.LA(10);
													if ( ((LA2_35 >= 33 && LA2_35 <= 34)||(LA2_35 >= 39 && LA2_35 <= 44)||LA2_35==61) ) {
														alt2=1;
													}
												}
											}
										}
										else if ( ((LA2_32 >= 33 && LA2_32 <= 34)||(LA2_32 >= 39 && LA2_32 <= 44)||LA2_32==61) ) {
											alt2=1;
										}
									}
								}
							}
						}
						}
						break;
					case 56:
					case 57:
					case 58:
					case 59:
						{
						int LA2_9 = input.LA(3);
						if ( (LA2_9==36) ) {
							int LA2_17 = input.LA(4);
							if ( (LA2_17==37) ) {
								int LA2_23 = input.LA(5);
								if ( ((LA2_23 >= 33 && LA2_23 <= 34)||(LA2_23 >= 39 && LA2_23 <= 44)||LA2_23==61) ) {
									alt2=1;
								}
							}
						}
						}
						break;
					case FLOAT:
						{
						int LA2_10 = input.LA(3);
						if ( ((LA2_10 >= 33 && LA2_10 <= 34)||(LA2_10 >= 39 && LA2_10 <= 44)||LA2_10==61) ) {
							alt2=1;
						}
						}
						break;
					case STRING:
						{
						int LA2_11 = input.LA(3);
						if ( ((LA2_11 >= 33 && LA2_11 <= 34)||(LA2_11 >= 39 && LA2_11 <= 44)||LA2_11==61) ) {
							alt2=1;
						}
						}
						break;
				}
			}
			switch (alt2) {
				case 1 :
					// VcfFilter.g:90:17: '!' ^
					{
					char_literal4=(Token)match(input,32,FOLLOW_32_in_subcondition408); 
					char_literal4_tree = (Object)adaptor.create(char_literal4);
					root_0 = (Object)adaptor.becomeRoot(char_literal4_tree, root_0);

					}
					break;

			}

			// VcfFilter.g:90:24: ( bare | paren )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==FLOAT||LA3_0==ID||LA3_0==STRING||LA3_0==32||(LA3_0 >= 48 && LA3_0 <= 51)||(LA3_0 >= 56 && LA3_0 <= 60)||(LA3_0 >= 62 && LA3_0 <= 66)) ) {
				alt3=1;
			}
			else if ( (LA3_0==36) ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// VcfFilter.g:90:25: bare
					{
					pushFollow(FOLLOW_bare_in_subcondition414);
					bare5=bare();
					state._fsp--;

					adaptor.addChild(root_0, bare5.getTree());

					}
					break;
				case 2 :
					// VcfFilter.g:90:32: paren
					{
					pushFollow(FOLLOW_paren_in_subcondition418);
					paren6=paren();
					state._fsp--;

					adaptor.addChild(root_0, paren6.getTree());

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "subcondition"


	public static class bare_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "bare"
	// VcfFilter.g:91:1: bare : ( unaryExpr | binaryExpr | functionBoolean );
	public final VcfFilterParser.bare_return bare() throws RecognitionException {
		VcfFilterParser.bare_return retval = new VcfFilterParser.bare_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope unaryExpr7 =null;
		ParserRuleReturnScope binaryExpr8 =null;
		ParserRuleReturnScope functionBoolean9 =null;


		try {
			// VcfFilter.g:91:7: ( unaryExpr | binaryExpr | functionBoolean )
			int alt4=3;
			switch ( input.LA(1) ) {
			case 32:
			case 60:
			case 66:
				{
				alt4=1;
				}
				break;
			case ID:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA4_11 = input.LA(3);
					if ( (LA4_11==FLOAT||LA4_11==38||(LA4_11 >= 45 && LA4_11 <= 47)) ) {
						int LA4_18 = input.LA(4);
						if ( (LA4_18==54) ) {
							int LA4_24 = input.LA(5);
							if ( ((LA4_24 >= 33 && LA4_24 <= 34)||(LA4_24 >= 39 && LA4_24 <= 44)) ) {
								alt4=2;
							}
							else if ( (LA4_24==61) ) {
								alt4=3;
							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 4, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 33:
				case 34:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
					{
					alt4=2;
					}
					break;
				case 61:
					{
					alt4=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 48:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA4_13 = input.LA(3);
					if ( (LA4_13==FLOAT||LA4_13==38||(LA4_13 >= 45 && LA4_13 <= 47)) ) {
						int LA4_19 = input.LA(4);
						if ( (LA4_19==55) ) {
							int LA4_25 = input.LA(5);
							if ( (LA4_25==ID) ) {
								int LA4_29 = input.LA(6);
								if ( ((LA4_29 >= 33 && LA4_29 <= 34)||(LA4_29 >= 39 && LA4_29 <= 44)) ) {
									alt4=2;
								}
								else if ( (LA4_29==61) ) {
									alt4=3;
								}

								else {
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 4, 29, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 4, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 13, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 33:
				case 34:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
					{
					alt4=2;
					}
					break;
				case 61:
					{
					alt4=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 50:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA4_14 = input.LA(3);
					if ( (LA4_14==FLOAT||LA4_14==38||(LA4_14 >= 45 && LA4_14 <= 47)) ) {
						int LA4_20 = input.LA(4);
						if ( (LA4_20==55) ) {
							int LA4_26 = input.LA(5);
							if ( (LA4_26==ID) ) {
								int LA4_30 = input.LA(6);
								if ( ((LA4_30 >= 33 && LA4_30 <= 34)||(LA4_30 >= 39 && LA4_30 <= 44)) ) {
									alt4=2;
								}
								else if ( (LA4_30==61) ) {
									alt4=3;
								}

								else {
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 4, 30, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 4, 26, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 14, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 33:
				case 34:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
					{
					alt4=2;
					}
					break;
				case 61:
					{
					alt4=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 51:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA4_15 = input.LA(3);
					if ( (LA4_15==FLOAT||LA4_15==38||(LA4_15 >= 45 && LA4_15 <= 47)) ) {
						int LA4_21 = input.LA(4);
						if ( (LA4_21==55) ) {
							int LA4_27 = input.LA(5);
							if ( (LA4_27==ID) ) {
								int LA4_31 = input.LA(6);
								if ( ((LA4_31 >= 33 && LA4_31 <= 34)||(LA4_31 >= 39 && LA4_31 <= 44)) ) {
									alt4=2;
								}
								else if ( (LA4_31==61) ) {
									alt4=3;
								}

								else {
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 4, 31, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 4, 27, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 15, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 33:
				case 34:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
					{
					alt4=2;
					}
					break;
				case 61:
					{
					alt4=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 49:
				{
				int LA4_6 = input.LA(2);
				if ( (LA4_6==53) ) {
					int LA4_16 = input.LA(3);
					if ( (LA4_16==FLOAT||LA4_16==38||(LA4_16 >= 45 && LA4_16 <= 47)) ) {
						int LA4_22 = input.LA(4);
						if ( (LA4_22==55) ) {
							int LA4_28 = input.LA(5);
							if ( (LA4_28==ID) ) {
								switch ( input.LA(6) ) {
								case 53:
									{
									int LA4_33 = input.LA(7);
									if ( (LA4_33==FLOAT||LA4_33==38||(LA4_33 >= 45 && LA4_33 <= 47)) ) {
										int LA4_34 = input.LA(8);
										if ( (LA4_34==54) ) {
											int LA4_35 = input.LA(9);
											if ( ((LA4_35 >= 33 && LA4_35 <= 34)||(LA4_35 >= 39 && LA4_35 <= 44)) ) {
												alt4=2;
											}
											else if ( (LA4_35==61) ) {
												alt4=3;
											}

											else {
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 4, 35, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

										}

										else {
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 4, 34, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}

									}

									else {
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 4, 33, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case 33:
								case 34:
								case 39:
								case 40:
								case 41:
								case 42:
								case 43:
								case 44:
									{
									alt4=2;
									}
									break;
								case 61:
									{
									alt4=3;
									}
									break;
								default:
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 4, 32, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 4, 28, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 16, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 56:
			case 57:
			case 58:
			case 59:
				{
				int LA4_7 = input.LA(2);
				if ( (LA4_7==36) ) {
					int LA4_17 = input.LA(3);
					if ( (LA4_17==37) ) {
						int LA4_23 = input.LA(4);
						if ( ((LA4_23 >= 33 && LA4_23 <= 34)||(LA4_23 >= 39 && LA4_23 <= 44)) ) {
							alt4=2;
						}
						else if ( (LA4_23==61) ) {
							alt4=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 4, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 4, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FLOAT:
				{
				int LA4_8 = input.LA(2);
				if ( ((LA4_8 >= 33 && LA4_8 <= 34)||(LA4_8 >= 39 && LA4_8 <= 44)) ) {
					alt4=2;
				}
				else if ( (LA4_8==61) ) {
					alt4=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STRING:
				{
				int LA4_9 = input.LA(2);
				if ( ((LA4_9 >= 33 && LA4_9 <= 34)||(LA4_9 >= 39 && LA4_9 <= 44)) ) {
					alt4=2;
				}
				else if ( (LA4_9==61) ) {
					alt4=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 62:
			case 63:
			case 64:
			case 65:
				{
				alt4=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}
			switch (alt4) {
				case 1 :
					// VcfFilter.g:91:9: unaryExpr
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_unaryExpr_in_bare427);
					unaryExpr7=unaryExpr();
					state._fsp--;

					adaptor.addChild(root_0, unaryExpr7.getTree());

					}
					break;
				case 2 :
					// VcfFilter.g:91:21: binaryExpr
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_binaryExpr_in_bare431);
					binaryExpr8=binaryExpr();
					state._fsp--;

					adaptor.addChild(root_0, binaryExpr8.getTree());

					}
					break;
				case 3 :
					// VcfFilter.g:91:34: functionBoolean
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_functionBoolean_in_bare435);
					functionBoolean9=functionBoolean();
					state._fsp--;

					adaptor.addChild(root_0, functionBoolean9.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "bare"


	public static class paren_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "paren"
	// VcfFilter.g:92:1: paren : '(' ! condition ')' !;
	public final VcfFilterParser.paren_return paren() throws RecognitionException {
		VcfFilterParser.paren_return retval = new VcfFilterParser.paren_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token char_literal10=null;
		Token char_literal12=null;
		ParserRuleReturnScope condition11 =null;

		Object char_literal10_tree=null;
		Object char_literal12_tree=null;

		try {
			// VcfFilter.g:92:9: ( '(' ! condition ')' !)
			// VcfFilter.g:92:11: '(' ! condition ')' !
			{
			root_0 = (Object)adaptor.nil();


			char_literal10=(Token)match(input,36,FOLLOW_36_in_paren445); 
			pushFollow(FOLLOW_condition_in_paren448);
			condition11=condition();
			state._fsp--;

			adaptor.addChild(root_0, condition11.getTree());

			char_literal12=(Token)match(input,37,FOLLOW_37_in_paren450); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "paren"


	public static class binaryExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "binaryExpr"
	// VcfFilter.g:95:1: binaryExpr : l= expression o= binOperator r= expression -> ^( OP_BINARY $o $l $r) ;
	public final VcfFilterParser.binaryExpr_return binaryExpr() throws RecognitionException {
		VcfFilterParser.binaryExpr_return retval = new VcfFilterParser.binaryExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope l =null;
		ParserRuleReturnScope o =null;
		ParserRuleReturnScope r =null;

		RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
		RewriteRuleSubtreeStream stream_binOperator=new RewriteRuleSubtreeStream(adaptor,"rule binOperator");

		try {
			// VcfFilter.g:95:12: (l= expression o= binOperator r= expression -> ^( OP_BINARY $o $l $r) )
			// VcfFilter.g:95:14: l= expression o= binOperator r= expression
			{
			pushFollow(FOLLOW_expression_in_binaryExpr462);
			l=expression();
			state._fsp--;

			stream_expression.add(l.getTree());
			pushFollow(FOLLOW_binOperator_in_binaryExpr466);
			o=binOperator();
			state._fsp--;

			stream_binOperator.add(o.getTree());
			pushFollow(FOLLOW_expression_in_binaryExpr470);
			r=expression();
			state._fsp--;

			stream_expression.add(r.getTree());
			// AST REWRITE
			// elements: l, o, r
			// token labels: 
			// rule labels: r, l, retval, o
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_r=new RewriteRuleSubtreeStream(adaptor,"rule r",r!=null?r.getTree():null);
			RewriteRuleSubtreeStream stream_l=new RewriteRuleSubtreeStream(adaptor,"rule l",l!=null?l.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
			RewriteRuleSubtreeStream stream_o=new RewriteRuleSubtreeStream(adaptor,"rule o",o!=null?o.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 95:57: -> ^( OP_BINARY $o $l $r)
			{
				// VcfFilter.g:95:60: ^( OP_BINARY $o $l $r)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OP_BINARY, "OP_BINARY"), root_1);
				adaptor.addChild(root_1, stream_o.nextTree());
				adaptor.addChild(root_1, stream_l.nextTree());
				adaptor.addChild(root_1, stream_r.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "binaryExpr"


	public static class unaryExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "unaryExpr"
	// VcfFilter.g:96:1: unaryExpr : o= uniOperator e= expression -> ^( OP_UNARY $o $e) ;
	public final VcfFilterParser.unaryExpr_return unaryExpr() throws RecognitionException {
		VcfFilterParser.unaryExpr_return retval = new VcfFilterParser.unaryExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope o =null;
		ParserRuleReturnScope e =null;

		RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
		RewriteRuleSubtreeStream stream_uniOperator=new RewriteRuleSubtreeStream(adaptor,"rule uniOperator");

		try {
			// VcfFilter.g:96:11: (o= uniOperator e= expression -> ^( OP_UNARY $o $e) )
			// VcfFilter.g:96:13: o= uniOperator e= expression
			{
			pushFollow(FOLLOW_uniOperator_in_unaryExpr497);
			o=uniOperator();
			state._fsp--;

			stream_uniOperator.add(o.getTree());
			pushFollow(FOLLOW_expression_in_unaryExpr501);
			e=expression();
			state._fsp--;

			stream_expression.add(e.getTree());
			// AST REWRITE
			// elements: e, o
			// token labels: 
			// rule labels: e, retval, o
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
			RewriteRuleSubtreeStream stream_o=new RewriteRuleSubtreeStream(adaptor,"rule o",o!=null?o.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 96:44: -> ^( OP_UNARY $o $e)
			{
				// VcfFilter.g:96:47: ^( OP_UNARY $o $e)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OP_UNARY, "OP_UNARY"), root_1);
				adaptor.addChild(root_1, stream_o.nextTree());
				adaptor.addChild(root_1, stream_e.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "unaryExpr"


	public static class boolOperator_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "boolOperator"
	// VcfFilter.g:99:1: boolOperator : ( '&' | '|' );
	public final VcfFilterParser.boolOperator_return boolOperator() throws RecognitionException {
		VcfFilterParser.boolOperator_return retval = new VcfFilterParser.boolOperator_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set13=null;

		Object set13_tree=null;

		try {
			// VcfFilter.g:99:16: ( '&' | '|' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set13=input.LT(1);
			if ( input.LA(1)==35||input.LA(1)==67 ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set13));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "boolOperator"


	public static class binOperator_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "binOperator"
	// VcfFilter.g:100:1: binOperator : ( '=' | '>=' | '>' | '<=' | '<' | '!=' | '=~' | '!~' );
	public final VcfFilterParser.binOperator_return binOperator() throws RecognitionException {
		VcfFilterParser.binOperator_return retval = new VcfFilterParser.binOperator_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set14=null;

		Object set14_tree=null;

		try {
			// VcfFilter.g:100:15: ( '=' | '>=' | '>' | '<=' | '<' | '!=' | '=~' | '!~' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set14=input.LT(1);
			if ( (input.LA(1) >= 33 && input.LA(1) <= 34)||(input.LA(1) >= 39 && input.LA(1) <= 44) ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set14));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "binOperator"


	public static class uniOperator_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "uniOperator"
	// VcfFilter.g:101:1: uniOperator : ( '!' | 'na' | 'exists' );
	public final VcfFilterParser.uniOperator_return uniOperator() throws RecognitionException {
		VcfFilterParser.uniOperator_return retval = new VcfFilterParser.uniOperator_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set15=null;

		Object set15_tree=null;

		try {
			// VcfFilter.g:101:15: ( '!' | 'na' | 'exists' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set15=input.LT(1);
			if ( input.LA(1)==32||input.LA(1)==60||input.LA(1)==66 ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set15));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "uniOperator"


	public static class expression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "expression"
	// VcfFilter.g:104:1: expression : ( var | functionEntry | literalFloat | literalString );
	public final VcfFilterParser.expression_return expression() throws RecognitionException {
		VcfFilterParser.expression_return retval = new VcfFilterParser.expression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope var16 =null;
		ParserRuleReturnScope functionEntry17 =null;
		ParserRuleReturnScope literalFloat18 =null;
		ParserRuleReturnScope literalString19 =null;


		try {
			// VcfFilter.g:104:12: ( var | functionEntry | literalFloat | literalString )
			int alt5=4;
			switch ( input.LA(1) ) {
			case ID:
			case 48:
			case 49:
			case 50:
			case 51:
				{
				alt5=1;
				}
				break;
			case 56:
			case 57:
			case 58:
			case 59:
				{
				alt5=2;
				}
				break;
			case FLOAT:
				{
				alt5=3;
				}
				break;
			case STRING:
				{
				alt5=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}
			switch (alt5) {
				case 1 :
					// VcfFilter.g:104:14: var
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_var_in_expression604);
					var16=var();
					state._fsp--;

					adaptor.addChild(root_0, var16.getTree());

					}
					break;
				case 2 :
					// VcfFilter.g:105:6: functionEntry
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_functionEntry_in_expression612);
					functionEntry17=functionEntry();
					state._fsp--;

					adaptor.addChild(root_0, functionEntry17.getTree());

					}
					break;
				case 3 :
					// VcfFilter.g:106:6: literalFloat
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_literalFloat_in_expression619);
					literalFloat18=literalFloat();
					state._fsp--;

					adaptor.addChild(root_0, literalFloat18.getTree());

					}
					break;
				case 4 :
					// VcfFilter.g:107:6: literalString
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_literalString_in_expression627);
					literalString19=literalString();
					state._fsp--;

					adaptor.addChild(root_0, literalString19.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expression"


	public static class literalFloat_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "literalFloat"
	// VcfFilter.g:110:1: literalFloat : f= FLOAT -> ^( LITERAL_NUMBER $f) ;
	public final VcfFilterParser.literalFloat_return literalFloat() throws RecognitionException {
		VcfFilterParser.literalFloat_return retval = new VcfFilterParser.literalFloat_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token f=null;

		Object f_tree=null;
		RewriteRuleTokenStream stream_FLOAT=new RewriteRuleTokenStream(adaptor,"token FLOAT");

		try {
			// VcfFilter.g:110:14: (f= FLOAT -> ^( LITERAL_NUMBER $f) )
			// VcfFilter.g:110:16: f= FLOAT
			{
			f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literalFloat638);  
			stream_FLOAT.add(f);

			// AST REWRITE
			// elements: f
			// token labels: f
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_f=new RewriteRuleTokenStream(adaptor,"token f",f);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 110:31: -> ^( LITERAL_NUMBER $f)
			{
				// VcfFilter.g:110:34: ^( LITERAL_NUMBER $f)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITERAL_NUMBER, "LITERAL_NUMBER"), root_1);
				adaptor.addChild(root_1, stream_f.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literalFloat"


	public static class literalString_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "literalString"
	// VcfFilter.g:111:1: literalString : s= STRING -> ^( LITERAL_STRING $s) ;
	public final VcfFilterParser.literalString_return literalString() throws RecognitionException {
		VcfFilterParser.literalString_return retval = new VcfFilterParser.literalString_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token s=null;

		Object s_tree=null;
		RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

		try {
			// VcfFilter.g:111:15: (s= STRING -> ^( LITERAL_STRING $s) )
			// VcfFilter.g:111:17: s= STRING
			{
			s=(Token)match(input,STRING,FOLLOW_STRING_in_literalString663);  
			stream_STRING.add(s);

			// AST REWRITE
			// elements: s
			// token labels: s
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_s=new RewriteRuleTokenStream(adaptor,"token s",s);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 111:32: -> ^( LITERAL_STRING $s)
			{
				// VcfFilter.g:111:35: ^( LITERAL_STRING $s)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITERAL_STRING, "LITERAL_STRING"), root_1);
				adaptor.addChild(root_1, stream_s.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literalString"


	public static class var_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "var"
	// VcfFilter.g:114:1: var : ( varField | varSubfield | varGenotypeSub | varGenotypeSubArray | varEffSub | varLofSub | varNmdSub );
	public final VcfFilterParser.var_return var() throws RecognitionException {
		VcfFilterParser.var_return retval = new VcfFilterParser.var_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope varField20 =null;
		ParserRuleReturnScope varSubfield21 =null;
		ParserRuleReturnScope varGenotypeSub22 =null;
		ParserRuleReturnScope varGenotypeSubArray23 =null;
		ParserRuleReturnScope varEffSub24 =null;
		ParserRuleReturnScope varLofSub25 =null;
		ParserRuleReturnScope varNmdSub26 =null;


		try {
			// VcfFilter.g:114:8: ( varField | varSubfield | varGenotypeSub | varGenotypeSubArray | varEffSub | varLofSub | varNmdSub )
			int alt6=7;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA6_1 = input.LA(2);
				if ( (LA6_1==53) ) {
					alt6=2;
				}
				else if ( (LA6_1==EOF||(LA6_1 >= 33 && LA6_1 <= 35)||LA6_1==37||(LA6_1 >= 39 && LA6_1 <= 44)||LA6_1==61||LA6_1==67) ) {
					alt6=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 48:
				{
				int LA6_2 = input.LA(2);
				if ( (LA6_2==53) ) {
					alt6=5;
				}
				else if ( (LA6_2==EOF||(LA6_2 >= 33 && LA6_2 <= 35)||LA6_2==37||(LA6_2 >= 39 && LA6_2 <= 44)||LA6_2==61||LA6_2==67) ) {
					alt6=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 50:
				{
				int LA6_3 = input.LA(2);
				if ( (LA6_3==53) ) {
					alt6=6;
				}
				else if ( (LA6_3==EOF||(LA6_3 >= 33 && LA6_3 <= 35)||LA6_3==37||(LA6_3 >= 39 && LA6_3 <= 44)||LA6_3==61||LA6_3==67) ) {
					alt6=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 51:
				{
				int LA6_4 = input.LA(2);
				if ( (LA6_4==53) ) {
					alt6=7;
				}
				else if ( (LA6_4==EOF||(LA6_4 >= 33 && LA6_4 <= 35)||LA6_4==37||(LA6_4 >= 39 && LA6_4 <= 44)||LA6_4==61||LA6_4==67) ) {
					alt6=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 49:
				{
				int LA6_5 = input.LA(2);
				if ( (LA6_5==53) ) {
					int LA6_11 = input.LA(3);
					if ( (LA6_11==FLOAT||LA6_11==38||(LA6_11 >= 45 && LA6_11 <= 47)) ) {
						int LA6_12 = input.LA(4);
						if ( (LA6_12==55) ) {
							int LA6_13 = input.LA(5);
							if ( (LA6_13==ID) ) {
								int LA6_14 = input.LA(6);
								if ( (LA6_14==53) ) {
									alt6=4;
								}
								else if ( (LA6_14==EOF||(LA6_14 >= 33 && LA6_14 <= 35)||LA6_14==37||(LA6_14 >= 39 && LA6_14 <= 44)||LA6_14==61||LA6_14==67) ) {
									alt6=3;
								}

								else {
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 6, 14, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 6, 13, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 6, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 6, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 6, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}
			switch (alt6) {
				case 1 :
					// VcfFilter.g:114:10: varField
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varField_in_var691);
					varField20=varField();
					state._fsp--;

					adaptor.addChild(root_0, varField20.getTree());

					}
					break;
				case 2 :
					// VcfFilter.g:114:21: varSubfield
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varSubfield_in_var695);
					varSubfield21=varSubfield();
					state._fsp--;

					adaptor.addChild(root_0, varSubfield21.getTree());

					}
					break;
				case 3 :
					// VcfFilter.g:114:35: varGenotypeSub
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varGenotypeSub_in_var699);
					varGenotypeSub22=varGenotypeSub();
					state._fsp--;

					adaptor.addChild(root_0, varGenotypeSub22.getTree());

					}
					break;
				case 4 :
					// VcfFilter.g:114:52: varGenotypeSubArray
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varGenotypeSubArray_in_var703);
					varGenotypeSubArray23=varGenotypeSubArray();
					state._fsp--;

					adaptor.addChild(root_0, varGenotypeSubArray23.getTree());

					}
					break;
				case 5 :
					// VcfFilter.g:114:74: varEffSub
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varEffSub_in_var707);
					varEffSub24=varEffSub();
					state._fsp--;

					adaptor.addChild(root_0, varEffSub24.getTree());

					}
					break;
				case 6 :
					// VcfFilter.g:114:86: varLofSub
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varLofSub_in_var711);
					varLofSub25=varLofSub();
					state._fsp--;

					adaptor.addChild(root_0, varLofSub25.getTree());

					}
					break;
				case 7 :
					// VcfFilter.g:114:98: varNmdSub
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_varNmdSub_in_var715);
					varNmdSub26=varNmdSub();
					state._fsp--;

					adaptor.addChild(root_0, varNmdSub26.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "var"


	public static class varField_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varField"
	// VcfFilter.g:115:1: varField : (i= ID |i= 'EFF' |i= 'LOF' |i= 'NMD' -> ^( VAR_FIELD $i) );
	public final VcfFilterParser.varField_return varField() throws RecognitionException {
		VcfFilterParser.varField_return retval = new VcfFilterParser.varField_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;

		Object i_tree=null;
		RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");

		try {
			// VcfFilter.g:115:11: (i= ID |i= 'EFF' |i= 'LOF' |i= 'NMD' -> ^( VAR_FIELD $i) )
			int alt7=4;
			switch ( input.LA(1) ) {
			case ID:
				{
				alt7=1;
				}
				break;
			case 48:
				{
				alt7=2;
				}
				break;
			case 50:
				{
				alt7=3;
				}
				break;
			case 51:
				{
				alt7=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}
			switch (alt7) {
				case 1 :
					// VcfFilter.g:115:13: i= ID
					{
					root_0 = (Object)adaptor.nil();


					i=(Token)match(input,ID,FOLLOW_ID_in_varField725); 
					i_tree = (Object)adaptor.create(i);
					adaptor.addChild(root_0, i_tree);

					}
					break;
				case 2 :
					// VcfFilter.g:115:20: i= 'EFF'
					{
					root_0 = (Object)adaptor.nil();


					i=(Token)match(input,48,FOLLOW_48_in_varField731); 
					i_tree = (Object)adaptor.create(i);
					adaptor.addChild(root_0, i_tree);

					}
					break;
				case 3 :
					// VcfFilter.g:115:30: i= 'LOF'
					{
					root_0 = (Object)adaptor.nil();


					i=(Token)match(input,50,FOLLOW_50_in_varField737); 
					i_tree = (Object)adaptor.create(i);
					adaptor.addChild(root_0, i_tree);

					}
					break;
				case 4 :
					// VcfFilter.g:115:40: i= 'NMD'
					{
					i=(Token)match(input,51,FOLLOW_51_in_varField743);  
					stream_51.add(i);

					// AST REWRITE
					// elements: i
					// token labels: i
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 115:50: -> ^( VAR_FIELD $i)
					{
						// VcfFilter.g:115:53: ^( VAR_FIELD $i)
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_FIELD, "VAR_FIELD"), root_1);
						adaptor.addChild(root_1, stream_i.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varField"


	public static class varSubfield_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varSubfield"
	// VcfFilter.g:116:1: varSubfield : i= ID '[' n= index ']' -> ^( VAR_SUBFIELD $i $n) ;
	public final VcfFilterParser.varSubfield_return varSubfield() throws RecognitionException {
		VcfFilterParser.varSubfield_return retval = new VcfFilterParser.varSubfield_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token char_literal27=null;
		Token char_literal28=null;
		ParserRuleReturnScope n =null;

		Object i_tree=null;
		Object char_literal27_tree=null;
		Object char_literal28_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleTokenStream stream_54=new RewriteRuleTokenStream(adaptor,"token 54");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:116:14: (i= ID '[' n= index ']' -> ^( VAR_SUBFIELD $i $n) )
			// VcfFilter.g:116:16: i= ID '[' n= index ']'
			{
			i=(Token)match(input,ID,FOLLOW_ID_in_varSubfield764);  
			stream_ID.add(i);

			char_literal27=(Token)match(input,53,FOLLOW_53_in_varSubfield766);  
			stream_53.add(char_literal27);

			pushFollow(FOLLOW_index_in_varSubfield770);
			n=index();
			state._fsp--;

			stream_index.add(n.getTree());
			char_literal28=(Token)match(input,54,FOLLOW_54_in_varSubfield772);  
			stream_54.add(char_literal28);

			// AST REWRITE
			// elements: n, i
			// token labels: i
			// rule labels: n, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_n=new RewriteRuleSubtreeStream(adaptor,"rule n",n!=null?n.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 116:41: -> ^( VAR_SUBFIELD $i $n)
			{
				// VcfFilter.g:116:44: ^( VAR_SUBFIELD $i $n)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_SUBFIELD, "VAR_SUBFIELD"), root_1);
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_1, stream_n.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varSubfield"


	public static class varGenotype_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varGenotype"
	// VcfFilter.g:117:1: varGenotype : 'GEN' '[' g= index ']' -> ^( VAR_GENOTYPE $g) ;
	public final VcfFilterParser.varGenotype_return varGenotype() throws RecognitionException {
		VcfFilterParser.varGenotype_return retval = new VcfFilterParser.varGenotype_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token string_literal29=null;
		Token char_literal30=null;
		Token char_literal31=null;
		ParserRuleReturnScope g =null;

		Object string_literal29_tree=null;
		Object char_literal30_tree=null;
		Object char_literal31_tree=null;
		RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleTokenStream stream_54=new RewriteRuleTokenStream(adaptor,"token 54");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:117:14: ( 'GEN' '[' g= index ']' -> ^( VAR_GENOTYPE $g) )
			// VcfFilter.g:117:16: 'GEN' '[' g= index ']'
			{
			string_literal29=(Token)match(input,49,FOLLOW_49_in_varGenotype796);  
			stream_49.add(string_literal29);

			char_literal30=(Token)match(input,53,FOLLOW_53_in_varGenotype798);  
			stream_53.add(char_literal30);

			pushFollow(FOLLOW_index_in_varGenotype802);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			char_literal31=(Token)match(input,54,FOLLOW_54_in_varGenotype804);  
			stream_54.add(char_literal31);

			// AST REWRITE
			// elements: g
			// token labels: 
			// rule labels: g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 117:43: -> ^( VAR_GENOTYPE $g)
			{
				// VcfFilter.g:117:46: ^( VAR_GENOTYPE $g)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_GENOTYPE, "VAR_GENOTYPE"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varGenotype"


	public static class varGenotypeSub_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varGenotypeSub"
	// VcfFilter.g:118:1: varGenotypeSub : 'GEN' '[' g= index '].' i= ID -> ^( VAR_GENOTYPE_SUB $g $i) ;
	public final VcfFilterParser.varGenotypeSub_return varGenotypeSub() throws RecognitionException {
		VcfFilterParser.varGenotypeSub_return retval = new VcfFilterParser.varGenotypeSub_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token string_literal32=null;
		Token char_literal33=null;
		Token string_literal34=null;
		ParserRuleReturnScope g =null;

		Object i_tree=null;
		Object string_literal32_tree=null;
		Object char_literal33_tree=null;
		Object string_literal34_tree=null;
		RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
		RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:118:17: ( 'GEN' '[' g= index '].' i= ID -> ^( VAR_GENOTYPE_SUB $g $i) )
			// VcfFilter.g:118:19: 'GEN' '[' g= index '].' i= ID
			{
			string_literal32=(Token)match(input,49,FOLLOW_49_in_varGenotypeSub826);  
			stream_49.add(string_literal32);

			char_literal33=(Token)match(input,53,FOLLOW_53_in_varGenotypeSub828);  
			stream_53.add(char_literal33);

			pushFollow(FOLLOW_index_in_varGenotypeSub832);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			string_literal34=(Token)match(input,55,FOLLOW_55_in_varGenotypeSub834);  
			stream_55.add(string_literal34);

			i=(Token)match(input,ID,FOLLOW_ID_in_varGenotypeSub838);  
			stream_ID.add(i);

			// AST REWRITE
			// elements: i, g
			// token labels: i
			// rule labels: g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 118:50: -> ^( VAR_GENOTYPE_SUB $g $i)
			{
				// VcfFilter.g:118:53: ^( VAR_GENOTYPE_SUB $g $i)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_GENOTYPE_SUB, "VAR_GENOTYPE_SUB"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varGenotypeSub"


	public static class varGenotypeSubArray_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varGenotypeSubArray"
	// VcfFilter.g:119:1: varGenotypeSubArray : 'GEN' '[' g= index '].' i= ID '[' n= index ']' -> ^( VAR_GENOTYPE_SUB_ARRAY $g $i $n) ;
	public final VcfFilterParser.varGenotypeSubArray_return varGenotypeSubArray() throws RecognitionException {
		VcfFilterParser.varGenotypeSubArray_return retval = new VcfFilterParser.varGenotypeSubArray_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token string_literal35=null;
		Token char_literal36=null;
		Token string_literal37=null;
		Token char_literal38=null;
		Token char_literal39=null;
		ParserRuleReturnScope g =null;
		ParserRuleReturnScope n =null;

		Object i_tree=null;
		Object string_literal35_tree=null;
		Object char_literal36_tree=null;
		Object string_literal37_tree=null;
		Object char_literal38_tree=null;
		Object char_literal39_tree=null;
		RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
		RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleTokenStream stream_54=new RewriteRuleTokenStream(adaptor,"token 54");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:119:21: ( 'GEN' '[' g= index '].' i= ID '[' n= index ']' -> ^( VAR_GENOTYPE_SUB_ARRAY $g $i $n) )
			// VcfFilter.g:119:23: 'GEN' '[' g= index '].' i= ID '[' n= index ']'
			{
			string_literal35=(Token)match(input,49,FOLLOW_49_in_varGenotypeSubArray860);  
			stream_49.add(string_literal35);

			char_literal36=(Token)match(input,53,FOLLOW_53_in_varGenotypeSubArray862);  
			stream_53.add(char_literal36);

			pushFollow(FOLLOW_index_in_varGenotypeSubArray866);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			string_literal37=(Token)match(input,55,FOLLOW_55_in_varGenotypeSubArray868);  
			stream_55.add(string_literal37);

			i=(Token)match(input,ID,FOLLOW_ID_in_varGenotypeSubArray872);  
			stream_ID.add(i);

			char_literal38=(Token)match(input,53,FOLLOW_53_in_varGenotypeSubArray875);  
			stream_53.add(char_literal38);

			pushFollow(FOLLOW_index_in_varGenotypeSubArray879);
			n=index();
			state._fsp--;

			stream_index.add(n.getTree());
			char_literal39=(Token)match(input,54,FOLLOW_54_in_varGenotypeSubArray881);  
			stream_54.add(char_literal39);

			// AST REWRITE
			// elements: n, g, i
			// token labels: i
			// rule labels: g, n, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_n=new RewriteRuleSubtreeStream(adaptor,"rule n",n!=null?n.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 119:69: -> ^( VAR_GENOTYPE_SUB_ARRAY $g $i $n)
			{
				// VcfFilter.g:119:72: ^( VAR_GENOTYPE_SUB_ARRAY $g $i $n)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_GENOTYPE_SUB_ARRAY, "VAR_GENOTYPE_SUB_ARRAY"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_1, stream_n.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varGenotypeSubArray"


	public static class varEffSub_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varEffSub"
	// VcfFilter.g:120:1: varEffSub : 'EFF' '[' g= index '].' i= ID -> ^( VAR_EFF_SUB $g $i) ;
	public final VcfFilterParser.varEffSub_return varEffSub() throws RecognitionException {
		VcfFilterParser.varEffSub_return retval = new VcfFilterParser.varEffSub_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token string_literal40=null;
		Token char_literal41=null;
		Token string_literal42=null;
		ParserRuleReturnScope g =null;

		Object i_tree=null;
		Object string_literal40_tree=null;
		Object char_literal41_tree=null;
		Object string_literal42_tree=null;
		RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
		RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:120:12: ( 'EFF' '[' g= index '].' i= ID -> ^( VAR_EFF_SUB $g $i) )
			// VcfFilter.g:120:14: 'EFF' '[' g= index '].' i= ID
			{
			string_literal40=(Token)match(input,48,FOLLOW_48_in_varEffSub905);  
			stream_48.add(string_literal40);

			char_literal41=(Token)match(input,53,FOLLOW_53_in_varEffSub907);  
			stream_53.add(char_literal41);

			pushFollow(FOLLOW_index_in_varEffSub911);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			string_literal42=(Token)match(input,55,FOLLOW_55_in_varEffSub913);  
			stream_55.add(string_literal42);

			i=(Token)match(input,ID,FOLLOW_ID_in_varEffSub917);  
			stream_ID.add(i);

			// AST REWRITE
			// elements: g, i
			// token labels: i
			// rule labels: g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 120:45: -> ^( VAR_EFF_SUB $g $i)
			{
				// VcfFilter.g:120:48: ^( VAR_EFF_SUB $g $i)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_EFF_SUB, "VAR_EFF_SUB"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varEffSub"


	public static class varLofSub_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varLofSub"
	// VcfFilter.g:121:1: varLofSub : 'LOF' '[' g= index '].' i= ID -> ^( VAR_LOF_SUB $g $i) ;
	public final VcfFilterParser.varLofSub_return varLofSub() throws RecognitionException {
		VcfFilterParser.varLofSub_return retval = new VcfFilterParser.varLofSub_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token string_literal43=null;
		Token char_literal44=null;
		Token string_literal45=null;
		ParserRuleReturnScope g =null;

		Object i_tree=null;
		Object string_literal43_tree=null;
		Object char_literal44_tree=null;
		Object string_literal45_tree=null;
		RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
		RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:121:12: ( 'LOF' '[' g= index '].' i= ID -> ^( VAR_LOF_SUB $g $i) )
			// VcfFilter.g:121:14: 'LOF' '[' g= index '].' i= ID
			{
			string_literal43=(Token)match(input,50,FOLLOW_50_in_varLofSub940);  
			stream_50.add(string_literal43);

			char_literal44=(Token)match(input,53,FOLLOW_53_in_varLofSub942);  
			stream_53.add(char_literal44);

			pushFollow(FOLLOW_index_in_varLofSub946);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			string_literal45=(Token)match(input,55,FOLLOW_55_in_varLofSub948);  
			stream_55.add(string_literal45);

			i=(Token)match(input,ID,FOLLOW_ID_in_varLofSub952);  
			stream_ID.add(i);

			// AST REWRITE
			// elements: i, g
			// token labels: i
			// rule labels: g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 121:45: -> ^( VAR_LOF_SUB $g $i)
			{
				// VcfFilter.g:121:48: ^( VAR_LOF_SUB $g $i)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_LOF_SUB, "VAR_LOF_SUB"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varLofSub"


	public static class varNmdSub_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "varNmdSub"
	// VcfFilter.g:122:1: varNmdSub : 'NMD' '[' g= index '].' i= ID -> ^( VAR_NMD_SUB $g $i) ;
	public final VcfFilterParser.varNmdSub_return varNmdSub() throws RecognitionException {
		VcfFilterParser.varNmdSub_return retval = new VcfFilterParser.varNmdSub_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token i=null;
		Token string_literal46=null;
		Token char_literal47=null;
		Token string_literal48=null;
		ParserRuleReturnScope g =null;

		Object i_tree=null;
		Object string_literal46_tree=null;
		Object char_literal47_tree=null;
		Object string_literal48_tree=null;
		RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:122:12: ( 'NMD' '[' g= index '].' i= ID -> ^( VAR_NMD_SUB $g $i) )
			// VcfFilter.g:122:14: 'NMD' '[' g= index '].' i= ID
			{
			string_literal46=(Token)match(input,51,FOLLOW_51_in_varNmdSub975);  
			stream_51.add(string_literal46);

			char_literal47=(Token)match(input,53,FOLLOW_53_in_varNmdSub977);  
			stream_53.add(char_literal47);

			pushFollow(FOLLOW_index_in_varNmdSub981);
			g=index();
			state._fsp--;

			stream_index.add(g.getTree());
			string_literal48=(Token)match(input,55,FOLLOW_55_in_varNmdSub983);  
			stream_55.add(string_literal48);

			i=(Token)match(input,ID,FOLLOW_ID_in_varNmdSub987);  
			stream_ID.add(i);

			// AST REWRITE
			// elements: g, i
			// token labels: i
			// rule labels: g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_i=new RewriteRuleTokenStream(adaptor,"token i",i);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 122:45: -> ^( VAR_NMD_SUB $g $i)
			{
				// VcfFilter.g:122:48: ^( VAR_NMD_SUB $g $i)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR_NMD_SUB, "VAR_NMD_SUB"), root_1);
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_1, stream_i.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "varNmdSub"


	public static class functionEntry_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionEntry"
	// VcfFilter.g:125:1: functionEntry : f= functionEntryName '(' ')' -> ^( FUNCTION_ENTRY $f) ;
	public final VcfFilterParser.functionEntry_return functionEntry() throws RecognitionException {
		VcfFilterParser.functionEntry_return retval = new VcfFilterParser.functionEntry_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token char_literal49=null;
		Token char_literal50=null;
		ParserRuleReturnScope f =null;

		Object char_literal49_tree=null;
		Object char_literal50_tree=null;
		RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
		RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
		RewriteRuleSubtreeStream stream_functionEntryName=new RewriteRuleSubtreeStream(adaptor,"rule functionEntryName");

		try {
			// VcfFilter.g:125:16: (f= functionEntryName '(' ')' -> ^( FUNCTION_ENTRY $f) )
			// VcfFilter.g:125:18: f= functionEntryName '(' ')'
			{
			pushFollow(FOLLOW_functionEntryName_in_functionEntry1014);
			f=functionEntryName();
			state._fsp--;

			stream_functionEntryName.add(f.getTree());
			char_literal49=(Token)match(input,36,FOLLOW_36_in_functionEntry1016);  
			stream_36.add(char_literal49);

			char_literal50=(Token)match(input,37,FOLLOW_37_in_functionEntry1018);  
			stream_37.add(char_literal50);

			// AST REWRITE
			// elements: f
			// token labels: 
			// rule labels: f, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_f=new RewriteRuleSubtreeStream(adaptor,"rule f",f!=null?f.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 125:49: -> ^( FUNCTION_ENTRY $f)
			{
				// VcfFilter.g:125:52: ^( FUNCTION_ENTRY $f)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_ENTRY, "FUNCTION_ENTRY"), root_1);
				adaptor.addChild(root_1, stream_f.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionEntry"


	public static class functionEntryName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionEntryName"
	// VcfFilter.g:126:1: functionEntryName : ( 'countHom' | 'countHet' | 'countVariant' | 'countRef' );
	public final VcfFilterParser.functionEntryName_return functionEntryName() throws RecognitionException {
		VcfFilterParser.functionEntryName_return retval = new VcfFilterParser.functionEntryName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set51=null;

		Object set51_tree=null;

		try {
			// VcfFilter.g:126:19: ( 'countHom' | 'countHet' | 'countVariant' | 'countRef' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set51=input.LT(1);
			if ( (input.LA(1) >= 56 && input.LA(1) <= 59) ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set51));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionEntryName"


	public static class functionBoolean_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionBoolean"
	// VcfFilter.g:129:1: functionBoolean : ( functionGenotypeBool | functionBooleanSet );
	public final VcfFilterParser.functionBoolean_return functionBoolean() throws RecognitionException {
		VcfFilterParser.functionBoolean_return retval = new VcfFilterParser.functionBoolean_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope functionGenotypeBool52 =null;
		ParserRuleReturnScope functionBooleanSet53 =null;


		try {
			// VcfFilter.g:129:18: ( functionGenotypeBool | functionBooleanSet )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( ((LA8_0 >= 62 && LA8_0 <= 65)) ) {
				alt8=1;
			}
			else if ( (LA8_0==FLOAT||LA8_0==ID||LA8_0==STRING||(LA8_0 >= 48 && LA8_0 <= 51)||(LA8_0 >= 56 && LA8_0 <= 59)) ) {
				alt8=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// VcfFilter.g:129:20: functionGenotypeBool
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_functionGenotypeBool_in_functionBoolean1059);
					functionGenotypeBool52=functionGenotypeBool();
					state._fsp--;

					adaptor.addChild(root_0, functionGenotypeBool52.getTree());

					}
					break;
				case 2 :
					// VcfFilter.g:130:7: functionBooleanSet
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_functionBooleanSet_in_functionBoolean1068);
					functionBooleanSet53=functionBooleanSet();
					state._fsp--;

					adaptor.addChild(root_0, functionBooleanSet53.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionBoolean"


	public static class functionBooleanSet_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionBooleanSet"
	// VcfFilter.g:134:1: functionBooleanSet : e= expression f= 'in' 'SET' '[' i= index ']' -> ^( FUNCTION_BOOL_SET $f $i $e) ;
	public final VcfFilterParser.functionBooleanSet_return functionBooleanSet() throws RecognitionException {
		VcfFilterParser.functionBooleanSet_return retval = new VcfFilterParser.functionBooleanSet_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token f=null;
		Token string_literal54=null;
		Token char_literal55=null;
		Token char_literal56=null;
		ParserRuleReturnScope e =null;
		ParserRuleReturnScope i =null;

		Object f_tree=null;
		Object string_literal54_tree=null;
		Object char_literal55_tree=null;
		Object char_literal56_tree=null;
		RewriteRuleTokenStream stream_61=new RewriteRuleTokenStream(adaptor,"token 61");
		RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
		RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
		RewriteRuleTokenStream stream_54=new RewriteRuleTokenStream(adaptor,"token 54");
		RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");

		try {
			// VcfFilter.g:134:20: (e= expression f= 'in' 'SET' '[' i= index ']' -> ^( FUNCTION_BOOL_SET $f $i $e) )
			// VcfFilter.g:134:22: e= expression f= 'in' 'SET' '[' i= index ']'
			{
			pushFollow(FOLLOW_expression_in_functionBooleanSet1084);
			e=expression();
			state._fsp--;

			stream_expression.add(e.getTree());
			f=(Token)match(input,61,FOLLOW_61_in_functionBooleanSet1088);  
			stream_61.add(f);

			string_literal54=(Token)match(input,52,FOLLOW_52_in_functionBooleanSet1090);  
			stream_52.add(string_literal54);

			char_literal55=(Token)match(input,53,FOLLOW_53_in_functionBooleanSet1092);  
			stream_53.add(char_literal55);

			pushFollow(FOLLOW_index_in_functionBooleanSet1096);
			i=index();
			state._fsp--;

			stream_index.add(i.getTree());
			char_literal56=(Token)match(input,54,FOLLOW_54_in_functionBooleanSet1098);  
			stream_54.add(char_literal56);

			// AST REWRITE
			// elements: i, f, e
			// token labels: f
			// rule labels: e, i, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleTokenStream stream_f=new RewriteRuleTokenStream(adaptor,"token f",f);
			RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.getTree():null);
			RewriteRuleSubtreeStream stream_i=new RewriteRuleSubtreeStream(adaptor,"rule i",i!=null?i.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 134:66: -> ^( FUNCTION_BOOL_SET $f $i $e)
			{
				// VcfFilter.g:134:69: ^( FUNCTION_BOOL_SET $f $i $e)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_BOOL_SET, "FUNCTION_BOOL_SET"), root_1);
				adaptor.addChild(root_1, stream_f.nextNode());
				adaptor.addChild(root_1, stream_i.nextTree());
				adaptor.addChild(root_1, stream_e.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionBooleanSet"


	public static class functionGenotypeBool_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionGenotypeBool"
	// VcfFilter.g:137:1: functionGenotypeBool : f= functionGenotypeBoolName '(' g= varGenotype ')' -> ^( FUNCTION_BOOL_GENOTYPE $f $g) ;
	public final VcfFilterParser.functionGenotypeBool_return functionGenotypeBool() throws RecognitionException {
		VcfFilterParser.functionGenotypeBool_return retval = new VcfFilterParser.functionGenotypeBool_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token char_literal57=null;
		Token char_literal58=null;
		ParserRuleReturnScope f =null;
		ParserRuleReturnScope g =null;

		Object char_literal57_tree=null;
		Object char_literal58_tree=null;
		RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
		RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
		RewriteRuleSubtreeStream stream_varGenotype=new RewriteRuleSubtreeStream(adaptor,"rule varGenotype");
		RewriteRuleSubtreeStream stream_functionGenotypeBoolName=new RewriteRuleSubtreeStream(adaptor,"rule functionGenotypeBoolName");

		try {
			// VcfFilter.g:137:22: (f= functionGenotypeBoolName '(' g= varGenotype ')' -> ^( FUNCTION_BOOL_GENOTYPE $f $g) )
			// VcfFilter.g:137:24: f= functionGenotypeBoolName '(' g= varGenotype ')'
			{
			pushFollow(FOLLOW_functionGenotypeBoolName_in_functionGenotypeBool1126);
			f=functionGenotypeBoolName();
			state._fsp--;

			stream_functionGenotypeBoolName.add(f.getTree());
			char_literal57=(Token)match(input,36,FOLLOW_36_in_functionGenotypeBool1128);  
			stream_36.add(char_literal57);

			pushFollow(FOLLOW_varGenotype_in_functionGenotypeBool1132);
			g=varGenotype();
			state._fsp--;

			stream_varGenotype.add(g.getTree());
			char_literal58=(Token)match(input,37,FOLLOW_37_in_functionGenotypeBool1134);  
			stream_37.add(char_literal58);

			// AST REWRITE
			// elements: g, f
			// token labels: 
			// rule labels: f, g, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_f=new RewriteRuleSubtreeStream(adaptor,"rule f",f!=null?f.getTree():null);
			RewriteRuleSubtreeStream stream_g=new RewriteRuleSubtreeStream(adaptor,"rule g",g!=null?g.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 137:73: -> ^( FUNCTION_BOOL_GENOTYPE $f $g)
			{
				// VcfFilter.g:137:76: ^( FUNCTION_BOOL_GENOTYPE $f $g)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_BOOL_GENOTYPE, "FUNCTION_BOOL_GENOTYPE"), root_1);
				adaptor.addChild(root_1, stream_f.nextTree());
				adaptor.addChild(root_1, stream_g.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionGenotypeBool"


	public static class functionGenotypeBoolName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionGenotypeBoolName"
	// VcfFilter.g:138:1: functionGenotypeBoolName : ( 'isHom' | 'isHet' | 'isVariant' | 'isRef' );
	public final VcfFilterParser.functionGenotypeBoolName_return functionGenotypeBoolName() throws RecognitionException {
		VcfFilterParser.functionGenotypeBoolName_return retval = new VcfFilterParser.functionGenotypeBoolName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set59=null;

		Object set59_tree=null;

		try {
			// VcfFilter.g:138:26: ( 'isHom' | 'isHet' | 'isVariant' | 'isRef' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set59=input.LT(1);
			if ( (input.LA(1) >= 62 && input.LA(1) <= 65) ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set59));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionGenotypeBoolName"


	public static class index_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "index"
	// VcfFilter.g:141:1: index : ( FLOAT | '*' | 'ANY' | '?' | 'ALL' );
	public final VcfFilterParser.index_return index() throws RecognitionException {
		VcfFilterParser.index_return retval = new VcfFilterParser.index_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set60=null;

		Object set60_tree=null;

		try {
			// VcfFilter.g:141:9: ( FLOAT | '*' | 'ANY' | '?' | 'ALL' )
			// VcfFilter.g:
			{
			root_0 = (Object)adaptor.nil();


			set60=input.LT(1);
			if ( input.LA(1)==FLOAT||input.LA(1)==38||(input.LA(1) >= 45 && input.LA(1) <= 47) ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set60));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "index"

	// Delegated rules



	public static final BitSet FOLLOW_condition_in_main375 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_subcondition_in_condition392 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000008L});
	public static final BitSet FOLLOW_boolOperator_in_condition395 = new BitSet(new long[]{0xDF0F001100201100L,0x0000000000000007L});
	public static final BitSet FOLLOW_subcondition_in_condition398 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000008L});
	public static final BitSet FOLLOW_32_in_subcondition408 = new BitSet(new long[]{0xDF0F001100201100L,0x0000000000000007L});
	public static final BitSet FOLLOW_bare_in_subcondition414 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_paren_in_subcondition418 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpr_in_bare427 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_binaryExpr_in_bare431 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionBoolean_in_bare435 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_paren445 = new BitSet(new long[]{0xDF0F001100201100L,0x0000000000000007L});
	public static final BitSet FOLLOW_condition_in_paren448 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_paren450 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_binaryExpr462 = new BitSet(new long[]{0x00001F8600000000L});
	public static final BitSet FOLLOW_binOperator_in_binaryExpr466 = new BitSet(new long[]{0x0F0F000000201100L});
	public static final BitSet FOLLOW_expression_in_binaryExpr470 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_uniOperator_in_unaryExpr497 = new BitSet(new long[]{0x0F0F000000201100L});
	public static final BitSet FOLLOW_expression_in_unaryExpr501 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_in_expression604 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionEntry_in_expression612 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literalFloat_in_expression619 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literalString_in_expression627 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_literalFloat638 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_literalString663 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varField_in_var691 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varSubfield_in_var695 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varGenotypeSub_in_var699 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varGenotypeSubArray_in_var703 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varEffSub_in_var707 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varLofSub_in_var711 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_varNmdSub_in_var715 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_varField725 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_48_in_varField731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_50_in_varField737 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_51_in_varField743 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_varSubfield764 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varSubfield766 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varSubfield770 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_varSubfield772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_49_in_varGenotype796 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varGenotype798 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varGenotype802 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_varGenotype804 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_49_in_varGenotypeSub826 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varGenotypeSub828 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varGenotypeSub832 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_55_in_varGenotypeSub834 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_ID_in_varGenotypeSub838 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_49_in_varGenotypeSubArray860 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varGenotypeSubArray862 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varGenotypeSubArray866 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_55_in_varGenotypeSubArray868 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_ID_in_varGenotypeSubArray872 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varGenotypeSubArray875 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varGenotypeSubArray879 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_varGenotypeSubArray881 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_48_in_varEffSub905 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varEffSub907 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varEffSub911 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_55_in_varEffSub913 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_ID_in_varEffSub917 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_50_in_varLofSub940 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varLofSub942 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varLofSub946 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_55_in_varLofSub948 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_ID_in_varLofSub952 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_51_in_varNmdSub975 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_varNmdSub977 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_varNmdSub981 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_55_in_varNmdSub983 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_ID_in_varNmdSub987 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionEntryName_in_functionEntry1014 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_functionEntry1016 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_functionEntry1018 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionGenotypeBool_in_functionBoolean1059 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionBooleanSet_in_functionBoolean1068 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_functionBooleanSet1084 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_61_in_functionBooleanSet1088 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_functionBooleanSet1090 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_functionBooleanSet1092 = new BitSet(new long[]{0x0000E04000000100L});
	public static final BitSet FOLLOW_index_in_functionBooleanSet1096 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_functionBooleanSet1098 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionGenotypeBoolName_in_functionGenotypeBool1126 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_functionGenotypeBool1128 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_varGenotype_in_functionGenotypeBool1132 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_functionGenotypeBool1134 = new BitSet(new long[]{0x0000000000000002L});
}
