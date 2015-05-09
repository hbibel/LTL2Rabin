package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MojmirAutomatonFactoryFromLTL extends AutomatonFactory<Pair<LTLFormula, Set<Set<String>>>, LTLPropEquivalenceClass, Set<String>> {

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(Pair<LTLFormula, Set<Set<String>>> from) {
        // TODO: Use Builders for immutable collections
        LTLFormula formula = from.getFirst();
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(from.getSecond());

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(formula);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = reach(initialState, alphabet);
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        return new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), alphabet);
    }

    private Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>,
                 ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reach(MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState,
                                                                                   Set<Set<String>> alphabet) {
        Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = new HashSet<>();
        ImmutableSet.Builder<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> acceptingStatesBuilder = new ImmutableSet.Builder<>();
        states.add(initialState);

        Queue<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);

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

                // state is accepting if
                // - previous state was accepting or
                // - state label is in curlyG or
                // - state label is a tautology (propositionally equivalent to true)
                MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = new MojmirAutomaton.State<>(newLabel);
                if (newLabel.isTautology()) {
                    acceptingStatesBuilder.add(newState);
                }
                transitions.put(letter, newState);
                if (!states.add(newState)) {
                    continue; // Remark: states is a set, so no duplicate states will be added, instead false is returned
                }
                statesToBeAdded.offer(newState);
            }
            if (isSink && !(temp == initialState)) {
                temp.setSink(true);
            }
            temp.setTransitions(transitions);
        }

        return new Pair<>(states, acceptingStatesBuilder.build());
    }
}
