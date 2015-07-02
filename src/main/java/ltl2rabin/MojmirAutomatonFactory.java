package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is the base class for Factories that create <code>MojmirAutomaton</code> objects. It provides the
 * <code>reach()</code> method that explores the state space from a given initial state
 * @param <F> The type of object that is used to create a <code>MojmirAutomaton</code>.
 */
public abstract class MojmirAutomatonFactory<F> extends AutomatonFactory<F, PropEquivalenceClass, Set<String>> {
    private static HashMap<Formula, MojmirAutomaton<PropEquivalenceClass, Set<String>>> mojmirAutomata = new HashMap<>();

    public MojmirAutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public abstract MojmirAutomaton<PropEquivalenceClass, Set<String>> createFrom(F from);

    protected static void putIntoCache(Formula psi, MojmirAutomaton<PropEquivalenceClass, Set<String>> ma) {
        mojmirAutomata.put(psi, ma);
    }

    protected static MojmirAutomaton<PropEquivalenceClass, Set<String>> getFromCache(Formula psi) {
        return mojmirAutomata.get(psi);
    }

    /**
     * This method creates a complete state set starting from an initial state by reading all possible letters from any
     * possible state.
     * @param initialState    The initial state, labelled with a <code>PropEquivalenceClass</code> that is the starting
     *                        point for the unfolding of all possible formulas.
     * @param alphabet        The alphabet used for the construction. All states in the result will have transitions
     *                        for all letters from this alphabet.
     * @return                The (immutable) set of states.
     */
    protected Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> reach(MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState,
                                                                                             Set<Set<String>> alphabet) {
        Map<PropEquivalenceClass, MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> states = new HashMap<>();
        states.put(initialState.getLabel(), initialState);

        Queue<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> statesToBeExpanded = new ConcurrentLinkedQueue<>();
        statesToBeExpanded.add(initialState);

        while (!statesToBeExpanded.isEmpty()) {
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> temp = statesToBeExpanded.poll();
            Map<Set<String>, MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> transitions = new HashMap<>();

            boolean isSink = true;
            for (Set<String> letter : alphabet) {
                LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
                PropEquivalenceClass newLabel = new PropEquivalenceClass(visitor.afG(temp.getLabel().getRepresentative()));

                if (newLabel.equals(temp.getLabel())) {
                    transitions.put(letter, temp);
                    continue;
                }
                // A sink is a state that only has self-loops as outgoing transitions. If temp is a sink, this
                // line never will be reached.
                isSink = false;

                MojmirAutomaton.State<PropEquivalenceClass, Set<String>> newState = states.get(newLabel);
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
