package ltl2rabin.LTL;

/**
 * Interface for LTL formulas. A formula that implements this interface can accept an IVisitor.
 * @param <T> The return type of the accept function (and also the return type of the visit function of the IVisitor).
 */
public interface IVisitable<T> {
    T accept(IVisitor<T> visitor);
}
