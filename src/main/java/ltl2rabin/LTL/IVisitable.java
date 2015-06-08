package ltl2rabin.LTL;

public interface IVisitable<T> {
    T accept(IVisitor<T> visitor);
}
