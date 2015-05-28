package ltl2rabin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GDRAFactory {
    private Map<LTLPropEquivalenceClass, RabinAutomaton.State<LTLPropEquivalenceClass, Set<String>>> existingStates = new HashMap<>();
    private MojmirAutomatonFactoryFromLTL mojmirAutomatonFactoryFromLTL = new MojmirAutomatonFactoryFromLTL();
    private MojmirAutomatonFactoryFromLTLAndSet mojmirAutomatonFactoryFromLTLAndSet = new MojmirAutomatonFactoryFromLTLAndSet();
    private RabinAutomatonFromMojmirFactory rabinAutomatonFromMojmirFactory = new RabinAutomatonFromMojmirFactory();
    private LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
    private ImmutableSet<Set<String>> alphabet;

    // the dontCare parameter is irrelevant
    public RabinAutomaton<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>> createFrom(String from, ImmutableSet<Set<String>> dontCare) {
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        ImmutableSet<LTLFormula> gSet = (new ImmutableSet.Builder<LTLFormula>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<LTLFormula>> curlyGSets = (new ImmutableSet.Builder<Set<LTLFormula>>())
                .addAll(Sets.powerSet(gSet)).build();
        this.alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
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
        gSet.forEach(subFormula -> initialLabelSlaveStatesBuilder.add(rabinAutomatonFromMojmirFactory.createFrom(mojmirAutomatonFactoryFromLTL.createFrom(subFormula, alphabet), alphabet).getInitialState()));
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
                        new RabinAutomaton.State<>(new Pair<>(newLabelLTL, newLabelSlaveStatesBuilder.build()));

                if (!statesBuilder.build().contains(newState)) {
                    statesToBeAdded.offer(newState);
                    statesBuilder.add(newState);
                }
                temp.setTransition(letter, newState);

                // this loop checks wether the transition is accepting or not
                curlyGPis.forEach(curlyGPi -> {
                    ImmutableSet<LTLFormula> curlyG = curlyGPi.getFirst();
                    Map<LTLFormula, Integer> pi = curlyGPi.getSecond();

                    // M(pi, curlyG)
                    List<LTLFormula> conjuncts = new ArrayList<>();
                    curlyG.forEach(psi -> {
                        conjuncts.add(new LTLGOperator(psi));
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG), alphabet);
                        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma, alphabet);
                        conjuncts.add() // TODO: F(r_psi)
                    });

                    curlyG.forEach(psi -> {
                        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactoryFromLTLAndSet.createFrom(new Pair<>(psi, curlyG), alphabet);
                        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFromMojmirFactory.createFrom(ma, alphabet);
                        pi.forEach((ltlFormula, integer) -> {
                            Pair<Set<LTLFormula>, Map<LTLFormula, Integer>> key = new Pair<>(curlyG, pi);
                            Set accPiCurlyG = piCurlyGToAccMap.get(key);
                        });
                    });
                });
            }
        }

        ImmutableSet<RabinAutomaton.State<Pair<LTLPropEquivalenceClass, List<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>>>, Set<String>>> states = statesBuilder.build();
        Set<Set<Pair<Set, Set>>> acc = ImmutableSet.copyOf(piCurlyGToAccMap.values());
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
