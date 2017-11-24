// Generated from CoolLexer.g4 by ANTLR 4.5.3
package cool;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, TYPEID=2, OBJECTID=3, BOOL_CONST=4, INT_CONST=5, STR_CONST=6, 
		LPAREN=7, RPAREN=8, COLON=9, ATSYM=10, SEMICOLON=11, COMMA=12, PLUS=13, 
		MINUS=14, STAR=15, SLASH=16, TILDE=17, LT=18, EQUALS=19, LBRACE=20, RBRACE=21, 
		DOT=22, DARROW=23, LE=24, ASSIGN=25, CLASS=26, ELSE=27, FI=28, IF=29, 
		IN=30, INHERITS=31, LET=32, LOOP=33, POOL=34, THEN=35, WHILE=36, CASE=37, 
		ESAC=38, OF=39, NEW=40, ISVOID=41, NOT=42, TRUE=43, FALSE=44, INV_ID_1=45, 
		UNTERM_STR_CONST=46, WHITESPACE=47, SINGLE_LINE_COMMENT=48, MULTI_LINE_COMMENT=49, 
		NO_TOKEN_FOUND=50, INV_COMMENT_1=51, NEST_START_COMMENT=52, NEST_END_COMMENT=53, 
		COMMENT_CONTENT=54, INV_COMMENT_2=55;
	public static final int START_COMMENT = 1;
	public static String[] modeNames = {
		"DEFAULT_MODE", "START_COMMENT"
	};

	public static final String[] ruleNames = {
		"ERROR", "LPAREN", "RPAREN", "COLON", "ATSYM", "COMMA", "SEMICOLON", "PLUS", 
		"MINUS", "STAR", "SLASH", "TILDE", "LT", "EQUALS", "LBRACE", "RBRACE", 
		"DOT", "DARROW", "LE", "ASSIGN", "CLASS", "ELSE", "FI", "IF", "IN", "INHERITS", 
		"LET", "LOOP", "POOL", "THEN", "WHILE", "CASE", "ESAC", "OF", "NEW", "ISVOID", 
		"NOT", "BOOL_CONST", "TRUE", "FALSE", "INT_CONST", "TYPEID", "OBJECTID", 
		"INV_ID_1", "STR_CONST", "UNTERM_STR_CONST", "WHITESPACE", "SINGLE_LINE_COMMENT", 
		"MULTI_LINE_COMMENT", "NO_TOKEN_FOUND", "INV_COMMENT_1", "NEST_START_COMMENT", 
		"NEST_END_COMMENT", "COMMENT_CONTENT", "INV_COMMENT_2"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, null, null, null, "'('", "')'", "':'", "'@'", 
		"';'", "','", "'+'", "'-'", "'*'", "'/'", "'~'", "'<'", "'='", "'{'", 
		"'}'", "'.'", "'=>'", "'<='", "'<-'", null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, "'*)'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "ERROR", "TYPEID", "OBJECTID", "BOOL_CONST", "INT_CONST", "STR_CONST", 
		"LPAREN", "RPAREN", "COLON", "ATSYM", "SEMICOLON", "COMMA", "PLUS", "MINUS", 
		"STAR", "SLASH", "TILDE", "LT", "EQUALS", "LBRACE", "RBRACE", "DOT", "DARROW", 
		"LE", "ASSIGN", "CLASS", "ELSE", "FI", "IF", "IN", "INHERITS", "LET", 
		"LOOP", "POOL", "THEN", "WHILE", "CASE", "ESAC", "OF", "NEW", "ISVOID", 
		"NOT", "TRUE", "FALSE", "INV_ID_1", "UNTERM_STR_CONST", "WHITESPACE", 
		"SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "NO_TOKEN_FOUND", "INV_COMMENT_1", 
		"NEST_START_COMMENT", "NEST_END_COMMENT", "COMMENT_CONTENT", "INV_COMMENT_2"
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



		/*
			YOU CAN ADD YOUR MEMBER VARIABLES AND METHODS HERE
		*/

		/**
		* Function to report errors.
		* Use this function whenever your lexer encounters any erroneous input
		* DO NOT EDIT THIS FUNCTION
		*/
		public void reportError(String errorString){
			setText(errorString);
			setType(ERROR);
		}

		public void processString() {
			Token t = _factory.create(_tokenFactorySourcePair, _type, _text, _channel, _tokenStartCharIndex, getCharIndex()-1, _tokenStartLine, _tokenStartCharPositionInLine);
			String text = t.getText();
			text = text.substring(1, text.length()-1);
			
			//write your code to test strings here
			StringBuilder new_text = new StringBuilder (0);
			
			// Converting the string with separate escape characters into one string where the escape sequences are merged
			int i = 0;
			while(i < text.length())
			{
				if(text.charAt(i) == '\\')
				{
					if(text.charAt(i+1) == 'b')
					{
						new_text.append('\b');	
					}
					else if(text.charAt(i+1) == 't')
					{
						new_text.append('\t');
					}
					else if(text.charAt(i+1) == 'n')
					{
						new_text.append('\n');
					}
					else if(text.charAt(i+1) == 'f')
					{
						new_text.append('\f');
					}
					else if(text.charAt(i+1) == '\"')
					{
						new_text.append('\"');
					}
					else if(text.charAt(i+1) == '\\')
					{
						new_text.append('\\');
					}
					else
					{
						new_text.append(text.charAt(i+1));
					}
					i = i + 1;
				}
				else
				{
					new_text.append(text.charAt(i));
				}
				i = i + 1;			
			}
			
			String new_text_string = new_text.toString();
			setText(new_text_string);
		}


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

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
		case 0:
			ERROR_action((RuleContext)_localctx, actionIndex);
			break;
		case 43:
			INV_ID_1_action((RuleContext)_localctx, actionIndex);
			break;
		case 44:
			STR_CONST_action((RuleContext)_localctx, actionIndex);
			break;
		case 49:
			NO_TOKEN_FOUND_action((RuleContext)_localctx, actionIndex);
			break;
		case 50:
			INV_COMMENT_1_action((RuleContext)_localctx, actionIndex);
			break;
		case 54:
			INV_COMMENT_2_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void ERROR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 reportError("EOF in string constant"); 
			break;
		case 1:
			 reportError("Unterminated string constant"); 
			break;
		case 2:
			 reportError("String contains null character"); 
			break;
		}
	}
	private void INV_ID_1_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:

							reportError("Invalid Identifier");
						
			break;
		}
	}
	private void STR_CONST_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:

							processString();
							String s = getText();
							if(s.length() > 1024)
							{
								reportError("String constant too long");
								setType(ERROR);
							}
						
			break;
		}
	}
	private void NO_TOKEN_FOUND_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:

								String s = getText();
								reportError(s);
							
			break;
		}
	}
	private void INV_COMMENT_1_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:

								reportError("Unmatched *)");
							
			break;
		}
	}
	private void INV_COMMENT_2_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:

								reportError("EOF in comment");
							
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\29\u0177\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\u0087\n\2\3\3"+
		"\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13"+
		"\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\35"+
		"\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37"+
		"\3 \3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3$\3$\3"+
		"$\3$\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\5\'\u0106\n\'\3(\3(\3(\3"+
		"(\3(\3)\3)\3)\3)\3)\3)\3*\6*\u0114\n*\r*\16*\u0115\3+\3+\7+\u011a\n+\f"+
		"+\16+\u011d\13+\3,\3,\7,\u0121\n,\f,\16,\u0124\13,\3-\3-\7-\u0128\n-\f"+
		"-\16-\u012b\13-\3-\3-\3.\3.\3.\3.\3/\3/\3/\3/\3/\5/\u0138\n/\7/\u013a"+
		"\n/\f/\16/\u013d\13/\3\60\6\60\u0140\n\60\r\60\16\60\u0141\3\60\3\60\3"+
		"\61\3\61\3\61\3\61\7\61\u014a\n\61\f\61\16\61\u014d\13\61\3\61\3\61\3"+
		"\61\3\61\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3"+
		"\64\5\64\u0160\n\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66"+
		"\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\38\38\38\38\3\u014b\29\4\3\6"+
		"\t\b\n\n\13\f\f\16\16\20\r\22\17\24\20\26\21\30\22\32\23\34\24\36\25 "+
		"\26\"\27$\30&\31(\32*\33,\34.\35\60\36\62\37\64 \66!8\":#<$>%@&B\'D(F"+
		")H*J+L,N\6P-R.T\7V\4X\5Z/\\\b^\60`\61b\62d\63f\64h\65j\66l\67n8p9\4\2"+
		"\3\35\4\2EEee\4\2NNnn\4\2CCcc\4\2UUuu\4\2GGgg\4\2HHhh\4\2KKkk\4\2PPpp"+
		"\4\2JJjj\4\2TTtt\4\2VVvv\4\2QQqq\4\2RRrr\4\2YYyy\4\2XXxx\4\2FFff\3\2v"+
		"v\4\2WWww\3\2hh\3\2\62;\3\2C\\\6\2\62;C\\aac|\3\2c|\4\2\62;aa\5\2\f\f"+
		"$$^^\4\2\13\17\"\"\3\3\f\f\u0182\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2"+
		"\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2"+
		"\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2"+
		"\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2"+
		",\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2"+
		"\2\28\3\2\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2"+
		"D\3\2\2\2\2F\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3"+
		"\2\2\2\2R\3\2\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2"+
		"\2\2\2^\3\2\2\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2h\3\2\2\2"+
		"\3j\3\2\2\2\3l\3\2\2\2\3n\3\2\2\2\3p\3\2\2\2\4\u0086\3\2\2\2\6\u0088\3"+
		"\2\2\2\b\u008a\3\2\2\2\n\u008c\3\2\2\2\f\u008e\3\2\2\2\16\u0090\3\2\2"+
		"\2\20\u0092\3\2\2\2\22\u0094\3\2\2\2\24\u0096\3\2\2\2\26\u0098\3\2\2\2"+
		"\30\u009a\3\2\2\2\32\u009c\3\2\2\2\34\u009e\3\2\2\2\36\u00a0\3\2\2\2 "+
		"\u00a2\3\2\2\2\"\u00a4\3\2\2\2$\u00a6\3\2\2\2&\u00a8\3\2\2\2(\u00ab\3"+
		"\2\2\2*\u00ae\3\2\2\2,\u00b1\3\2\2\2.\u00b7\3\2\2\2\60\u00bc\3\2\2\2\62"+
		"\u00bf\3\2\2\2\64\u00c2\3\2\2\2\66\u00c5\3\2\2\28\u00ce\3\2\2\2:\u00d2"+
		"\3\2\2\2<\u00d7\3\2\2\2>\u00dc\3\2\2\2@\u00e1\3\2\2\2B\u00e7\3\2\2\2D"+
		"\u00ec\3\2\2\2F\u00f1\3\2\2\2H\u00f4\3\2\2\2J\u00f8\3\2\2\2L\u00ff\3\2"+
		"\2\2N\u0105\3\2\2\2P\u0107\3\2\2\2R\u010c\3\2\2\2T\u0113\3\2\2\2V\u0117"+
		"\3\2\2\2X\u011e\3\2\2\2Z\u0125\3\2\2\2\\\u012e\3\2\2\2^\u0132\3\2\2\2"+
		"`\u013f\3\2\2\2b\u0145\3\2\2\2d\u0152\3\2\2\2f\u0158\3\2\2\2h\u015b\3"+
		"\2\2\2j\u0163\3\2\2\2l\u0169\3\2\2\2n\u016f\3\2\2\2p\u0173\3\2\2\2rs\5"+
		"^/\2st\13\2\2\2tu\7\2\2\3uv\b\2\2\2v\u0087\3\2\2\2wx\5^/\2xy\b\2\3\2y"+
		"\u0087\3\2\2\2z{\5^/\2{|\7^\2\2|}\7w\2\2}~\7\62\2\2~\177\7\62\2\2\177"+
		"\u0080\7\62\2\2\u0080\u0081\7\62\2\2\u0081\u0082\3\2\2\2\u0082\u0083\5"+
		"^/\2\u0083\u0084\7$\2\2\u0084\u0085\b\2\4\2\u0085\u0087\3\2\2\2\u0086"+
		"r\3\2\2\2\u0086w\3\2\2\2\u0086z\3\2\2\2\u0087\5\3\2\2\2\u0088\u0089\7"+
		"*\2\2\u0089\7\3\2\2\2\u008a\u008b\7+\2\2\u008b\t\3\2\2\2\u008c\u008d\7"+
		"<\2\2\u008d\13\3\2\2\2\u008e\u008f\7B\2\2\u008f\r\3\2\2\2\u0090\u0091"+
		"\7.\2\2\u0091\17\3\2\2\2\u0092\u0093\7=\2\2\u0093\21\3\2\2\2\u0094\u0095"+
		"\7-\2\2\u0095\23\3\2\2\2\u0096\u0097\7/\2\2\u0097\25\3\2\2\2\u0098\u0099"+
		"\7,\2\2\u0099\27\3\2\2\2\u009a\u009b\7\61\2\2\u009b\31\3\2\2\2\u009c\u009d"+
		"\7\u0080\2\2\u009d\33\3\2\2\2\u009e\u009f\7>\2\2\u009f\35\3\2\2\2\u00a0"+
		"\u00a1\7?\2\2\u00a1\37\3\2\2\2\u00a2\u00a3\7}\2\2\u00a3!\3\2\2\2\u00a4"+
		"\u00a5\7\177\2\2\u00a5#\3\2\2\2\u00a6\u00a7\7\60\2\2\u00a7%\3\2\2\2\u00a8"+
		"\u00a9\7?\2\2\u00a9\u00aa\7@\2\2\u00aa\'\3\2\2\2\u00ab\u00ac\7>\2\2\u00ac"+
		"\u00ad\7?\2\2\u00ad)\3\2\2\2\u00ae\u00af\7>\2\2\u00af\u00b0\7/\2\2\u00b0"+
		"+\3\2\2\2\u00b1\u00b2\t\2\2\2\u00b2\u00b3\t\3\2\2\u00b3\u00b4\t\4\2\2"+
		"\u00b4\u00b5\t\5\2\2\u00b5\u00b6\t\5\2\2\u00b6-\3\2\2\2\u00b7\u00b8\t"+
		"\6\2\2\u00b8\u00b9\t\3\2\2\u00b9\u00ba\t\5\2\2\u00ba\u00bb\t\6\2\2\u00bb"+
		"/\3\2\2\2\u00bc\u00bd\t\7\2\2\u00bd\u00be\t\b\2\2\u00be\61\3\2\2\2\u00bf"+
		"\u00c0\t\b\2\2\u00c0\u00c1\t\7\2\2\u00c1\63\3\2\2\2\u00c2\u00c3\t\b\2"+
		"\2\u00c3\u00c4\t\t\2\2\u00c4\65\3\2\2\2\u00c5\u00c6\t\b\2\2\u00c6\u00c7"+
		"\t\t\2\2\u00c7\u00c8\t\n\2\2\u00c8\u00c9\t\6\2\2\u00c9\u00ca\t\13\2\2"+
		"\u00ca\u00cb\t\b\2\2\u00cb\u00cc\t\f\2\2\u00cc\u00cd\t\5\2\2\u00cd\67"+
		"\3\2\2\2\u00ce\u00cf\t\3\2\2\u00cf\u00d0\t\6\2\2\u00d0\u00d1\t\f\2\2\u00d1"+
		"9\3\2\2\2\u00d2\u00d3\t\3\2\2\u00d3\u00d4\t\r\2\2\u00d4\u00d5\t\r\2\2"+
		"\u00d5\u00d6\t\16\2\2\u00d6;\3\2\2\2\u00d7\u00d8\t\16\2\2\u00d8\u00d9"+
		"\t\r\2\2\u00d9\u00da\t\r\2\2\u00da\u00db\t\3\2\2\u00db=\3\2\2\2\u00dc"+
		"\u00dd\t\f\2\2\u00dd\u00de\t\n\2\2\u00de\u00df\t\6\2\2\u00df\u00e0\t\t"+
		"\2\2\u00e0?\3\2\2\2\u00e1\u00e2\t\17\2\2\u00e2\u00e3\t\n\2\2\u00e3\u00e4"+
		"\t\b\2\2\u00e4\u00e5\t\3\2\2\u00e5\u00e6\t\6\2\2\u00e6A\3\2\2\2\u00e7"+
		"\u00e8\t\2\2\2\u00e8\u00e9\t\4\2\2\u00e9\u00ea\t\5\2\2\u00ea\u00eb\t\6"+
		"\2\2\u00ebC\3\2\2\2\u00ec\u00ed\t\6\2\2\u00ed\u00ee\t\5\2\2\u00ee\u00ef"+
		"\t\4\2\2\u00ef\u00f0\t\2\2\2\u00f0E\3\2\2\2\u00f1\u00f2\t\r\2\2\u00f2"+
		"\u00f3\t\7\2\2\u00f3G\3\2\2\2\u00f4\u00f5\t\t\2\2\u00f5\u00f6\t\6\2\2"+
		"\u00f6\u00f7\t\17\2\2\u00f7I\3\2\2\2\u00f8\u00f9\t\b\2\2\u00f9\u00fa\t"+
		"\5\2\2\u00fa\u00fb\t\20\2\2\u00fb\u00fc\t\r\2\2\u00fc\u00fd\t\b\2\2\u00fd"+
		"\u00fe\t\21\2\2\u00feK\3\2\2\2\u00ff\u0100\t\t\2\2\u0100\u0101\t\r\2\2"+
		"\u0101\u0102\t\f\2\2\u0102M\3\2\2\2\u0103\u0106\5P(\2\u0104\u0106\5R)"+
		"\2\u0105\u0103\3\2\2\2\u0105\u0104\3\2\2\2\u0106O\3\2\2\2\u0107\u0108"+
		"\t\22\2\2\u0108\u0109\t\13\2\2\u0109\u010a\t\23\2\2\u010a\u010b\t\6\2"+
		"\2\u010bQ\3\2\2\2\u010c\u010d\t\24\2\2\u010d\u010e\t\4\2\2\u010e\u010f"+
		"\t\3\2\2\u010f\u0110\t\5\2\2\u0110\u0111\t\6\2\2\u0111S\3\2\2\2\u0112"+
		"\u0114\t\25\2\2\u0113\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0113\3"+
		"\2\2\2\u0115\u0116\3\2\2\2\u0116U\3\2\2\2\u0117\u011b\t\26\2\2\u0118\u011a"+
		"\t\27\2\2\u0119\u0118\3\2\2\2\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2"+
		"\u011b\u011c\3\2\2\2\u011cW\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u0122\t"+
		"\30\2\2\u011f\u0121\t\27\2\2\u0120\u011f\3\2\2\2\u0121\u0124\3\2\2\2\u0122"+
		"\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123Y\3\2\2\2\u0124\u0122\3\2\2\2"+
		"\u0125\u0129\t\31\2\2\u0126\u0128\t\27\2\2\u0127\u0126\3\2\2\2\u0128\u012b"+
		"\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012c\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012c\u012d\b-\5\2\u012d[\3\2\2\2\u012e\u012f\5^/\2\u012f"+
		"\u0130\7$\2\2\u0130\u0131\b.\6\2\u0131]\3\2\2\2\u0132\u013b\7$\2\2\u0133"+
		"\u013a\n\32\2\2\u0134\u0137\7^\2\2\u0135\u0138\13\2\2\2\u0136\u0138\7"+
		"\2\2\3\u0137\u0135\3\2\2\2\u0137\u0136\3\2\2\2\u0138\u013a\3\2\2\2\u0139"+
		"\u0133\3\2\2\2\u0139\u0134\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u0139\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c_\3\2\2\2\u013d\u013b\3\2\2\2\u013e\u0140"+
		"\t\33\2\2\u013f\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u013f\3\2\2\2"+
		"\u0141\u0142\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0144\b\60\7\2\u0144a\3"+
		"\2\2\2\u0145\u0146\7/\2\2\u0146\u0147\7/\2\2\u0147\u014b\3\2\2\2\u0148"+
		"\u014a\13\2\2\2\u0149\u0148\3\2\2\2\u014a\u014d\3\2\2\2\u014b\u014c\3"+
		"\2\2\2\u014b\u0149\3\2\2\2\u014c\u014e\3\2\2\2\u014d\u014b\3\2\2\2\u014e"+
		"\u014f\7\f\2\2\u014f\u0150\3\2\2\2\u0150\u0151\b\61\7\2\u0151c\3\2\2\2"+
		"\u0152\u0153\7*\2\2\u0153\u0154\7,\2\2\u0154\u0155\3\2\2\2\u0155\u0156"+
		"\b\62\b\2\u0156\u0157\b\62\7\2\u0157e\3\2\2\2\u0158\u0159\13\2\2\2\u0159"+
		"\u015a\b\63\t\2\u015ag\3\2\2\2\u015b\u015c\7,\2\2\u015c\u015d\7+\2\2\u015d"+
		"\u015f\3\2\2\2\u015e\u0160\t\34\2\2\u015f\u015e\3\2\2\2\u015f\u0160\3"+
		"\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162\b\64\n\2\u0162i\3\2\2\2\u0163\u0164"+
		"\7*\2\2\u0164\u0165\7,\2\2\u0165\u0166\3\2\2\2\u0166\u0167\b\65\b\2\u0167"+
		"\u0168\b\65\7\2\u0168k\3\2\2\2\u0169\u016a\7,\2\2\u016a\u016b\7+\2\2\u016b"+
		"\u016c\3\2\2\2\u016c\u016d\b\66\13\2\u016d\u016e\b\66\7\2\u016em\3\2\2"+
		"\2\u016f\u0170\13\2\2\2\u0170\u0171\3\2\2\2\u0171\u0172\b\67\7\2\u0172"+
		"o\3\2\2\2\u0173\u0174\13\2\2\2\u0174\u0175\7\2\2\3\u0175\u0176\b8\f\2"+
		"\u0176q\3\2\2\2\20\2\3\u0086\u0105\u0115\u011b\u0122\u0129\u0137\u0139"+
		"\u013b\u0141\u014b\u015f\r\3\2\2\3\2\3\3\2\4\3-\5\3.\6\b\2\2\7\3\2\3\63"+
		"\7\3\64\b\6\2\2\38\t";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}