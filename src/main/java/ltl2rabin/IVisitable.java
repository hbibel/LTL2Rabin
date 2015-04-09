package ltl2rabin;

public interface IVisitable {
    void accept(ILTLFormulaVisitor visitor);
}
