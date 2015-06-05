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
    private Map<Pair<LTLPropEquivalenceClass, List<Slave.State>>,
                RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>>> existingStates = new HashMap<>();
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL;
    private MojmirAutomatonFactoryFromLTLAndSet mojmirAutomatonFactoryFromLTLAndSet;
    private SlaveFromMojmirFactory slaveFromMojmirFactory;
    private LTLFactoryFromString ltlFactory = new LTLFactoryFromString();

    public RabinAutomaton<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> createFrom(String from) {
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL(alphabet);
        mojmirAutomatonFactoryFromLTLAndSet = new MojmirAutomatonFactoryFromLTLAndSet(alphabet);
        slaveFromMojmirFactory = new SlaveFromMojmirFactory(alphabet);


        ImmutableSet<Formula> gSet = (new ImmutableSet.Builder<Formula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<Formula>> curlyGSets = (new ImmutableSet.Builder<Set<Formula>>())
                .addAll(Sets.powerSet(gSet)).build();
        Formula phi = parserResult.getLtlFormula();

        Set<RabinAutomaton.Transition> univ = new HashSet<>(); // TODO: UNIV = all transitions?
        ImmutableSet.Builder<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>>> statesBuilder = new ImmutableSet.Builder<>();

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
                curlyGPiBuilder.add(new Pair<>(ImmutableSet.copyOf(curlyG), mapBuilder.build()));
            });
        }
        ImmutableList<Pair<ImmutableSet<Formula>, Map<Formula, Integer>>> curlyGPis = curlyGPiBuilder.build();
        Map<Pair<Set<Formula>, Map<Formula, Integer>>, Set> piCurlyGToAccMap = new HashMap<>();
        curlyGPis.forEach(curlyGPi -> { // TODO: Integrate this loop into some other
            Set<Formula> curlyG = curlyGPi.getFirst();
            Map<Formula, Integer> pi = curlyGPi.getSecond();
            piCurlyGToAccMap.put(new Pair<>(curlyG, pi), new HashSet<>());
        });

        // Initial state:
        ImmutableList.Builder<Slave.State> initialLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
        gSet.forEach(subFormula -> {
            initialLabelSlaveStatesBuilder.add((Slave.State) slaveFromMojmirFactory.createFrom(mojmirAutomatonFactoryFromLTL.createFrom(subFormula)).getInitialState());
        });
        Pair<LTLPropEquivalenceClass, List<Slave.State>> initialLabel
                = new Pair<>(new LTLPropEquivalenceClass(phi), initialLabelSlaveStatesBuilder.build());
        RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> initialState
                = new RabinAutomaton.State<>(initialLabel);
        statesBuilder.add(initialState);

        // Generate all other states:
        Queue<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>>> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);
        while (!statesToBeAdded.isEmpty()) {
            RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> temp = statesToBeAdded.poll();

            for (Set<String> letter : alphabet) {
                LTLAfGVisitor afVisitor = new LTLAfGVisitor(letter) {
                    // We want to use af here, not afG
                    @Override
                    public Formula visit(G formula) {
                        Formula afOperand = afG(formula.getOperand());
                        return new And(formula, afOperand);
                    }
                };
                LTLPropEquivalenceClass newLabelLTL = new LTLPropEquivalenceClass(afVisitor.afG(temp.getLabel().getFirst().getRepresentative()));
                ImmutableList.Builder<Slave.State> newLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
                // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
                temp.getLabel().getSecond().forEach(slaveState -> newLabelSlaveStatesBuilder.add((Slave.State) slaveState.readLetter(letter)));
                RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> newState =
                        addOrGet(new Pair<>(newLabelLTL, newLabelSlaveStatesBuilder.build()));

                if (!statesBuilder.build().contains(newState)) {
                    statesToBeAdded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                // this loop checks wether the transition is accepting or not
                curlyGPis.forEach(curlyGPi -> {
                    ImmutableSet<Formula> curlyG = curlyGPi.getFirst();
                    Map<Formula, Integer> pi = curlyGPi.getSecond();

                    // M_pi^curlyG
                    List<Formula> conjuncts = new ArrayList<>();
                    curlyG.forEach(psi -> {
                        conjuncts.add(new G(psi));
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
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
                    if (!new LTLPropEquivalenceClass(conjuncts.isEmpty() ? new Boolean(true) : new And(conjuncts)).implies(newLabelLTL)) {
                        // the state "temp" is not in F and thus its outgoing transition is in M(pi, curlyG)
                        Pair<Set<Formula>, Map<Formula, Integer>> key = new Pair<>(curlyG, pi);
                        Set accPiCurlyG = piCurlyGToAccMap.get(key);
                        accPiCurlyG.add(new Pair<>(ImmutableSet.of(new Automaton.Transition<>(temp, letter, newState)), univ));
                    }

                    // Acc_pi(psi)
                    curlyG.forEach(psi -> {
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
                        Slave ra = slaveFromMojmirFactory.createFrom(ma);
                        int piForPsi = pi.get(psi);
                        Set<Slave.Transition> succeedPi = ra.succeed(piForPsi);
                        Set<Slave.Transition> avoidPi = ra.failBuy(piForPsi);
                        Pair<Set, Set> accPiPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                        for(Slave.Transition transition : succeedPi) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiPsi.getFirst().add(new Automaton.Transition<>(temp, letter, newState));
                                break;
                            }
                        }
                        for(Slave.Transition transition : avoidPi) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiPsi.getSecond().add(new Automaton.Transition<>(temp, letter, newState));
                                break;
                            }
                        }
                        if ((!accPiPsi.getFirst().isEmpty()) && (!accPiPsi.getSecond().isEmpty())) {
                            Pair<Set<Formula>, Map<Formula, Integer>> key = new Pair<>(curlyG, pi);
                            Set accPiCurlyG = piCurlyGToAccMap.get(key);
                            accPiCurlyG.add(accPiPsi);
                        }
                    });
                });
            }
        }

        ImmutableSet<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>>> states = statesBuilder.build();
        // Set<Set<Pair<Set, Set>>> acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
        Set acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
        return new RabinAutomaton<>(states, initialState, acc, alphabet);
    }

    private RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> addOrGet(Pair<LTLPropEquivalenceClass, List<Slave.State>> label) {
        RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<Slave.State>>, Set<String>> result = existingStates.get(label);
        if (result == null) {
            result = new RabinAutomaton.State<>(label);
            existingStates.put(label, result);
        }
        return result;
    }
}
