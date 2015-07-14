package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.List;
import java.util.Set;

/**
 * A <code>GDRA</code> object represents a generalized deterministic Rabin automaton. It can be constructed by a
 * {@link GDRAFactory}. This class here does not provide any functionality except for storing the set of states, the
 * acceptance sets, etc.
 *
 * <p>This class inherits from the abstract {@link Automaton} class. GDRA states are labeled with a
 * <code>Pair&lt;PropEquivalenceClass, List&lt;SubAutomaton.State&gt;&gt;</code>. Letters are of type
 * <code>Set&lt;String&gt;</code>.
 */
public class GDRA extends RabinAutomaton<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> {
    private final Set<Set<Pair<Set<Transition>, Set<Transition>>>> gdraCondition;

    /**
     * Once created, a GDRA can not be changed.
     * @param states The immutable set of all {@link ltl2rabin.GDRA.State}s of this automaton.
     * @param initialState The initial {@link ltl2rabin.GDRA.State}
     * @param rabinCondition The set that represents the generalized rabin condition
     * @param alphabet The alphabet the automaton runs on. A letter has the type <code>Set&lt;String&gt;</code>.
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

    /**
     *
     */
    public static class State extends RabinAutomaton.State<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> {
        /**
         *
         * @param label The label consists of a {@link Pair} of<p>
         *              - The {@link PropEquivalenceClass} that is reached after reading a word <i>w</i> starting from
         *                the initial state<p>
         *              - The List of {@link ltl2rabin.SubAutomaton.State}s that are reached after the corresponding
         *                {@link ltl2rabin.SubAutomaton} also read the word <i>w</i>.
         */
        public State(Pair<PropEquivalenceClass, List<SubAutomaton.State>> label) {
            super(label);
        }

        /**
         * @see ltl2rabin.Automaton.State#readLetter(Object)
         */
        @Override
        public State readLetter(Set<String> letter) {
            return (State) super.readLetter(letter);
        }
    }

    /**
     * @see ltl2rabin.Automaton.Transition
     */
    public static class Transition extends Automaton.Transition<State, Set<String>> {

        protected Transition(State from, Set<String> letter, State to) {
            super(from, letter, to);
        }
    }
}
