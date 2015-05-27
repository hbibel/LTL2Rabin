package ltl2rabin;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRAFactory extends RabinAutomatonFactory<String,
        LTLPropEquivalenceClass,
        Set<String>> {
    private HashMap<LTLPropEquivalenceClass, RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> existingStates;
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL();
    private RabinAutomatonFromMojmirFactory rabinAutomatonFromMojmirFactory = new RabinAutomatonFromMojmirFactory();
    private ImmutableSet<Set<String>> alphabet;

    // the alphabet parameter is irrelevant
    public RabinAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from, ImmutableSet<Set<String>> dontCare) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        ImmutableSet<LTLFormula> gSet = (new ImmutableSet.Builder<LTLFormula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<LTLFormula>> curlyGSets = (new ImmutableSet.Builder<Set<LTLFormula>>())
                .addAll(Sets.powerSet(gSet)).build();
        this.alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        LTLFormula phi = parserResult.getLtlFormula();

        ImmutableSet.Builder<RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> statesBuilder = new ImmutableSet.Builder<>();
        RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState = new RabinAutomaton.State<>(new LTLPropEquivalenceClass(phi));
        statesBuilder.add(initialState);

        Queue<RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);
        while (!statesToBeAdded.isEmpty()) {
            RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> temp = statesToBeAdded.poll();

            for (Set<String> letter : alphabet) {
                LTLAfGVisitor afVisitor = new LTLAfGVisitor(letter) {
                    @Override
                    public LTLFormula visit(LTLGOperator formula) {
                        LTLFormula afOperand = afG(formula.getOperand());
                        return new LTLAnd(formula, afOperand);
                    }
                };
                LTLPropEquivalenceClass newLabel = new LTLPropEquivalenceClass(afVisitor.afG(temp.getLabel().getRepresentative()));

                RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = addOrGet(newLabel);
                if (!statesBuilder.build().contains(newState)) {
                    statesToBeAdded.offer(newState);
                }
                statesBuilder.add(newState);
                temp.setTransition(letter, newState);
            }
        }
        ImmutableSet<RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = statesBuilder.build();


        int maxRank = 0;
        ImmutableSet.Builder<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slavesBuilder = new ImmutableSet.Builder<>();

        ImmutableSet<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slaves
                = slavesBuilder.build();

        Set<RabinAutomaton.Transition> univ = new HashSet<>(); // TODO: UNIV = all transitions?
        Set<Set<Pair<Set, Set>>> acc = new HashSet<>();
        for (Set<LTLFormula> curlyG : curlyGSets) {
            Set<Pair<Set, Set>> accForCurlyG = new HashSet<>();
            Set<RabinAutomaton.State> notInF = new HashSet<>();
            Pair<Set, Set> mPair = new Pair<>(notInF, univ);
            for (RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> tempState : states) {
                List<LTLFormula> conjuncts = new ArrayList<>();
                curlyG.forEach(f -> conjuncts.add(new LTLGOperator(f)));
                LTLAnd prefix = new LTLAnd(conjuncts); // e.g. G(psi1) && G(psi2) && G(psi3)

                List<Set<Pair<LTLFormula, Integer>>> mappingsForPsis = new ArrayList<>();
                for (LTLFormula psi : curlyG) {
                    Set<Pair<LTLFormula, Integer>> possibleRanks = new HashSet<>();
                    for (int rank = 0; rank < mojmirAutomatonFactoryFromLTL.createFrom(psi, alphabet).getMaxRank(); rank++) {
                        possibleRanks.add(new Pair<>(psi, rank));
                    }
                    mappingsForPsis.add(possibleRanks);
                }
                Set<List<Pair<LTLFormula, Integer>>> possibleMappings = Sets.cartesianProduct(mappingsForPsis);
                for (List<Pair<LTLFormula, Integer>> mapping : possibleMappings) {
                    // M(pi, G)
                    List<LTLFormula> cons = new ArrayList<>();
                    for (Pair<LTLFormula, Integer> p : mapping) {
                        cons.add(pi(p.getFirst(), p.getSecond()));
                    }
                    if (!(new LTLPropEquivalenceClass(new LTLAnd(prefix, new LTLAnd(cons)))).implies(tempState.getLabel())) {
                        notInF.add(tempState);
                    }

                    // Acc(pi, G, psi)
                    // TODO
                }

            }
            accForCurlyG.add(mPair);
            acc.add(accForCurlyG);
        }
        // TODO
        return null;
    }

    private LTLFormula pi(LTLFormula psi, int rank) {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTL.createFrom(psi, alphabet);
        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma, alphabet);
        List<LTLFormula> conjuncts = new ArrayList<>();
        ra.succeed(rank).forEach(t -> conjuncts.add(t.getFrom().getLabel().get(rank).getLabel().getRepresentative()));
        return new LTLAnd(conjuncts);
    }

//    private LTLPropEquivalenceClass eval(LTLPropEquivalenceClass formula, Set<LTLGOperator> curlyG) {

  //  }

    private RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> addOrGet(LTLPropEquivalenceClass label) {
        RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> result = existingStates.get(label);
        if (result == null) {
            RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = new RabinAutomaton.State<>(label);
            existingStates.put(label, newState);
            result = newState;
        }
        return result;
    }
}
