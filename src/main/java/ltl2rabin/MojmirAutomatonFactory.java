package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MojmirAutomatonFactory<F> extends AutomatonFactory<F, LTLPropEquivalenceClass, Set<String>> {
    private static HashMap<Pair<LTLFormula, Set<LTLFormula>>, MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>> mojmirAutomata = new HashMap<>();

    public MojmirAutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public abstract MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(F from);

    protected static void putIntoCache(Pair<LTLFormula, Set<LTLFormula>> key, MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma) {
        mojmirAutomata.put(key, ma);
    }

    protected static MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> getFromCache(Pair<LTLFormula, Set<LTLFormula>> key) {
        return mojmirAutomata.get(key);
    }

    protected Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>,
            ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reach(MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState,
                                                                                             Set<Set<String>> alphabet,
                                                                                             Set<LTLFormula> curlyG) {
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
                // - state label is in curlyG or
                // - state label is a tautology (propositionally equivalent to true)
                MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = new MojmirAutomaton.State<>(newLabel);
                if (newLabel.isTautology() || curlyG.contains(newLabel)) {
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
