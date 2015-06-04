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
    private final ImmutableSet<State<T, U>> acceptingStates;
    private int maxRank = -1;

    public ImmutableSet<State<T, U>> getStates() {
        return states;
    }

    public ImmutableSet<U> getAlphabet() {
        return alphabet;
    }

    public int getMaxRank() { // ranks start at 0
        if (-1 == maxRank) {
            int result = states.size() - 1;
            for (State<T, U> state : states) {
                if (state.isSink()) {
                    result -= 1;
                }
            }
            maxRank = result;
            return result;
        }
        return maxRank;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }

    public MojmirAutomaton(ImmutableSet<State<T, U>> states, State<T, U> initialState, ImmutableSet<State<T, U>> acceptingStates,
                           ImmutableSet<U> alphabet) {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
        this.acceptingStates = acceptingStates;
    }

    public boolean isAcceptingState(State<T, U> s) {
        return acceptingStates.contains(s);
    }

    public static class State<R, S> extends Automaton.State<R, S> {
        private final R label;
        private boolean isSink;
        private Map<S, State<R, S>> transitions;

        public State(R label) {
            isSink = false;
            this.label = label;
        }

        public R getLabel() {
            return label;
        }

        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        public boolean isSink() {
            return isSink;
        }

        public void setTransitions(Map<S, State<R, S>> transitions) {
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
