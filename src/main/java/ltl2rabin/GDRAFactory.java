package ltl2rabin;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import javafx.scene.control.RadioMenuItem;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRAFactory extends RabinAutomatonFactory<String,
        LTLPropEquivalenceClass,
        Set<String>> {
    private HashMap<LTLPropEquivalenceClass, RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> existingStates;
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL();
    private RabinAutomatonFromMojmirFactory rabinAutomatonFromMojmirFactory = new RabinAutomatonFromMojmirFactory();
    private Set<Set<String>> alphabet;

    public GDRAFactory(Set<Set<String>> alphabet) {
        this.alphabet = alphabet;
    }

    public RabinAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        ImmutableSet<LTLFormula> gSet = (new ImmutableSet.Builder<LTLFormula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<LTLFormula>> curlyGSets = (new ImmutableSet.Builder<Set<LTLFormula>>())
                .addAll(Sets.powerSet(gSet)).build();
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
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
        MojmirAutomatonListFactory mojmirAutomatonListFactory = new MojmirAutomatonListFactory(curlyGSets, alphabet); // TODO: Remove this

        for (LTLFormula psi : gSet) {
            List<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>> mojmirAutomatonList = mojmirAutomatonListFactory.createFrom(psi);
            for (MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma : mojmirAutomatonList) {
                slavesBuilder.add(rabinAutomatonFromMojmirFactory.createFrom(ma));
                maxRank = ma.getMaxRank() > maxRank ? ma.getMaxRank() : maxRank;
            }
        }
        ImmutableSet<RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> slaves
                = slavesBuilder.build();

        Set<RabinAutomaton.Transition> univ = new HashSet<>(); // TODO: UNIV = all transitions?
        Set<Set<Pair<Set, Set>>> acc = new HashSet<>();
        for (Set<LTLFormula> curlyG : curlyGSets) {
            Set<Pair<Set, Set>> accForCurlyG = new HashSet<>();
            Set<RabinAutomaton.State> notInF = new HashSet<>();
            Pair<Set, Set> theMPart = new Pair<>(notInF, univ);
            for (RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>> tempState : states) {
                List<LTLFormula> conjuncts = new ArrayList<>();
                curlyG.forEach(conjuncts::add);
                LTLAnd prefix = new LTLAnd(conjuncts);

                List<Set<Pair<LTLFormula, Integer>>> mappingsForPsis = new ArrayList<>();
                for (LTLFormula psi : curlyG) {
                    Set<Pair<LTLFormula, Integer>> possibleRanks = new HashSet<>();
                    for (int rank = 0; rank < mojmirAutomatonFactoryFromLTL.createFrom(new Pair<>(psi, alphabet)).getMaxRank(); rank++) {
                        possibleRanks.add(new Pair<>(psi, rank));
                    }
                    mappingsForPsis.add(possibleRanks);
                }
                Set<List<Pair<LTLFormula, Integer>>> possibleMappings = Sets.cartesianProduct(mappingsForPsis);
                for (List<Pair<LTLFormula, Integer>> mapping : possibleMappings) {
                    List<LTLFormula> cons = new ArrayList<>();
                    for (Pair<LTLFormula, Integer> p : mapping) {
                        cons.add(pi(p.getFirst(), p.getSecond()));
                    }
                    if (!(new LTLPropEquivalenceClass(new LTLAnd(prefix, new LTLAnd(cons)))).implies(tempState.getLabel())) {
                        notInF.add(tempState);
                    }
                }

            }
            acc.add(accForCurlyG);
        }
        // TODO
        return null;
    }

    private LTLFormula pi(LTLFormula psi, int rank) {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTL.createFrom(new Pair<>(psi, alphabet));
        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma);
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
