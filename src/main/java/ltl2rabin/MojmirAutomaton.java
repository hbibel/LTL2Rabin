package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class describes a Mojmir automaton.
 * @param <T> The type of information associated with a state, e.g. its corresponding LTL formula
 * @param <L> The type that represents the "letters", e.g. Set of Strings
 */
public class MojmirAutomaton<T, L> extends Automaton<T, L> {
    private final ImmutableSet<State<T, L>> states;
    private final State<T, L> initialState;
    private final ImmutableSet<L> alphabet;

    public ImmutableSet<State<T, L>> getStates() {
        return states;
    }

    public ImmutableSet<L> getAlphabet() {
        return alphabet;
    }

    public State<T, L> getInitialState() {
        return initialState;
    }

    /**
     *
     * @param states          The immutable set of states. The state parameters have to be the same as the automaton
     *                        parameters.
     * @param initialState    The entry point for the automaton.
     * @param alphabet        The alphabet that was used to construct the automaton.
     */
    public MojmirAutomaton(ImmutableSet<State<T, L>> states, State<T, L> initialState, 
                           ImmutableSet<L> alphabet) {
        this.states = states;
        this.initialState = initialState;
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

        /**
         * A state is accepting, if, for a given set of G formulas <code>curlyG</code>, the conjunct of this set
         * implies the formula the state is labelled with.
         * @param curlyG    The set of formulas of type <code>G</code>
         * @return          True, if the state is accepting for <code>curlyG</code>. False otherwise.
         */
        public boolean isAcceptingState(Set<G> curlyG) {
            PropEquivalenceClass gConjunction;
            if (curlyG.isEmpty()) {
                gConjunction = new PropEquivalenceClass(new ltl2rabin.LTL.Boolean(true));
            } else {
                gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
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
