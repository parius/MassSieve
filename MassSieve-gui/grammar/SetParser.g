header {
package gov.nih.nimh.mass_sieve.io;
import gov.nih.nimh.mass_sieve.*;
}

class SetParser extends Parser;

{
    private PeptideCollection pepCollect;

    public void setPeptideCollection(PeptideCollection pc) {
        pepCollect = pc;
    }

    

}

expr returns [PeptideCollection result = new PeptideCollection()]
{PeptideCollection x;}
    :   result=cexpr 
        ( UNION x=cexpr {result = result.Union(x);}
        | DIFF x=cexpr {result = result.Difference(x);}
        | INTERSECT x=cexpr {result = result.Intersection(x);}
        )*
    ;

cexpr returns [PeptideCollection result = new PeptideCollection()]
{PeptideCollection x;}
    :   (x=atom {result = x;}
        | COMPLEMENT x=atom {result = pepCollect.Difference(x);})*
    ;

atom returns [PeptideCollection result = new PeptideCollection()]
{PeptideCollection x;
char subset;}
    :   sv:SET {
            subset=sv.getText().charAt(0);
            switch (subset) {
                case 'm':
                case 'M': { 
                    result = pepCollect.getMascot();
                    break;
                }
                case 'o':
                case 'O': {
                    result = pepCollect.getOmssa();
                    break;
                }
                case 'x':
                case 'X': {
                    result = pepCollect.getXTandem();
                    break;
                }
                case 's':
                case 'S': {
                    result = pepCollect.getSequest();
                    break;
                }
            }
        }
    |   LPAREN x=expr RPAREN {result=x;} 
    ;


class SetLexer extends Lexer;

options { k=2;
          charVocabulary='\u0000'..'\u007F'; // allow ascii
        } 

LPAREN    : '(' ;
RPAREN    : ')' ;
UNION     : '+' ;
DIFF      : '-' ;
INTERSECT : '&' ;
COMPLEMENT: '~' ;
SET       : ('m'|'M'|'o'|'O'|'x'|'X'|'s'|'S') ;
WS        : (' '|'\t')
            {$setType(Token.SKIP);}
          ;
