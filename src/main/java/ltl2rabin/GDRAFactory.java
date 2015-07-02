package ltl2rabin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class GDRAFactory extends AutomatonFactory<LTLFactory.Result, Pair<PropEquivalenceClass, List<Slave.State>>, Set<String>> {
    private Map<Pair<PropEquivalenceClass, List<Slave.State>>,
                GDRA.State> existingStates = new HashMap<>();
    private Map<Pair<Pair<Integer, Set<G>>, Formula>, Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyGPsis = new HashMap<>();

    public GDRAFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    public GDRA createFrom(LTLFactory.Result parserResult) {
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        SlaveFromFormulaFactory slaveFactory = new SlaveFromFormulaFactory(alphabet);

        ImmutableSet<G> gSet = (new ImmutableSet.Builder<G>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<G>> curlyGSets = (new ImmutableSet.Builder<Set<G>>())
                .addAll(Sets.powerSet(gSet)).build();
        Formula phi = parserResult.getLtlFormula();

        Set<GDRA.Transition> univ = new HashSet<>(); // UNIV = Universe, all transitions in the automaton.
        ImmutableSet.Builder<GDRA.State> statesBuilder = new ImmutableSet.Builder<>();
        Map<Map<Formula, Integer>, Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> curlyGRankToAccMap = new HashMap<>();

        // All possible combinations of curlyG and their possible ranks:
        ImmutableList.Builder<Map<Formula, Integer>> curlyGRanksBuilder = new ImmutableList.Builder<>();
        for (Set<G> curlyG : curlyGSets) {
            ImmutableList.Builder<Set<Pair<Formula, Integer>>> psiAndRankPairs = new ImmutableList.Builder<>();
            curlyG.forEach(gPsi -> {
                Formula psi = gPsi.getOperand();
                ImmutableSet.Builder<Pair<Formula, Integer>> pairBuilder = new ImmutableSet.Builder<>();
                for (int i = 0; i <= slaveFactory.createFrom(psi).getMaxRank(); i++) {
                    pairBuilder.add(new Pair<>(psi, i));
                }
                psiAndRankPairs.add(pairBuilder.build());
            });
            Set<List<Pair<Formula, Integer>>> possiblePairsForCurlyG = Sets.cartesianProduct(psiAndRankPairs.build());
            possiblePairsForCurlyG.forEach(listOfPairs -> {
                ImmutableMap.Builder<Formula, Integer> mapBuilder = new ImmutableMap.Builder<>();
                listOfPairs.forEach(pair -> {
                    mapBuilder.put(pair.getFirst(), pair.getSecond());
                });
                Map<Formula, Integer> curlyGRanks = mapBuilder.build();
                curlyGRankToAccMap.put(curlyGRanks, new HashSet<>());
                curlyGRanksBuilder.add(curlyGRanks);
            });
        }
        ImmutableList<Map<Formula, Integer>> curlyGRanks = curlyGRanksBuilder.build();

        // Create the initial state:
        ImmutableList.Builder<Slave.State> initialLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding Slave
        gSet.forEach(subFormula -> {
            initialLabelSlaveStatesBuilder.add(slaveFactory.createFrom(subFormula.getOperand()).getInitialState());
        });
        Pair<PropEquivalenceClass, List<Slave.State>> initialLabel
                = new Pair<>(new PropEquivalenceClass(phi), initialLabelSlaveStatesBuilder.build());
        GDRA.State initialState = new GDRA.State(initialLabel);
        statesBuilder.add(initialState);

        // Generate all other states:
        Queue<GDRA.State> statesToBeExpanded = new ConcurrentLinkedQueue<>();
        statesToBeExpanded.add(initialState);
        while (!statesToBeExpanded.isEmpty()) {
            GDRA.State temp = statesToBeExpanded.poll();

            for (Set<String> letter : alphabet) { // TODO: This might be possible to be turned into a parallel forEach loop
                LTLAfGVisitor afVisitor = new LTLAfGVisitor(letter) {
                    // We want to use af here, not afG
                    @Override
                    public Formula visit(G formula) {
                        Formula afOperand = afG(formula.getOperand());
                        return new And(formula, afOperand);
                    }
                };
                PropEquivalenceClass newLabelLTL = new PropEquivalenceClass(afVisitor.afG(temp.getLabel().getFirst().getRepresentative()));
                ImmutableList.Builder<Slave.State> newLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
                temp.getLabel().getSecond().forEach(slaveState -> newLabelSlaveStatesBuilder.add(slaveState.readLetter(letter)));
                GDRA.State newState = addOrGet(new Pair<>(newLabelLTL, newLabelSlaveStatesBuilder.build()));

                // if the states set already contains the newState then it already has been visited and expanded
                if (!statesBuilder.build().contains(newState)) {
                    statesToBeExpanded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                final GDRA.Transition tempTransition = new GDRA.Transition(temp, letter, newState);
                univ.add(tempTransition);

                // This loop checks whether the transition is in Acc_r^curlyG(psi) or not
                curlyGRanks.forEach(formulaToRankMap -> {
                    Set<G> curlyG = ImmutableSet.copyOf(formulaToRankMap.keySet().stream().map(G::new).collect(Collectors.toSet()));

                    formulaToRankMap.forEach((psi, rank) -> {
                        int rankForPsi = formulaToRankMap.get(psi);
                        Slave ra = slaveFactory.createFrom(psi);
                        // The acceptance sets of the Slave:
                        Set<Slave.Transition> succeedAtRank = ra.succeed(rankForPsi, curlyG);
                        Set<Slave.Transition> failMergeAtRank = ra.failMerge(rankForPsi, curlyG);

                        Pair<Pair<Integer, Set<G>>, Formula> keyForAccRCurlyGPsis = new Pair<>(new Pair<>(rankForPsi, curlyG), psi);
                        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accRCurlyGPsi = accRCurlyGPsis.get(keyForAccRCurlyGPsis); // Acc_r^G (psi)
                        if (null == accRCurlyGPsi) {
                            accRCurlyGPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                            accRCurlyGPsis.put(keyForAccRCurlyGPsis, accRCurlyGPsi);

                            // Add the newly created Acc_r^G (psi) to Acc_r^G
                            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyG = curlyGRankToAccMap.get(formulaToRankMap);
                            accRCurlyG.add(accRCurlyGPsi);
                        }

                        for (Slave.Transition slaveReachTransition : succeedAtRank) {
                            if (slaveReachTransition.getLetter().equals(letter)
                                    && tempTransition.getFrom().getLabel().getSecond().contains(slaveReachTransition.getFrom())) {
                                accRCurlyGPsi.getSecond().add(tempTransition);
                            }
                        }
                        for (Slave.Transition slaveAvoidTransition : failMergeAtRank) {
                            if (slaveAvoidTransition.getLetter().equals(letter)
                                    && tempTransition.getFrom().getLabel().getSecond().contains(slaveAvoidTransition.getFrom())) {
                                accRCurlyGPsi.getFirst().add(tempTransition);
                            }
                        }
                    });
                });

            }
        }
        ImmutableSet<GDRA.State> states = statesBuilder.build();

        // Now that all states and transitions have been generated, we can construct M_r^curlyG:
        curlyGRanks.forEach(curlyGRanking -> {
            Set<G> curlyG = ImmutableSet.copyOf(curlyGRanking.keySet().stream().map(G::new).collect(Collectors.toSet()));
            Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> mRG = new Pair<>(new HashSet<>(), univ);


            states.forEach(state -> {
                List<Formula> conjunctList = new ArrayList<>();
                state.getLabel().getSecond().forEach(slaveState -> {
                    Formula psi = slaveState.getPsi();
                    if (curlyG.contains(new G(psi))) {
                        int rankForPsi = curlyGRanking.get(psi);
                        conjunctList.add(new G(psi));
                        conjunctList.addAll(slaveState.succeedingFormulas(rankForPsi));
                    }
                });

                if (!new PropEquivalenceClass(conjunctList.isEmpty() ? new Boolean(true) : new And(conjunctList)).implies(state.getLabel().getFirst())) {
                    // the state "state" is not in F and thus its outgoing transitions are in M_r^curlyG
                    alphabet.forEach(letter -> mRG.getFirst().add(new GDRA.Transition(state, letter, state.readLetter(letter))));
                }
            });
            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyG = curlyGRankToAccMap.get(curlyGRanking);
            accRCurlyG.add(mRG);
        });

        ImmutableSet.Builder<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> acc = new ImmutableSet.Builder<>();
        curlyGRanks.forEach(curlyGRanking -> {
            acc.add(curlyGRankToAccMap.get(curlyGRanking));
        });
        return new GDRA(states, initialState, acc.build(), alphabet);
    }

    private GDRA.State addOrGet(Pair<PropEquivalenceClass, List<Slave.State>> label) {
        GDRA.State result = existingStates.get(label);
        if (result == null) {
            result = new GDRA.State(label);
            existingStates.put(label, result);
        }
        return result;
    }
}
