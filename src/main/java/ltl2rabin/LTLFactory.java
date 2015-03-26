package ltl2rabin;

public abstract class LTLFactory<T> {
    public abstract LTLFormula buildLTL(T input);
}
