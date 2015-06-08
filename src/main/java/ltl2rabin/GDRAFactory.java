package ltl2rabin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRAFactory {
    private Map<Pair<PropEquivalenceClass, List<Slave.State>>,
                GDRA.State> existingStates = new HashMap<>();
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL;
    private MojmirAutomatonFactoryFromLTLSetRanking mojmirAutomatonFactoryFromLTLAndSet;
    private SlaveFromMojmirFactory slaveFromMojmirFactory;
    private LTLFactoryFromString ltlFactory = new LTLFactoryFromString();

    public GDRA createFrom(String from) {
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL(alphabet);
        mojmirAutomatonFactoryFromLTLAndSet = new MojmirAutomatonFactoryFromLTLSetRanking(alphabet);
        slaveFromMojmirFactory = new SlaveFromMojmirFactory(alphabet);

        ImmutableSet<Formula> gSet = (new ImmutableSet.Builder<Formula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<Formula>> curlyGSets = (new ImmutableSet.Builder<Set<Formula>>())
                .addAll(Sets.powerSet(gSet)).build();
        Formula phi = parserResult.getLtlFormula();

        Set<GDRA.Transition> univ = new HashSet<>(); // TODO: UNIV = all transitions?
        ImmutableSet.Builder<GDRA.State> statesBuilder = new ImmutableSet.Builder<>();
        Map<Pair<Set<Formula>, Map<Formula, Integer>>, Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> piCurlyGToAccMap = new HashMap<>();

        // All possible combinations of curlyG and pi:
        ImmutableList.Builder<Pair<ImmutableSet<Formula>, Map<Formula, Integer>>> curlyGPiBuilder = new ImmutableList.Builder<>();
        for (Set<Formula> curlyG : curlyGSets) {
            ImmutableList.Builder<Set<Pair<Formula, Integer>>> psiAndPiPairs = new ImmutableList.Builder<>();
            curlyG.forEach(psi -> {
                ImmutableSet.Builder<Pair<Formula, Integer>> pairBuilder = new ImmutableSet.Builder<>();
                for (int i = 0; i < mojmirAutomatonFactoryFromLTL.createFrom(psi).getMaxRank(); i++) {
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
                Pair<ImmutableSet<Formula>, Map<Formula, Integer>> curlyGPi = new Pair<>(ImmutableSet.copyOf(curlyG), mapBuilder.build());
                Map<Formula, Integer> pi = curlyGPi.getSecond();
                piCurlyGToAccMap.put(new Pair<>(curlyG, pi), new HashSet<>());
                curlyGPiBuilder.add(curlyGPi);
            });
        }
        ImmutableList<Pair<ImmutableSet<Formula>, Map<Formula, Integer>>> curlyGPis = curlyGPiBuilder.build();

        // Initial state:
        ImmutableList.Builder<Slave.State> initialLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
        gSet.forEach(subFormula -> {
            initialLabelSlaveStatesBuilder.add(slaveFromMojmirFactory.createFrom(mojmirAutomatonFactoryFromLTL.createFrom(subFormula)).getInitialState());
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

            for (Set<String> letter : alphabet) {
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
                // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
                temp.getLabel().getSecond().forEach(slaveState -> newLabelSlaveStatesBuilder.add((Slave.State) slaveState.readLetter(letter)));
                GDRA.State newState = addOrGet(new Pair<>(newLabelLTL, newLabelSlaveStatesBuilder.build()));

                if (!statesBuilder.build().contains(newState)) {
                    statesToBeAdded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                final GDRA.Transition tempTransition = new GDRA.Transition(temp, letter, newState);
                // this loop checks wether the transition is accepting or not
                curlyGPis.forEach(curlyGPi -> {
                    ImmutableSet<Formula> curlyG = curlyGPi.getFirst();
                    Map<Formula, Integer> pi = curlyGPi.getSecond();

                    // Acc_pi(psi)
                    curlyG.forEach(psi -> {
                        MojmirAutomaton<PropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
                        Slave ra = slaveFromMojmirFactory.createFrom(ma);
                        int piForPsi = pi.get(psi);
                        Set<Slave.Transition> succeedPi = ra.succeed(piForPsi);
                        Set<Slave.Transition> avoidPi = ra.failMerge(piForPsi);
                        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accPiCurlyGPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                        for(Slave.Transition transition : succeedPi) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiCurlyGPsi.getFirst().add(tempTransition);
                                break;
                            }
                        }
                        for(Slave.Transition transition : avoidPi) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiCurlyGPsi.getSecond().add(tempTransition);
                                break;
                            }
                        }
                        if ((!accPiCurlyGPsi.getFirst().isEmpty()) && (!accPiCurlyGPsi.getSecond().isEmpty())) {
                            Pair<Set<Formula>, Map<Formula, Integer>> key = new Pair<>(curlyG, pi);
                            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accPiCurlyG = piCurlyGToAccMap.get(key);
                            accPiCurlyG.add(accPiCurlyGPsi);
                        }
                    });
                });
            }

            // Now that temp has all transitions, we can check whether they all are in M_pi^curlyG or not:
            curlyGPis.forEach(curlyGPi -> {
                ImmutableSet<Formula> curlyG = curlyGPi.getFirst();
                Map<Formula, Integer> pi = curlyGPi.getSecond();

                List<Formula> conjuncts = new ArrayList<>();
                curlyG.forEach(psi -> {
                    conjuncts.add(new G(psi));
                    MojmirAutomaton<PropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
                    Slave ra = slaveFromMojmirFactory.createFrom(ma);
                    ra.getStates().forEach(raState -> {
                        // F(r_psi): For each raState, get the corresponding mojmir state with rank pi(psi) or higher
                        int piForPsi = pi.get(psi);
                        if (raState.getLabel().size() - 1 >= piForPsi) {
                            // raState has LTL formulae with rank pi(psi) or higher.
                            for (int i = piForPsi; i < raState.getLabel().size() - 1; i++) {
                                conjuncts.add(raState.getLabel().get(i).getLabel().getRepresentative());
                            }
                        }
                    });
                });
                if (!new PropEquivalenceClass(conjuncts.isEmpty() ? new Boolean(true) : new And(conjuncts)).implies(temp.getLabel().getFirst())) {
                    // the state "temp" is not in F and thus its outgoing transitions are in M(pi, curlyG)
                    Pair<Set<Formula>, Map<Formula, Integer>> key = new Pair<>(curlyG, pi);
                    Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accPiCurlyG = piCurlyGToAccMap.get(key);
                    alphabet.forEach(letter -> accPiCurlyG.add(new Pair<>(ImmutableSet.of(new GDRA.Transition(temp, letter, temp.readLetter(letter))), univ)));
                }
                });
        }

        ImmutableSet<GDRA.State> states = statesBuilder.build();
        Set<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
        return new GDRA(states, initialState, acc, alphabet);
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
