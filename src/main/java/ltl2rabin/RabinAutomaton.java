package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;

/*
 * Base class for Rabin Automata. Seems unnecessary, since it does not provide anything more of value than
 * the Automaton class. Thus, this class can be removed in the next refactoring.
 *
 * @param <T> The type of the object that States are labelled with.
 * @param <U> Letters are of this type.
 */
public abstract class RabinAutomaton<T, U extends Collection> extends Automaton<T, U> {
    private final ImmutableSet<? extends State<T, U>> states;
    private final State<T, U> initialState;
    private final ImmutableSet<U> alphabet;

    public RabinAutomaton(ImmutableSet<? extends State<T, U>> states,
                          State<T, U> initialState,
                          ImmutableSet<U> alphabet) {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }

    public ImmutableSet<? extends State<T, U>> getStates() {
        return states;
    }

    public ImmutableSet<? extends U> getAlphabet() {
        return alphabet;
    }

    // TODO: This method only is used in tests, so it should be outsourced into a test class.
    public State<T, U> run(List<U> word) {
        State<T, U> result = initialState;
        for (U nextLetter : word) {
            result = result.readLetter(nextLetter);
        }
        return result;
    }

    public static class State<R, S> extends Automaton.State<R, S> {
        private final R label;
        private Map<S, State<R, S>> transitions = new HashMap<>();

        public void setTransition(S letter, State<R, S> to) {
            transitions.put(letter, to);
        }

        public Map<S, State<R, S>> getTransitions() {
            return transitions;
        }

        public R getLabel() {
            return label;
        }

        public State<R, S> readLetter(S letter) {
            return transitions.get(letter);
        }

        public State(R label) {
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State<?, ?> state = (State<?, ?>) o;
            return Objects.equals(label, state.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }
    }
}
