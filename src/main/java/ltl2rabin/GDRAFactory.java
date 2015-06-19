package ltl2rabin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
// TODO: Rename Pi into acceptingRanks or sth like that
public class GDRAFactory {
    private Map<Pair<PropEquivalenceClass, List<Slave.State>>,
                GDRA.State> existingStates = new HashMap<>();
    private LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
    private Map<Pair<Pair<Integer, Set<Formula>>, Formula>, Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accPiCurlyGPsis = new HashMap<>();

    public GDRA createFrom(String from) {
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        SlaveFactory slaveFactory = new SlaveFactory(alphabet);

        ImmutableSet<Formula> gSet = (new ImmutableSet.Builder<Formula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<Formula>> curlyGSets = (new ImmutableSet.Builder<Set<Formula>>())
                .addAll(Sets.powerSet(gSet)).build();
        Formula phi = parserResult.getLtlFormula();

        Set<GDRA.Transition> univ = new HashSet<>(); // UNIV = Universe, all transitions in the automaton.
        ImmutableSet.Builder<GDRA.State> statesBuilder = new ImmutableSet.Builder<>();
        Map<Pair<Set<Formula>, Map<Formula, Integer>>, Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> piCurlyGToAccMap = new HashMap<>();

        // All possible combinations of curlyG and pi:
        ImmutableList.Builder<Pair<Set<Formula>, Map<Formula, Integer>>> curlyGPiBuilder = new ImmutableList.Builder<>();
        for (Set<Formula> curlyG : curlyGSets) {
            ImmutableList.Builder<Set<Pair<Formula, Integer>>> psiAndPiPairs = new ImmutableList.Builder<>();
            curlyG.forEach(psi -> {
                ImmutableSet.Builder<Pair<Formula, Integer>> pairBuilder = new ImmutableSet.Builder<>();
                for (int i = 0; i <= slaveFactory.createFrom(psi).getMaxRank(); i++) {
                    pairBuilder.add(new Pair<>(psi, i));
                }
                psiAndPiPairs.add(pairBuilder.build());
            });
            Set<List<Pair<Formula, Integer>>> possiblePairsForCurlyG = Sets.cartesianProduct(psiAndPiPairs.build());
            possiblePairsForCurlyG.forEach(listOfPairs -> {
                ImmutableMap.Builder<Formula, Integer> mapBuilder = new ImmutableMap.Builder<>();
                listOfPairs.forEach(pair -> {
                    mapBuilder.put(pair.getFirst(), pair.getSecond());
                });
                Pair<Set<Formula>, Map<Formula, Integer>> curlyGPi = new Pair<>(ImmutableSet.copyOf(curlyG), mapBuilder.build());
                Map<Formula, Integer> pi = curlyGPi.getSecond();
                piCurlyGToAccMap.put(new Pair<>(curlyG, pi), new HashSet<>());
                curlyGPiBuilder.add(curlyGPi);
            });
        }
        ImmutableList<Pair<Set<Formula>, Map<Formula, Integer>>> curlyGPis = curlyGPiBuilder.build();

        // Create the initial state:
        ImmutableList.Builder<Slave.State> initialLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding Slave
        gSet.forEach(subFormula -> {
            initialLabelSlaveStatesBuilder.add(slaveFactory.createFrom(subFormula).getInitialState());
        });
        Pair<PropEquivalenceClass, List<Slave.State>> initialLabel
                = new Pair<>(new PropEquivalenceClass(phi), initialLabelSlaveStatesBuilder.build());
        GDRA.State initialState = new GDRA.State(initialLabel);
        statesBuilder.add(initialState);

        // Generate all other states:
        Queue<GDRA.State> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);
        while (!statesToBeAdded.isEmpty()) {
            GDRA.State temp = statesToBeAdded.poll();

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
                    statesToBeAdded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                final GDRA.Transition tempTransition = new GDRA.Transition(temp, letter, newState);
                univ.add(tempTransition);

                // This loop checks whether the transition is in Acc_pi^curlyG(psi) or not
                curlyGPis.forEach(curlyGPi -> { // TODO: Might want to make this parallel
                    Set<Formula> curlyG = curlyGPi.getFirst();
                    Map<Formula, Integer> rankMap = curlyGPi.getSecond();

                    curlyG.forEach(psi -> {
                        int rankForPsi = rankMap.get(psi);
                        Slave ra = slaveFactory.createFrom(psi);
                        // The acceptance sets of the Slave:
                        Set<Slave.Transition> succeedAtRank = ra.succeed(rankForPsi, curlyG);
                        Set<Slave.Transition> failMergeAtRank = ra.failMerge(rankForPsi, curlyG);

                        /*** TODO: Remove after debugging *
                         System.out.println("psi has " + failMergeAtRank.size() + " transitions failing at rank " + rankForPsi + ":");
                         failMergeAtRank.forEach(t -> {
                         System.out.println("(" + (t.getFrom().getLabel().size() == 1 ? "sr0, " : "sr1, ") + t.getLetter() + ", " + (t.getTo().getLabel().size() == 1 ? "sr0)" : "sr1)"));
                         });
                         System.out.println("\n\n");/**/

                        Pair<Pair<Integer, Set<Formula>>, Formula> keyForAccRCurlyGPsis = new Pair<>(new Pair<>(rankForPsi, curlyG), psi);
                        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accRCurlyGPsi = accPiCurlyGPsis.get(keyForAccRCurlyGPsis); // Acc_r^G (psi)
                        if (null == accRCurlyGPsi) {
                            accRCurlyGPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                            accPiCurlyGPsis.put(keyForAccRCurlyGPsis, accRCurlyGPsi);

                            // Add the newly created Acc_r^G (psi) to Acc_r^G
                            Pair<Set<Formula>, Map<Formula, Integer>> keyForAccRCurlyG = new Pair<>(curlyG, rankMap);
                            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyG = piCurlyGToAccMap.get(keyForAccRCurlyG);
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

        // Now that all states and transitions have been generated, we can construct M_pi^curlyG:
        curlyGPis.forEach(curlyGPi -> {
            Set<Formula> curlyG = curlyGPi.getFirst();
            Map<Formula, Integer> pi = curlyGPi.getSecond();
            Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> mPiG = new Pair<>(new HashSet<>(), univ);


            states.forEach(state -> {
                List<Formula> conjuncts = new ArrayList<>();
                state.getLabel().getSecond().forEach(slaveState -> {
                    Formula psi = slaveState.getPsi();
                    if (curlyG.contains(psi)) {
                        int piForPsi = pi.get(psi);
                        conjuncts.add(new G(psi));
                        conjuncts.addAll(slaveState.succeedingFormulas(piForPsi));
                    }
                });
                /*** TODO: Remove after debugging*
                 System.out.println("M^{" + curlyG.toString() + "} conjunction = " + conjuncts);
                /***                              ***/

                if (!new PropEquivalenceClass(conjuncts.isEmpty() ? new Boolean(true) : new And(conjuncts)).implies(state.getLabel().getFirst())) {
                    // the state "state" is not in F and thus its outgoing transitions are in M(pi, curlyG)
                    alphabet.forEach(letter -> mPiG.getFirst().add(new GDRA.Transition(state, letter, state.readLetter(letter))));
                }
            });
            Pair<Set<Formula>, Map<Formula, Integer>> key = new Pair<>(curlyG, pi);
            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accPiCurlyG = piCurlyGToAccMap.get(key);
            accPiCurlyG.add(mPiG);
        });

        ImmutableSet.Builder<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> acc = new ImmutableSet.Builder<>();
        curlyGPis.forEach(key -> {
            acc.add(piCurlyGToAccMap.get(key));
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
