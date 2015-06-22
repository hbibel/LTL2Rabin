package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClassWithBeeDeeDee;

import java.util.List;
import java.util.Set;

public class GDRA extends RabinAutomaton<Pair<PropEquivalenceClassWithBeeDeeDee, List<Slave.State>>, Set<String>> {
    private final Set<Set<Pair<Set<Transition>, Set<Transition>>>> gdraCondition;

    public GDRA(ImmutableCollection<? extends RabinAutomaton.State<Pair<PropEquivalenceClassWithBeeDeeDee, List<Slave.State>>, Set<String>>> states,
                RabinAutomaton.State<Pair<PropEquivalenceClassWithBeeDeeDee, List<Slave.State>>, Set<String>> initialState,
                Set<Set<Pair<Set<Transition>, Set<Transition>>>> rabinCondition,
                ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, alphabet);
        this.gdraCondition = rabinCondition;
    }

    public Set<Set<Pair<Set<Transition>, Set<Transition>>>> getGdraCondition() {
        return gdraCondition;
    }

    @Override
    public GDRA.State getInitialState() {
        return (GDRA.State) super.getInitialState();
    }

    @Override
    public ImmutableSet<? extends Set<String>> getAlphabet() {
        return super.getAlphabet();
    }

    public static class State extends RabinAutomaton.State<Pair<PropEquivalenceClassWithBeeDeeDee, List<Slave.State>>, Set<String>> {

        /**
         * @param label the list representing the ranking of the states of the corresponding mojmir automaton.
         *              The elder states come first in the list.
         */
        public State(Pair<PropEquivalenceClassWithBeeDeeDee, List<Slave.State>> label) {
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
