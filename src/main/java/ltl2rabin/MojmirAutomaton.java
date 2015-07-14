package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class describes a Mojmir automaton. A description of a Mojmir automaton can be found in the paper
 * "From LTL to Deterministic Automata -- A Safraless Compositional Approach" by Javier Esparza et al.
 * 
 * <p>A Mojmir automaton has a set of states, one initial state, an alphabet and accepting states.
 * 
 * <p>Usually, a <code>MojmirAutomaton</code> is constructed in a {@link MojmirAutomatonFactory}. <i>F</i> (the
 * set of accepting states) is not computed explicitly. To check if a state is accepting, call the
 * {@link ltl2rabin.MojmirAutomaton.State#isAcceptingState(Set)} method.
 * 
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
     * Once initialized, the fields of a <code>MojmirAutomaton</code> can not be changed.
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

    /**
     * A <code>MojmirAutomaton.State</code> additionaly to a {@link ltl2rabin.Automaton.State} has a flag indicating
     * if it is a sink or not.
     *
     * <p>A <code>MojmirAutomaton.State</code> can be accepting or not, depending on a set of G-formulas that are
     * assumed to hold. To check whether a state is accepting or not, call its {@link #isAcceptingState(Set)}
     * method.
     *
     * @see ltl2rabin.Automaton.State
     *
     * @param <R>    The type of the a <code>State</code>'s label
     * @param <S>    The letter format
     */
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

        /**
         * This method should only be called from a Factory or constructor. A <code>State</code> is a sink, if all
         * outgoing transitions are self loops.
         *
         * @param isSink    A boolean value indicating if the <code>State</code> is a sink or not.
         */
        public void setSink(boolean isSink) {
            this.isSink = isSink;
        }

        /**
         * A <code>State</code> is a sink, if all outgoing transitions are self loops.
         *
         * <p><b>Note:</b> This method might return wrong results, if the construction of the {@link MojmirAutomaton}
         * containing this state is not finished yet. It only is safe to call if this <code>State</code> is in a
         * {@link MojmirAutomaton} that has been constructed by a {@link MojmirAutomatonFactory}.
         *
         * @return True, if the <code>State</code> is a sink. False, otherwise.
         */
        public boolean isSink() {
            return isSink;
        }

        /**
         * This method should only be called from a Factory or constructor.
         *
         * @param transitions    All outgoing transitions for an alphabet (&Sigma;) from this state.
         */
        public void setTransitions(Map<S, State<R, S>> transitions) {
            this.transitions = transitions;
        }

        /**
         * A state is accepting, if, for a given set of G formulas <code>curlyG</code>, the conjunct of this set
         * implies the formula the state is labelled with.
         *
         * @param curlyG    A set of formulas of type {@link G}
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

        /**
         * @see ltl2rabin.Automaton.State#readLetter(Object)
         *
         * @param letter    The letter (&nu;) that is consumed by the transition
         * @return The state the transition maps to.
         */
        @Override
        public MojmirAutomaton.State<R, S> readLetter(S letter) {
            return transitions.get(letter);
        }

        /**
         * Two <code>State</code> objects are considered equal, if their labels are equal. Transitions and being a
         * sink does not play a role.
         *
         * @param obj    The other state
         * @return True, if the states are equal.
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (obj.getClass().equals(this.getClass())) && (((State) obj).label.equals(this.label));
        }

        /**
         * The hash codes of two <code>State</code> objects are equal, if their labels are equal.
         * @return A hash value.
         */
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
