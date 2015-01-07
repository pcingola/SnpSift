//-----------------------------------------------------------------------------
//
// Filter parsing grammar
//			Pablo Cingolani
//
//-----------------------------------------------------------------------------
grammar VcfFilter;

options {
  // We're going to output an AST.
  output = AST;
}

// Tokens (reserved words)
tokens {
	CONDITION;
	OP_BINARY;
	OP_UNARY;	
	VAR_FIELD;
	VAR_SUBFIELD;
	VAR_GENOTYPE;
	VAR_GENOTYPE_SUB;
	VAR_EFF_SUB;
	VAR_LOF_SUB;
	VAR_NMD_SUB;
	VAR_GENOTYPE_SUB_ARRAY;
	FUNCTION_ENTRY;
	FUNCTION_BOOL_GENOTYPE;
	FUNCTION_BOOL_SET;
	LITERAL_NUMBER;
	LITERAL_STRING;
}

@lexer::header {
package ca.mcgill.mcb.pcingola.snpSift.antlr;
}

@header {
package ca.mcgill.mcb.pcingola.snpSift.antlr;
}


//-----------------------------------------------------------------------------
// Lexer
//-----------------------------------------------------------------------------

// Send runs of space and tab characters to the hidden channel.        
WS		: (' ' | '\t')+ { $channel = HIDDEN; };

// Treat runs of newline characters as a single NEWLINE token.
// On some platforms, newlines are represented by a \n character.
// On others they are represented by a \r and a \n character.
NEWLINE		: ('\r'? '\n')+ { $channel=HIDDEN; };

	
// A number is a set of digits
fragment NUMBER	: (DIGIT)+;

// A DIGIT
fragment DIGIT	: '0'..'9' ;

// A letter
fragment LETTER	: LOWER | UPPER;
fragment LOWER	: 'a'..'z';
fragment UPPER	: 'A'..'Z';

// Letter or digit
fragment ALPHANUM 	:	LETTER | DIGIT;

// 'C' style single line comments
COMMENT_SL : '//' ~('\r' | '\n')* NEWLINE	{ $channel=HIDDEN; };

// FLOAT number (float/double) without any signNUMBER
FLOAT  :   ('+'|'-')? NUMBER ( '.' NUMBER )? (('e'|'E') ('+'|'-')? NUMBER)? ;

// A string literal
 STRING: '\'' ~( '\n' | '\r' | '\'' )* '\'' { setText(getText().substring( 1, getText().length()-1 ) ); } ;

// An identifier.
ID : (ALPHANUM | '_' | '.' )*;

//-----------------------------------------------------------------------------
// Parser
//-----------------------------------------------------------------------------

// FCL file may contain several funcion blocks
main		:	f=condition -> ^(CONDITION $f);

condition	:	subcondition (boolOperator^ subcondition)*;
subcondition	:	('!'^)? (bare | paren);
bare		:	unaryExpr | binaryExpr | functionBoolean ;
paren 		:	'('! condition ')'!;

// Operations always are in parenthesis
binaryExpr	:	l=expression o=binOperator r=expression 			-> ^(OP_BINARY $o $l $r);
unaryExpr	:	o=uniOperator e=expression					-> ^(OP_UNARY $o $e);

// All these return a boolean
boolOperator  	:	'&' | '|';
binOperator  	:	'='  | '>='  | '>' | '<=' | '<'  | '!=' | '=~' | '!~' ;
uniOperator  	: 	'!' | 'na' | 'exists';				

// Variables, functions or literals (these are values
expression	:	var 
			| functionEntry
			| literalFloat 
			| literalString;


literalFloat	:	f=FLOAT								-> ^(LITERAL_NUMBER $f);
literalString	:	s=STRING							-> ^(LITERAL_STRING $s);
	
// Variables
var 			:	varField | varSubfield | varGenotypeSub | varGenotypeSubArray | varEffSub | varLofSub | varNmdSub;
varField		:	i=ID | i='EFF' | i='LOF' | i='NMD'			-> ^(VAR_FIELD $i);
varSubfield		:	i=ID '[' n=index ']'					-> ^(VAR_SUBFIELD $i $n);
varGenotype		:	'GEN' '[' g=index ']' 					-> ^(VAR_GENOTYPE $g);
varGenotypeSub		:	'GEN' '[' g=index '].' i=ID				-> ^(VAR_GENOTYPE_SUB $g $i);
varGenotypeSubArray	:	'GEN' '[' g=index '].' i=ID  '[' n=index ']'		-> ^(VAR_GENOTYPE_SUB_ARRAY $g $i $n);
varEffSub		:	'EFF' '[' g=index '].' i=ID				-> ^(VAR_EFF_SUB $g $i);
varLofSub		:	'LOF' '[' g=index '].' i=ID				-> ^(VAR_LOF_SUB $g $i);
varNmdSub		:	'NMD' '[' g=index '].' i=ID				-> ^(VAR_NMD_SUB $g $i);

// Functions based on the whole VCF entry information
functionEntry		:	f=functionEntryName '(' ')'				-> ^(FUNCTION_ENTRY $f);
functionEntryName	:	'countHom' | 'countHet' | 'countVariant' | 'countRef';

// Boolean functions (return TRUE or FALSE)
functionBoolean		:	functionGenotypeBool 
				| functionBooleanSet
				;

// Function on set
functionBooleanSet	:	e=expression f='in' 'SET' '[' i=index ']' 		-> ^(FUNCTION_BOOL_SET $f $i $e);

// Boolean Genotype functions (return TRUE or FALSE)
functionGenotypeBool	:	f=functionGenotypeBoolName '(' g=varGenotype ')'	-> ^(FUNCTION_BOOL_GENOTYPE $f $g);
functionGenotypeBoolName	:	'isHom' | 'isHet' | 'isVariant' | 'isRef';

// You can use '*' for 'any'
index 		:	FLOAT | '*' | 'ANY' | '?' | 'ALL';
