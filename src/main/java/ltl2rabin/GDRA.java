package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.List;
import java.util.Set;

public class GDRA extends RabinAutomaton<Pair<PropEquivalenceClass, List<Slave.State>>, Set<String>> {


    public GDRA(ImmutableCollection<? extends RabinAutomaton.State<Pair<PropEquivalenceClass, List<Slave.State>>, Set<String>>> states,
                RabinAutomaton.State<Pair<PropEquivalenceClass, List<Slave.State>>, Set<String>> initialState,
                Set<Set<Pair<Set<Transition>, Set<Transition>>>> rabinCondition,
                ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, rabinCondition, alphabet);
    }

    public static class State extends RabinAutomaton.State<Pair<PropEquivalenceClass, List<Slave.State>>, Set<String>> {

        /**
         * @param label the list representing the ranking of the states of the corresponding mojmir automaton.
         *              The elder states come first in the list.
         */
        public State(Pair<PropEquivalenceClass, List<Slave.State>> label) {
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
