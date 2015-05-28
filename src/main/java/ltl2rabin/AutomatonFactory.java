package ltl2rabin;

import com.google.common.collect.ImmutableSet;

public abstract class AutomatonFactory<F, A, L> {
    public abstract Automaton<A, L> createFrom(F from, ImmutableSet<L> alphabet);
}
