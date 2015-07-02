package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * The abstract base class for all factories that build <code>Automaton</code>s.
 * @param <F> The type of object that is used to create the Automaton from.
 * @param <A> The type of information the automaton is associated with. States of the resulting automaton are labelled
 *            with objects of this type.
 * @param <L> The type of letters the automaton runs on.
 */
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
