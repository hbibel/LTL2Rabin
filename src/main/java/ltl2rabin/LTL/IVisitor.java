package ltl2rabin.LTL;

/**
 * The interface a visitor for Formula objects must implement.
 * @param <T> The return type of the visit function (and also of the accept function of the IVisitable interface).
 */
public interface IVisitor<T> {
    T visit(And formula);
    T visit(Boolean formula);
    T visit(F formula);
    T visit(G formula);
    T visit(Or formula);
    T visit(U formula);
    T visit(Variable formula);
    T visit(X formula);
}