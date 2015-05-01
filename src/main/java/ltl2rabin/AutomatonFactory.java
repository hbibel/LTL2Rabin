package ltl2rabin;

public abstract class AutomatonFactory<F, A, L> {
    public abstract Automaton<A, L> createFrom(F from);
}
