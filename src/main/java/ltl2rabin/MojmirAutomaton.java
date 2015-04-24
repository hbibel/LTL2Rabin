package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Map;

/**
 * This class describes a mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <U> The type that represents the "letters", e.g. Set of Strings
 */
public class MojmirAutomaton<T, U> extends Automaton<T, U> {
    private final ImmutableSet<State<T, U>> states;
    private final State<T, U> initialState;
    private final ImmutableSet<U> alphabet;
    private final int maxRank;

    public ImmutableSet<State<T, U>> getStates() {
        return states;
    }

    public ImmutableSet<U> getAlphabet() {
        return alphabet;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }

    public MojmirAutomaton(ImmutableSet<State<T, U>> states, State<T, U> initialState,
                           ImmutableSet<U> alphabet, int maxRank) {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
        this.maxRank = maxRank;
    }

    public static class State<R, S> extends Automaton.State<R, S> {
        private final R label;
        private final boolean accepting;
        private boolean isSink;
        private Map<S, State> transitions;

        public State(R label, boolean accepting) {
            isSink = false;
            this.accepting = accepting;
            this.label = label;
        }

        public R getLabel() {
            return label;
        }

        public boolean isAccepting() {
            return this.accepting;
        }

        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        public boolean isSink() {
            return isSink;
        }

        public void setTransitions(Map<S, State> transitions) {
            this.transitions = transitions;
        }

        @Override
        public MojmirAutomaton.State<R, S> readLetter(S letter) {
            return transitions.get(letter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (obj.getClass().equals(this.getClass())) && (((State) obj).label.equals(this.label));
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

        @Override
        public String toString() {
            return "state(" + label.toString() + ")";
        }
    }
}
