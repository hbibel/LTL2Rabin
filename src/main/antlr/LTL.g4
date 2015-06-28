grammar LTL;

@header {
   package ltl2rabin.parser;
}

formula
    : 'F' formula                   # formulaf
    | 'G' formula                   # formulag
    | 'X' formula                   # formulax
    | orformula                     # formulaorformula
    | atom                          # formulaatom
    ;

orformula
    : andformula ('|' andformula)*
    ;

andformula
    : uformula ('&' uformula)*
    ;

uformula
    : atom ('U' atom)?
    ;

atom 
    : '(' formula ')'
    | Boolean
    | Identifier
    ;

Boolean
    : 'tt'
    | 'ff'
    ;

Identifier
    : '!'?[a-zA-Z]+
    ;

WS : [ \t\n\r]+ -> skip ;
