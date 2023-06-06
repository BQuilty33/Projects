grammar cal;

prog: decl_list function_list main;

decl_list : decl SEMI decl_list | ;

decl : var_decl 
	| const_decl
	;
var_decl : Variable ID COL type;

const_decl : Constant ID COL type ASSIGN expression;

function_list : function function_list | ;

function : type ID LBR parameter_list RBR Is 
	   decl_list
	   Begin
	   statement_block
	   Return LBR expression RBR SEMI
	   End
           ;
type :  Integer
	| Boolean
	| Void 
	;

parameter_list : nemp_parameter_list | ;
				
nemp_parameter_list : ID COL type
			| ID COL type COM nemp_parameter_list
			;
main : Main Begin decl_list statement_block End;

statement_block : statement statement_block | ; 

statement : ID ASSIGN expression SEMI  
		| ID LBR arg_list RBR SEMI
		| Begin statement_block End
		| If condition Begin statement_block End
		| Else Begin statement_block End
		| While condition Begin statement_block End
		| Skip SEMI			
		;
expression : fragmnt binary_arith_op fragmnt
		| LBR expression RBR
		| ID LBR arg_list RBR
		| fragmnt
		;

binary_arith_op : PLUS
		| MINUS
		;

fragmnt : ID
	| MINUS( ID )
	| NUMBER
	| BV
	;

condition : TILDA condition 
		|   LBR condition RBR 
		|   expression comp_op expression
		|   condition ( OR | AMP ) condition
		;
comp_op : EQ
	| NEQ
	| LT
	| LE
	| GT
	| GE
	;
arg_list : nemp_arg_list | ;

nemp_arg_list : ID 
		| ID COM nemp_arg_list
		;
/* Fragments */

fragment A:	'a'|'A';
fragment B:	'b'|'C';
fragment C:	'c'|'C';
fragment D:	'd'|'D';
fragment E:	'e'|'E';
fragment F:	'f'|'F';
fragment G:	'g'|'G';
fragment H:	'h'|'H';
fragment I:	'i'|'I';
fragment J:	'j'|'J';
fragment K:	'k'|'K';
fragment L:	'l'|'L';
fragment M:	'm'|'M';
fragment N:	'n'|'N';
fragment O:	'o'|'O';
fragment P:	'p'|'P';
fragment Q:	'q'|'Q';
fragment R:	'r'|'R';
fragment S:	's'|'S';
fragment T:	't'|'T';
fragment U:	'u'|'U';
fragment V:	'v'|'V';
fragment W:	'w'|'W';
fragment X:	'x'|'X';
fragment Y:	'y'|'Y';
fragment Z:	'z'|'Z';


/* Reserved words */

Variable: 	V A R I A B L E;
Constant:	C O N S T A N T;
Is: 		I S;
Begin:		B E G I N;
Return:		R E T U R N;
End: 		E N D;
Integer:		I N T E G E R;
Boolean: 	B O O L E A N;
Void:		V O I D;
Main: 		M A I N;
If:		I F;
Else:		E L S E;
While:		W H I L E;
Skip:		S K I P;


/* Operators */

SEMI : ';';
PLUS : '+';
MINUS : '-'; 
LBR : '(';
RBR : ')';
COL : ':';
COM : ',';
ASSIGN : ':=';
TILDA : '∼';
AMP : '&';
OR : '|';
EQ : '=';
NEQ : '!=';
LT : '<';
LE : '<=';
GT : '>';
GE : '>=';

NUMBER:                 MINUS? ( Digit |  NonZero Digit+ );
BV: 'true' | 'false';
ID : Letter (Letter | Digit | UnderScore)*;


fragment Letter:        [a-zA-Z];
fragment Digit:         [0-9];
fragment NonZero:       [1-9];
fragment UnderScore:    '_';
fragment Dot:           '.';

/* Skips */
WS:                     [ \t\n\r]+ -> skip;