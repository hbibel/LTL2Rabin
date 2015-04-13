package ltl2rabin;

public interface IVisitable<T> {
    T accept(ILTLFormulaVisitor<T> visitor);
}
