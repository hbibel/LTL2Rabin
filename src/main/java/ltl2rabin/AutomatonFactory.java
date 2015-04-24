package ltl2rabin;

public abstract class AutomatonFactory<S, T, U> {
    public abstract Automaton<T, U> createFrom(S from);
}
