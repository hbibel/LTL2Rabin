package ltl2rabin;

public interface IVisitableFormula<T> {
    T accept(ILTLFormulaVisitor<T> visitor);
}
