package ltl2rabin;

public abstract class AutomatonFactory<T, U> {
    public abstract Automaton<T, U> createFrom(T from);
}
