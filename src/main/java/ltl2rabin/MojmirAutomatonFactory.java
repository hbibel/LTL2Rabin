package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MojmirAutomatonFactory<F> extends AutomatonFactory<F, PropEquivalenceClassWithBeeDeeDee, Set<String>> {
    private static HashMap<Formula, MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>>> mojmirAutomata = new HashMap<>();

    public MojmirAutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public abstract MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> createFrom(F from);

    protected static void putIntoCache(Formula psi, MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> ma) {
        mojmirAutomata.put(psi, ma);
    }

    protected static MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> getFromCache(Formula psi) {
        return mojmirAutomata.get(psi);
    }

    protected ImmutableSet<MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> reach(MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> initialState,
                                                                                             Set<Set<String>> alphabet) {
        Map<PropEquivalenceClassWithBeeDeeDee, MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> states = new HashMap<>();
        states.put(initialState.getLabel(), initialState);

        Queue<MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> statesToBeExpanded = new ConcurrentLinkedQueue<>();
        statesToBeExpanded.add(initialState);

        while (!statesToBeExpanded.isEmpty()) {
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> temp = statesToBeExpanded.poll();
            Map<Set<String>, MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> transitions = new HashMap<>();

            boolean isSink = true;
            for (Set<String> letter : alphabet) {
                LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
                PropEquivalenceClassWithBeeDeeDee newLabel = new PropEquivalenceClassWithBeeDeeDee(visitor.afG(temp.getLabel().getRepresentative()));

                if (newLabel.equals(temp.getLabel())) {
                    transitions.put(letter, temp);
                    continue;
                }
                // A sink is a state that only has self-loops as outgoing transitions. If temp is a sink, this
                // line never will be reached.
                isSink = false;

                MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> newState = states.get(newLabel);
                if (null == newState) {
                    newState = new MojmirAutomaton.State<>(newLabel);
                    statesToBeExpanded.offer(newState);
                    states.put(newLabel, newState);
                }
                transitions.put(letter, newState);
            }
            if (isSink && !(temp == initialState)) {
                temp.setSink(true);
            }
            temp.setTransitions(transitions);
        }

        return ImmutableSet.copyOf(states.values());
    }
}
