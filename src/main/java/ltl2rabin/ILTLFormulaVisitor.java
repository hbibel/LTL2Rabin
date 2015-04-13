package ltl2rabin;

public interface ILTLFormulaVisitor<T> {
    T visit(LTLAnd formula);
    T visit(LTLBoolean formula);
    T visit(LTLFOperator formula);
    T visit(LTLGOperator formula);
    T visit(LTLOr formula);
    T visit(LTLUOperator formula);
    T visit(LTLVariable formula);
    T visit(LTLXOperator formula);
}