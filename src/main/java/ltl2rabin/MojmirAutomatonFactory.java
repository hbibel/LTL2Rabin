package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MojmirAutomatonFactory<F> extends AutomatonFactory<F, PropEquivalenceClass, Set<String>> {
    private static HashMap<Pair<Formula, Set<Formula>>, MojmirAutomaton<PropEquivalenceClass, Set<String>>> mojmirAutomata = new HashMap<>();

    public MojmirAutomatonFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public abstract MojmirAutomaton<PropEquivalenceClass, Set<String>> createFrom(F from);

    protected static void putIntoCache(Pair<Formula, Set<Formula>> key, MojmirAutomaton<PropEquivalenceClass, Set<String>> ma) {
        mojmirAutomata.put(key, ma);
    }

    protected static MojmirAutomaton<PropEquivalenceClass, Set<String>> getFromCache(Pair<Formula, Set<Formula>> key) {
        return mojmirAutomata.get(key);
    }

    protected Pair<Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>,
            ImmutableSet<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>> reach(MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState,
                                                                                             Set<Set<String>> alphabet,
                                                                                             Set<Formula> curlyG) {
        PropEquivalenceClass curlyGConjunction;
        if (curlyG.isEmpty()) {
            curlyGConjunction = new PropEquivalenceClass(new Boolean(true));
        }
        else {
            List<Formula> curlyGConjuncts = new ArrayList<>();
            curlyG.forEach(ltlFormula -> {
                curlyGConjuncts.add(new G(ltlFormula)); // TODO: Salomon: "Kann es sein, dass du in Zeile 38 ein G zuviel hast?"
            });
            curlyGConjunction = new PropEquivalenceClass(new And(curlyGConjuncts));
        }

        Map<PropEquivalenceClass, MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> states = new HashMap<>();
        ImmutableSet.Builder<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> acceptingStatesBuilder = new ImmutableSet.Builder<>();
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

                // state is accepting if state label
                // - is a tautology (propositionally equivalent to true)
                // - is implied by curlyG
                if (newLabel.isTautology() || curlyGConjunction.implies(newLabel)) {
                    acceptingStatesBuilder.add(newState);
                }
                transitions.put(letter, newState);
            }
            if (isSink && !(temp == initialState)) {
                temp.setSink(true);
            }
            temp.setTransitions(transitions);
        }

        return new Pair<>(ImmutableSet.copyOf(states.values()), acceptingStatesBuilder.build());
    }
}
