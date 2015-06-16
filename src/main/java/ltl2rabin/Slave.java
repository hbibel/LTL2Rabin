package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Slave extends RabinAutomaton<List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>, Set<String>> {
    private final int maxRank;
    private final ImmutableCollection<State> slaveStates;

    public Slave(ImmutableCollection<State> states,
                 State initialState,
                 ImmutableSet<Set<String>> alphabet,
                 int maxRank) {
        super(states, initialState, alphabet);
        slaveStates = states;
        this.maxRank = maxRank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    @Override
    public Slave.State getInitialState() {
        return (State) super.getInitialState();
    }

    @Override
    public State run(List<Set<String>> word) {
        return (State) super.run(word);
    }

    /* public ImmutableSet<Transition> failMerge(int i) {
        return failMergeSucceed.getFirst().get(i); // TODO: Remove
    } */

    public ImmutableSet<Transition> failMerge(int rank, Set<Formula> curlyG) {
        if (rank > maxRank) {
            return ImmutableSet.copyOf(Collections.emptySet());
        }
        ImmutableSet.Builder<Transition> resultBuilder = new ImmutableSet.Builder<>();
        PropEquivalenceClass gConjunction;
        if (curlyG.isEmpty()) {
            gConjunction = new PropEquivalenceClass(new Boolean(true));
        } else {
            gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
        }
        slaveStates.parallelStream().forEach(slaveState -> {
            getAlphabet().parallelStream().forEach(letter -> {
                // fail: Add transitions that move tokens from all Mojmir states in slaveState in a non-accepting sink
                final State toState = slaveState.readLetter(letter);
                slaveState.getLabel().parallelStream().forEach(ms -> {
                    final MojmirAutomaton.State<PropEquivalenceClass, Set<String>> msToState = ms.readLetter(letter);
                    if (msToState.isSink() && !gConjunction.implies(msToState.getLabel())) {
                        resultBuilder.add(new Transition(slaveState, letter, toState));
                    }
                });
                // merge: A token with r < rank moves to the non-accepting state q' and another token also moves there
                final int maxMergeRank = rank > slaveState.getLabel().size() - 1 ? slaveState.getLabel().size() - 1 : rank;
                for (int r = 0; r <= maxMergeRank; r++) {
                    MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mFromState = slaveState.getLabel().get(r); // q
                    MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mToState = mFromState.readLetter(letter); // q'
                    if (mToState.equals(getInitialState().getLabel().get(0))) {
                        resultBuilder.add(new Transition(slaveState, letter, toState));
                    }
                    else {
                        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> otherTokens = slaveState.getLabel().parallelStream()
                                .filter(ms -> !ms.equals(mFromState)) // tokens that don't come from q ...
                                .map(ms -> ms.readLetter(letter))
                                .filter(ms -> ms.equals(mToState)) // ... also move to q'
                                .collect(Collectors.toList());
                        if (otherTokens.size() > 0) {
                            resultBuilder.add(new Transition(slaveState, letter, toState));
                        }
                    }
                }
            });
        });
        return resultBuilder.build();
    }

    /* public ImmutableSet<Transition> succeed(int i) {
        return failMergeSucceed.getSecond().get(i); // TODO: Remove
    } */

    public ImmutableSet<Transition> succeed(int rank, Set<Formula> curlyG) {
        if (rank > maxRank) {
            return ImmutableSet.copyOf(Collections.emptySet());
        }
        ImmutableSet.Builder<Transition> resultBuilder = new ImmutableSet.Builder<>();
        PropEquivalenceClass gConjunction;
        if (curlyG.isEmpty()) {
            gConjunction = new PropEquivalenceClass(new Boolean(true));
        } else {
            gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
        }
        slaveStates.parallelStream()
                .forEach(state -> {
                    getAlphabet().parallelStream().forEach(letter -> {
                        if (state.getLabel().size() > rank && gConjunction.implies(state.getLabel().get(rank).readLetter(letter).getLabel())) {
                            resultBuilder.add(new Transition(state, letter, state.readLetter(letter)));
                        }
                    });
                });
        return resultBuilder.build();
    }

    public PropEquivalenceClass succeedingStates() { // TODO: Find a better name
        return null;
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
