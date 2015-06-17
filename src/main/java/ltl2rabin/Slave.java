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
        slaveStates.stream().forEach(slaveState -> {
            getAlphabet().stream().forEach(letter -> {
                // fail: Add transitions that move tokens from all Mojmir states in slaveState in a non-accepting sink
                final State toState = slaveState.readLetter(letter);
                slaveState.getLabel().stream().forEach(ms -> {
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
                        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> otherTokens = slaveState.getLabel().stream()
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
        slaveStates.stream()
                .forEach(state -> {
                    getAlphabet().stream().forEach(letter -> {
                        if (state.getLabel().size() > rank && gConjunction.implies(state.getLabel().get(rank).readLetter(letter).getLabel())) {
                            resultBuilder.add(new Transition(state, letter, state.readLetter(letter)));
                        }
                    });
                });
        return resultBuilder.build();
    }

    public List<Formula> succeedingStates(Set<Formula> curlyG, int pi, ImmutableSet<Set<String>> alphabet) { // TODO: Find a better name
        ImmutableList.Builder<Formula> conjunctsBuilder = new ImmutableList.Builder<>();
        PropEquivalenceClass gConjunction;
        if (curlyG.isEmpty()) {
            gConjunction = new PropEquivalenceClass(new Boolean(true));
        } else {
            gConjunction = new PropEquivalenceClass(new And(ImmutableList.copyOf(curlyG)));
        }

        MojmirAutomaton<PropEquivalenceClass, Set<String>> ma = new MojmirAutomatonFactoryFromLTL(alphabet).createFrom(getInitialState().getLabel().get(0).getLabel().getRepresentative());
        ma.getStates().stream().forEach(maState -> {
            if (gConjunction.implies(maState.getLabel())) {
                conjunctsBuilder.add(maState.getLabel().getRepresentative());
            }
        });
        slaveStates.stream().forEach(state -> {
            // get all mojmir states that either have rank >= pi or are accepting
            conjunctsBuilder.addAll(
                state.getLabel().stream().filter(mState -> mState.isAcceptingState(curlyG))
                                                 .map(mState -> eval(mState.getLabel().getRepresentative(), curlyG))
                                                 .collect(Collectors.toList()));
            if (state.getLabel().size() > pi) {
                conjunctsBuilder.addAll(state.getLabel().subList(pi, state.getLabel().size())
                        .stream()
                        .map(mState -> eval(mState.getLabel().getRepresentative(), curlyG))
                        .collect(Collectors.toList()));
            }
        });
        return conjunctsBuilder.build();
    }

    private Formula eval(Formula f, Set<Formula> curlyG) {
        if (f instanceof G) {
            Formula operand = ((G) f).getOperand();
            if (curlyG.contains(operand)) {
                return new G(eval(operand, curlyG));
            }
            else {
                return new Boolean(false);
            }
        }
        else if (f instanceof And) {
            return new And(((And) f).getConjuncts().stream().map(conjunct -> eval(conjunct, curlyG)).collect(Collectors.toList()));
        }
        else if (f instanceof F) {
            return new F(eval(((F) f).getOperand(), curlyG));
        }
        else if (f instanceof Or) {
            return new Or(((Or) f).getDisjuncts().stream().map(disjunct -> eval(disjunct, curlyG)).collect(Collectors.toList()));
        }
        else if (f instanceof U) {
            return new U(eval(((U) f).getLeft(), curlyG), eval(((U) f).getRight(), curlyG));
        }
        else if (f instanceof X) {
            return new X(eval(((X) f).getOperand(), curlyG));
        }
        return f; // Booleans and Variables don't change at all
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
