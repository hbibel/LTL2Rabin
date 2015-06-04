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
        LTLPropEquivalenceClass curlyGConjunction;
        if (curlyG.isEmpty()) {
            curlyGConjunction = new LTLPropEquivalenceClass(new LTLBoolean(true));
        }
        else {
            List<LTLFormula> curlyGConjuncts = new ArrayList<>();
            curlyG.forEach(ltlFormula -> {
                curlyGConjuncts.add(new LTLGOperator(ltlFormula)); // TODO: Salomon: "Kann es sein, dass du in Zeile 38 ein G zuviel hast?"
            });
            curlyGConjunction = new LTLPropEquivalenceClass(new LTLAnd(curlyGConjuncts));
        }

        Map<LTLPropEquivalenceClass, MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = new HashMap<>();
        ImmutableSet.Builder<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> acceptingStatesBuilder = new ImmutableSet.Builder<>();
        states.put(initialState.getLabel(), initialState);

        Queue<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> statesToBeExpanded = new ConcurrentLinkedQueue<>();
        statesToBeExpanded.add(initialState);

        while (!statesToBeExpanded.isEmpty()) {
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> temp = statesToBeExpanded.poll();
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

                MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = states.get(newLabel);
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
