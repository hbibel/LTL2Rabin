package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MojmirAutomatonListFactory {
    private final ImmutableSet<Set<LTLFormula>> curlyGSet;
    private final ImmutableSet<Set<String>> alphabet;

    public MojmirAutomatonListFactory(Set<Set<LTLFormula>> curlyGSet, ImmutableSet<Set<String>> alphabet) {
        this.alphabet = alphabet;
        if (curlyGSet instanceof ImmutableSet) {
            this.curlyGSet = (ImmutableSet<Set<LTLFormula>>) curlyGSet;
        }
        else {
            this.curlyGSet = ImmutableSet.copyOf(curlyGSet);
        }
    }

    public ImmutableList<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>> createFrom (LTLFormula formula) {
        Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> stateSet = new HashSet<>();


        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(formula);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        stateSet.add(initialState);

        Queue<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);
        // Loop that generates stateSet
        while (!statesToBeAdded.isEmpty()) {
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> temp = statesToBeAdded.poll();
            Map<Set<String>, MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> transitions = new HashMap<>();

            boolean isSink = true;
            for (Set<String> letter : alphabet) {
                LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
                LTLPropEquivalenceClass newLabel = new LTLPropEquivalenceClass(visitor.afG(temp.getLabel().getRepresentative()));

                if (newLabel.equals(temp.getLabel())) {
                    transitions.put(letter, temp);
                    continue;
                }
                // A sink is a state that only has self-loops as outgoing transitions. If temp is a sink, this
                // line never will be reached.
                isSink = false;

                MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = new MojmirAutomaton.State<>(newLabel);

                transitions.put(letter, newState);
                if (!stateSet.add(newState)) {
                    continue; // Remark: stateSet is a set, so no duplicate states will be added, instead false is returned
                }
                statesToBeAdded.offer(newState);
            }
            if (isSink && !(temp == initialState)) {
                temp.setSink(true);
            }
            temp.setTransitions(transitions);
        }
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(stateSet);

        ImmutableList.Builder<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>> mojmirListBuilder = new ImmutableList.Builder<>();
        // Loop that generates acceptance sets
        curlyGSet.forEach(curlyG -> {
            if ((initialState.getLabel().getRepresentative() instanceof LTLGOperator && curlyG.contains(((LTLGOperator) (initialState.getLabel().getRepresentative())).getOperand()))
                    || initialState.getLabel().isTautology()) {
                mojmirListBuilder.add(new MojmirAutomaton<>(states, initialState, states, alphabet));
            }
            else {
                ImmutableSet.Builder<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> acceptingStatesBuilder = new ImmutableSet.Builder<>();
                Queue<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> stateQueue = new ConcurrentLinkedQueue<>();
                stateQueue.add(initialState);
                states.forEach(state -> {
                    // state is accepting if
                    // - state label is in curlyG or
                    // - state label is a tautology (propositionally equivalent to true)
                    if ((state.getLabel().getRepresentative() instanceof LTLGOperator && curlyG.contains(state.getLabel().getRepresentative()))
                            || state.getLabel().isTautology()) {
                        acceptingStatesBuilder.add(state);
                    }
                });
                mojmirListBuilder.add(new MojmirAutomaton<>(states, initialState, acceptingStatesBuilder.build(), alphabet));
            }
        });

        return mojmirListBuilder.build();
    }

}
