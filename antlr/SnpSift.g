//-----------------------------------------------------------------------------
//
// Filter parsing grammar
//			Pablo Cingolani
//
//-----------------------------------------------------------------------------
grammar SnpSift;

//-----------------------------------------------------------------------------
// Lexer
//-----------------------------------------------------------------------------

//---
// Fragments
//---

// A digit
fragment DIGIT    : '0'..'9' ;

// A number is a set of digits
fragment NUMBER   : (DIGIT)+;

// A letter
fragment LETTER   : LOWER | UPPER;
fragment LOWER    : 'a'..'z';
fragment UPPER    : 'A'..'Z';

fragment NEWLINE  : ( '\n' | '\r' )+;

// Letter or digit
fragment ALPHANUM : LETTER | DIGIT;

//---
// Lexer entries
//---

// Discard spaces, tabs and newlines
WS             : (' ' | '\t' | '\r' | '\n')+ { skip(); };

// 'C' style single line comments
COMMENT_SL     : '//' ~('\r' | '\n')* NEWLINE	{ skip(); };
COMMENT_HASH   : '#' ~('\r' | '\n')* NEWLINE	{ skip(); };

BOOL_LITERAL   : 'true' | 'false' ;

// FLOAT number (float/double) without any signNUMBER
INT_LITERAL    :   NUMBER ;
FLOAT_LITERAL  :   NUMBER ( '.' NUMBER )? (('e'|'E') ('+'|'-')? NUMBER)? 
                   | 'NaN' | 'NAN'
                   ;

// A string literal
STRING_LITERAL : '\'' ~( '\n' | '\r' | '\'' )* '\'' { setText(getText().substring( 1, getText().length()-1 ) ); } ;

// An identifier.
ID             : (ALPHANUM | '_' | '.' )+;

//-----------------------------------------------------------------------------
// Parser
//-----------------------------------------------------------------------------

// Compilation Unit
compilationUnit : expression EOF;

// In SNpSift, everything is an expression
expression : BOOL_LITERAL                                                                             # literalBool
           | INT_LITERAL                                                                              # literalInt
           | FLOAT_LITERAL                                                                            # literalFloat
           | STRING_LITERAL                                                                           # literalString
           | idx=('ANY' | '*' | 'ALL' | '?')                                                          # literalIndex
           | ID '('(expression (',' expression )*)? ')'                                               # functionCall
           | ID                                                                                       # varReference
           | expression '[' expression ']'                                                            # varReferenceList
           | expression '[' expression '].' expression                                                # varReferenceListSub
           | expression '[' ('ANY' | '*' | 'ALL' | '?') '].' expression                               # varReferenceListSub
           | expression op=('&' | '&&' | '|' | '||' | '^') expression                                 # expressionLogic
           | expression op=('*' | '/' | '%') expression                                               # expressionTimes
           | expression op=('+' | '-') expression                                                     # expressionPlus
           | expression op=('=' | '==' | '!=' | '<' | '<=' | '>' | '>=' | '=~' | '!~' ) expression    # expressionComp
           | op=('!' | '-' | '+') expression                                                          # expressionUnary
           | op=('exists' | 'na' | 'has') expression                                                  # expressionExists
           | expression 'in' 'SET' '[' expression ']'                                                 # expressionSet
           | '(' expression ')'                                                                       # expressionParen
           | expression '?' expression ':' expression                                                 # expressionCond
           ;

