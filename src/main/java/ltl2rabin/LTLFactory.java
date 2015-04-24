package ltl2rabin;

import java.util.Set;

public abstract class LTLFactory<T> {
    public abstract Pair<LTLFormula, Set<Set<String>>> buildLTL(T input);
}
