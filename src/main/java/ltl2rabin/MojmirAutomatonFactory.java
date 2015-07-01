package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    protected Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> reach(MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState,
                                                                                             Set<Set<String>> alphabet,
                                                                                             Set<Formula> curlyG) {
        PropEquivalenceClass curlyGConjunction;
        if (curlyG.isEmpty()) {
            curlyGConjunction = new PropEquivalenceClass(new Boolean(true));
        }
        else {
            List<Formula> curlyGConjunctList = new ArrayList<>();
            curlyG.forEach(ltlFormula -> {
                curlyGConjunctList.add(new G(ltlFormula));
            });
            curlyGConjunction = new PropEquivalenceClass(new And(curlyGConjunctList));
        }

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
