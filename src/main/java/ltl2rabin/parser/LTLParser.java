package ltl2rabin.parser;
// Generated from LTL.g4 by ANTLR 4.5
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LTLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, Boolean=9, 
		Identifier=10, WS=11;
	public static final int
		RULE_formula = 0, RULE_formulainparentheses = 1, RULE_orformula = 2, RULE_andformula = 3, 
		RULE_uformula = 4, RULE_atom = 5;
	public static final String[] ruleNames = {
		"formula", "formulainparentheses", "orformula", "andformula", "uformula", 
		"atom"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'F'", "'G'", "'X'", "'('", "')'", "'|'", "'&'", "'U'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "Boolean", "Identifier", 
		"WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "LTL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LTLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FormulaContext extends ParserRuleContext {
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
	 
		public FormulaContext() { }
		public void copyFrom(FormulaContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FormulaxContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public FormulaxContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulax(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulax(this);
		}
	}
	public static class FormulafContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public FormulafContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulaf(this);
		}
	}
	public static class FormulagContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public FormulagContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulag(this);
		}
	}
	public static class FormulaorformulaContext extends FormulaContext {
		public OrformulaContext orformula() {
			return getRuleContext(OrformulaContext.class,0);
		}
		public FormulaorformulaContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulaorformula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulaorformula(this);
		}
	}
	public static class FormulaatomContext extends FormulaContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public FormulaatomContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulaatom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulaatom(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_formula);
		try {
			setState(20);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				_localctx = new FormulafContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(12);
				match(T__0);
				setState(13);
				formula();
				}
				break;
			case 2:
				_localctx = new FormulagContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(14);
				match(T__1);
				setState(15);
				formula();
				}
				break;
			case 3:
				_localctx = new FormulaxContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(16);
				match(T__2);
				setState(17);
				formula();
				}
				break;
			case 4:
				_localctx = new FormulaorformulaContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(18);
				orformula();
				}
				break;
			case 5:
				_localctx = new FormulaatomContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(19);
				atom();
				}
				break;
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

	public static class FormulainparenthesesContext extends ParserRuleContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public FormulainparenthesesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formulainparentheses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterFormulainparentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitFormulainparentheses(this);
		}
	}

	public final FormulainparenthesesContext formulainparentheses() throws RecognitionException {
		FormulainparenthesesContext _localctx = new FormulainparenthesesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_formulainparentheses);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(T__3);
			setState(23);
			formula();
			setState(24);
			match(T__4);
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

	public static class OrformulaContext extends ParserRuleContext {
		public List<AndformulaContext> andformula() {
			return getRuleContexts(AndformulaContext.class);
		}
		public AndformulaContext andformula(int i) {
			return getRuleContext(AndformulaContext.class,i);
		}
		public OrformulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orformula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterOrformula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitOrformula(this);
		}
	}

	public final OrformulaContext orformula() throws RecognitionException {
		OrformulaContext _localctx = new OrformulaContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_orformula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			andformula();
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(27);
				match(T__5);
				setState(28);
				andformula();
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class AndformulaContext extends ParserRuleContext {
		public List<UformulaContext> uformula() {
			return getRuleContexts(UformulaContext.class);
		}
		public UformulaContext uformula(int i) {
			return getRuleContext(UformulaContext.class,i);
		}
		public AndformulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andformula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterAndformula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitAndformula(this);
		}
	}

	public final AndformulaContext andformula() throws RecognitionException {
		AndformulaContext _localctx = new AndformulaContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_andformula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			uformula();
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(35);
				match(T__6);
				setState(36);
				uformula();
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class UformulaContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public UformulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uformula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterUformula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitUformula(this);
		}
	}

	public final UformulaContext uformula() throws RecognitionException {
		UformulaContext _localctx = new UformulaContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_uformula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			atom();
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(43);
				match(T__7);
				setState(44);
				atom();
				}
				}
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class AtomContext extends ParserRuleContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode Boolean() { return getToken(LTLParser.Boolean, 0); }
		public TerminalNode Identifier() { return getToken(LTLParser.Identifier, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LTLListener ) ((LTLListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atom);
		try {
			setState(56);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(50);
				match(T__3);
				setState(51);
				formula();
				setState(52);
				match(T__4);
				}
				break;
			case Boolean:
				enterOuterAlt(_localctx, 2);
				{
				setState(54);
				match(Boolean);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(55);
				match(Identifier);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\r=\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2"+
		"\27\n\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4 \n\4\f\4\16\4#\13\4\3\5\3\5\3"+
		"\5\7\5(\n\5\f\5\16\5+\13\5\3\6\3\6\3\6\7\6\60\n\6\f\6\16\6\63\13\6\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\5\7;\n\7\3\7\2\2\b\2\4\6\b\n\f\2\2?\2\26\3\2\2\2"+
		"\4\30\3\2\2\2\6\34\3\2\2\2\b$\3\2\2\2\n,\3\2\2\2\f:\3\2\2\2\16\17\7\3"+
		"\2\2\17\27\5\2\2\2\20\21\7\4\2\2\21\27\5\2\2\2\22\23\7\5\2\2\23\27\5\2"+
		"\2\2\24\27\5\6\4\2\25\27\5\f\7\2\26\16\3\2\2\2\26\20\3\2\2\2\26\22\3\2"+
		"\2\2\26\24\3\2\2\2\26\25\3\2\2\2\27\3\3\2\2\2\30\31\7\6\2\2\31\32\5\2"+
		"\2\2\32\33\7\7\2\2\33\5\3\2\2\2\34!\5\b\5\2\35\36\7\b\2\2\36 \5\b\5\2"+
		"\37\35\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"\7\3\2\2\2#!\3\2\2\2"+
		"$)\5\n\6\2%&\7\t\2\2&(\5\n\6\2\'%\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2"+
		"\2*\t\3\2\2\2+)\3\2\2\2,\61\5\f\7\2-.\7\n\2\2.\60\5\f\7\2/-\3\2\2\2\60"+
		"\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\13\3\2\2\2\63\61\3\2\2\2\64\65"+
		"\7\6\2\2\65\66\5\2\2\2\66\67\7\7\2\2\67;\3\2\2\28;\7\13\2\29;\7\f\2\2"+
		":\64\3\2\2\2:8\3\2\2\2:9\3\2\2\2;\r\3\2\2\2\7\26!)\61:";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
