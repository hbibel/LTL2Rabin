package ltl2rabin.LTL;

import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

public interface ILTLFormulaVisitor<T> {
    T visit(And formula);
    T visit(Boolean formula);
    T visit(F formula);
    T visit(G formula);
    T visit(Or formula);
    T visit(U formula);
    T visit(Variable formula);
    T visit(X formula);
}