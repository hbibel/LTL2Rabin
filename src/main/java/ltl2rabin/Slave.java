package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.List;
import java.util.Set;

public class Slave extends RabinAutomaton<List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>, Set<String>> {
    private final Pair<ImmutableMap<Integer, ImmutableSet<Transition>>, ImmutableMap<Integer, ImmutableSet<Transition>>> failMergeSucceed;

    public Slave(ImmutableCollection<State> states,
                 State initialState,
                 Pair<ImmutableMap<Integer, ImmutableSet<Transition>>, ImmutableMap<Integer, ImmutableSet<Transition>>> rabinCondition,
                 ImmutableSet<Set<String>> alphabet) {
        super(states, initialState, alphabet);
        this.failMergeSucceed = rabinCondition;
    }

    @Override
    public Slave.State getInitialState() {
        return (State) super.getInitialState();
    }

    @Override
    public State run(List<Set<String>> word) {
        return (State) super.run(word);
    }

    public ImmutableSet<Transition> failMerge(int i) {
        return failMergeSucceed.getFirst().get(i);
    }

    public ImmutableSet<Transition> succeed (int i) {
        return failMergeSucceed.getSecond().get(i);
    }

    public static class State extends RabinAutomaton.State<List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>, Set<String>> {
        /**
         * @param label the list representing the ranking of the states of the corresponding mojmir automaton.
         *              The elder states come first in the list.
         */
        public State(List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> label) {
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
