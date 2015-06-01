package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public abstract class AutomatonFactory<F, A, L> {
    private final ImmutableSet<Set<String>> alphabet;

    public ImmutableSet<Set<String>> getAlphabet() {
        return alphabet;
    }

    public AutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        this.alphabet = alphabet;
    }

    public abstract Automaton<A, L> createFrom(F from);
}
