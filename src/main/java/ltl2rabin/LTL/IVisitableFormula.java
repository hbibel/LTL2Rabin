package ltl2rabin.LTL;

public interface IVisitableFormula<T> {
    T accept(ILTLFormulaVisitor<T> visitor);
}
