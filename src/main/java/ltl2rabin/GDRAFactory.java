package ltl2rabin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRAFactory {
    private Map<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>,
                RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>>> existingStates = new HashMap<>();
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL;
    private MojmirAutomatonFactoryFromLTLAndSet mojmirAutomatonFactoryFromLTLAndSet;
    private RabinAutomatonFromMojmirFactory rabinAutomatonFromMojmirFactory;
    private LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
    private ImmutableSet<Set<String>> alphabet;

    public RabinAutomaton<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> createFrom(String from) {
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        this.alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL(alphabet);
        mojmirAutomatonFactoryFromLTLAndSet = new MojmirAutomatonFactoryFromLTLAndSet(alphabet);
        rabinAutomatonFromMojmirFactory = new RabinAutomatonFromMojmirFactory(alphabet);


        ImmutableSet<LTLFormula> gSet = (new ImmutableSet.Builder<LTLFormula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<LTLFormula>> curlyGSets = (new ImmutableSet.Builder<Set<LTLFormula>>())
                .addAll(Sets.powerSet(gSet)).build();
        LTLFormula phi = parserResult.getLtlFormula();

        Set<RabinAutomaton.Transition> univ = new HashSet<>(); // TODO: UNIV = all transitions?
        ImmutableSet.Builder<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>>> statesBuilder = new ImmutableSet.Builder<>();

        // All possible combinations of curlyG and pi:
        ImmutableList.Builder<Pair<ImmutableSet<LTLFormula>, Map<LTLFormula, Integer>>> curlyGPiBuilder = new ImmutableList.Builder<>();
        for (Set<LTLFormula> curlyG : curlyGSets) {
            // TODO
        }
        ImmutableList<Pair<ImmutableSet<LTLFormula>, Map<LTLFormula, Integer>>> curlyGPis = curlyGPiBuilder.build();
        Map<Pair<Set<LTLFormula>, Map<LTLFormula, Integer>>, Set> piCurlyGToAccMap = new HashMap<>();
        curlyGPis.forEach(curlyGPi -> { // TODO: Integrate this loop into some other
            Set<LTLFormula> curlyG = curlyGPi.getFirst();
            Map<LTLFormula, Integer> pi = curlyGPi.getSecond();
            piCurlyGToAccMap.put(new Pair<>(curlyG, pi), new HashSet<>());
        });

        // Initial state:
        ImmutableList.Builder<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> initialLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
        gSet.forEach(subFormula -> initialLabelSlaveStatesBuilder.add(rabinAutomatonFromMojmirFactory.createFrom(mojmirAutomatonFactoryFromLTL.createFrom(subFormula)).getInitialState()));
        Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>> initialLabel
                = new Pair<>(new LTLPropEquivalenceClass(phi), initialLabelSlaveStatesBuilder.build());
        RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> initialState
                = new RabinAutomaton.State<>(initialLabel);
        statesBuilder.add(initialState);

        // Generate all other states:
        Queue<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>>> statesToBeAdded = new ConcurrentLinkedQueue<>();
        statesToBeAdded.add(initialState);
        while (!statesToBeAdded.isEmpty()) {
            RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> temp = statesToBeAdded.poll();

            for (Set<String> letter : alphabet) {
                LTLAfGVisitor afVisitor = new LTLAfGVisitor(letter) {
                    // We want to use af here, not afG
                    @Override
                    public LTLFormula visit(LTLGOperator formula) {
                        LTLFormula afOperand = afG(formula.getOperand());
                        return new LTLAnd(formula, afOperand);
                    }
                };
                LTLPropEquivalenceClass newLabelLTL = new LTLPropEquivalenceClass(afVisitor.afG(temp.getLabel().getFirst().getRepresentative()));
                ImmutableList.Builder<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> newLabelSlaveStatesBuilder = new ImmutableList.Builder<>();
                // for each subformula from gSet add the initial state of the corresponding RabinAutomaton
                temp.getLabel().getSecond().forEach(slaveState -> newLabelSlaveStatesBuilder.add(slaveState.readLetter(letter)));
                RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> newState =
                        addOrGet(new Pair<>(newLabelLTL, newLabelSlaveStatesBuilder.build()));

                if (!statesBuilder.build().contains(newState)) {
                    statesToBeAdded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                // this loop checks wether the transition is accepting or not
                curlyGPis.forEach(curlyGPi -> {
                    ImmutableSet<LTLFormula> curlyG = curlyGPi.getFirst();
                    Map<LTLFormula, Integer> pi = curlyGPi.getSecond();

                    // M_pi^curlyG
                    List<LTLFormula> conjuncts = new ArrayList<>();
                    curlyG.forEach(psi -> {
                        conjuncts.add(new LTLGOperator(psi));
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
                        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma);
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
                    if (!new LTLPropEquivalenceClass(new LTLAnd(conjuncts)).implies(newLabelLTL)) {
                        // the state "temp" is not in F and thus its outgoing transition is in M(pi, curlyG)
                        Pair<Set<LTLFormula>, Map<LTLFormula, Integer>> key = new Pair<>(curlyG, pi);
                        Set accPiCurlyG = piCurlyGToAccMap.get(key);
                        accPiCurlyG.add(new Pair<>(ImmutableSet.of(new Automaton.Transition<>(temp, letter, newState)), univ));
                    }

                    // Acc_pi(psi)
                    curlyG.forEach(psi -> {
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG));
                        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma);
                        int piForPsi = pi.get(psi);
                        Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> succeedPi = null; // TODO: ra.succeed(piForPsi)
                        Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> fail = null; // TODO: ra.fail()
                        Pair<Set, Set> accPiPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                        for(Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>> transition : succeedPi) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiPsi.getFirst().add(new Automaton.Transition<>(temp, letter, newState));
                                break;
                            }
                        }
                        for(Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>> transition : fail) {
                            if (transition.getLetter().equals(letter)
                                    && temp.getLabel().getSecond().contains(transition.getFrom())) {
                                accPiPsi.getSecond().add(new Automaton.Transition<>(temp, letter, newState));
                                break;
                            }
                        }
                        if ((!accPiPsi.getFirst().isEmpty()) && (!accPiPsi.getSecond().isEmpty())) {
                            Pair<Set<LTLFormula>, Map<LTLFormula, Integer>> key = new Pair<>(curlyG, pi);
                            Set accPiCurlyG = piCurlyGToAccMap.get(key);
                            accPiCurlyG.add(accPiPsi);
                        }
                    });
                });
            }
        }

        ImmutableSet<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>>> states = statesBuilder.build();
        // Set<Set<Pair<Set, Set>>> acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
        Set acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
        return new RabinAutomaton<>(states, initialState, acc, alphabet);
    }

    private RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> addOrGet(Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>> label) {
        RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> result = existingStates.get(label);
        if (result == null) {
            result = new RabinAutomaton.State<>(label);
            existingStates.put(label, result);
        }
        return result;
    }
}
