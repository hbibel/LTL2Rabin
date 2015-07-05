package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.List;
import java.util.Set;

/**
 * A <code>GDRA</code> object represents a generalized deterministic Rabin automaton.
 */
public class GDRA extends RabinAutomaton<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> {
    private final Set<Set<Pair<Set<Transition>, Set<Transition>>>> gdraCondition;

    /**
     *
     * @param states The immutable set of all <code>State</code>s of this automaton.
     * @param initialState The initial <code>State</code>
     * @param rabinCondition The set that represents the generalized rabin condition
     * @param alphabet The alphabet the automaton runs on
     */
    public GDRA(ImmutableSet<? extends RabinAutomaton.State<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>>> states,
                RabinAutomaton.State<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> initialState,
                Set<Set<Pair<Set<Transition>, Set<Transition>>>> rabinCondition,
                ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, alphabet);
        this.gdraCondition = rabinCondition;
    }

    public Set<Set<Pair<Set<Transition>, Set<Transition>>>> getGdraCondition() {
        return gdraCondition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableSet<State> getStates() {
        return (ImmutableSet<State>) super.getStates();
    }

    @Override
    public GDRA.State getInitialState() {
        return (GDRA.State) super.getInitialState();
    }

    @Override
    public ImmutableSet<? extends Set<String>> getAlphabet() {
        return super.getAlphabet();
    }

    public static class State extends RabinAutomaton.State<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> {
        /**
         *
         * @param label The label consists of a <code>Pair</code> of<p>
         *              - The PropEquivalenceClass that is reached after reading a word <i>w</i> starting from the
         *                initial state<p>
         *              - The List of <code>SubAutomaton.State</code>s that are reached after the <code>SubAutomaton</code>
         *                automatons also read the word <i>w</i>.
         */
        public State(Pair<PropEquivalenceClass, List<SubAutomaton.State>> label) {
            super(label);
        }

        @Override
        public State readLetter(Set<String> letter) {
            return (State) super.readLetter(letter);
        }
    }

    public static class Transition extends Automaton.Transition<State, Set<String>> {

        protected Transition(State from, Set<String> letter, State to) {
            super(from, letter, to);
        }
    }
}
