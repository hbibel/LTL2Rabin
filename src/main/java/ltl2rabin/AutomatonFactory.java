package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public abstract class AutomatonFactory<F, A, L> {
    public abstract Automaton<A, L> createFrom(F from, ImmutableSet<L> alphabet);
}
