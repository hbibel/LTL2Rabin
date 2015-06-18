package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class describes a mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <U> The type that represents the "letters", e.g. Set of Strings
 */
public class MojmirAutomaton<T, U> extends Automaton<T, U> {
    private final ImmutableSet<State<T, U>> states;
    private final State<T, U> initialState;
    private final ImmutableSet<State<T, U>> acceptingStates; // TODO: Remove everything that has to do with accepting states of Mojmir automata
    private final ImmutableSet<U> alphabet;
    private int maxRank = -1;

    public ImmutableSet<State<T, U>> getStates() {
        return states;
    }

    public ImmutableSet<U> getAlphabet() {
        return alphabet;
    }

    public State<T, U> getInitialState() {
        return initialState;
    }

    public MojmirAutomaton(ImmutableSet<State<T, U>> states, State<T, U> initialState, ImmutableSet<State<T, U>> acceptingStates,
                           ImmutableSet<U> alphabet) {
        this.states = states;
        this.initialState = initialState;
        this.acceptingStates = acceptingStates;
        this.alphabet = alphabet;
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

        public boolean isAcceptingState(Set<Formula> curlyG) {
            PropEquivalenceClass gConjunction;
            if (curlyG.isEmpty()) {
                gConjunction = new PropEquivalenceClass(new ltl2rabin.LTL.Boolean(true));
            } else {
                gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG.stream().map(G::new).collect(Collectors.toList()))));
            }
            return gConjunction.implies((PropEquivalenceClass) label);
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
