package ltl2rabin;

public interface ILTLFormulaVisitor {
    void visit(LTLAnd formula);
    void visit(LTLBoolean formula);
    void visit(LTLFOperator formula);
    void visit(LTLGOperator formula);
    void visit(LTLOr formula);
    void visit(LTLUOperator formula);
    void visit(LTLVariable formula);
    void visit(LTLXOperator formula);
}