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

/**
 * A <code>GDRAFactory</code> can create a {@link GDRA} out of the {@link ltl2rabin.LTL.LTLFactory.Result} of an
 * {@link ltl2rabin.LTL.LTLFactory}. A GDRA represents a Generalized Deterministic Rabin Automaton. The construction
 * used by the {@link #createFrom(LTLFactory.Result)} method was introduced in the paper "From LTL to
 * Deterministic Automata -- A Safraless Compositional Approach" by Javier Esparza et al. This implementation of this
 * algorithm is explained in the Bachelor's Thesis "Deterministic &omega;-Automata for LTL: An efficient and scalable
 * implementation" by Hannes Bibel.
 *
 * <p>The creation of a GDRA can take minutes, hours, or even days, so be patient. If the computation takes long,
 * it might not be an error, but the result might simply be huge.
 *
 * <p>This class is a subclass of the {@link AutomatonFactory} class. It doesn't inherit any actual functionality
 * though.
 */
public class GDRAFactory extends AutomatonFactory<LTLFactory.Result, Pair<PropEquivalenceClass, List<SubAutomaton.State>>, Set<String>> {
    // This map stores the acceptance pairs Acc_r^G (psi) for every combination of r, curlyG and psi.
    private Map<Pair<Pair<Integer, Set<G>>, Formula>, Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyGPsis = new HashMap<>();

    /**
     * Creates a new <code>GDRAFactory</code> while ignoring the <code>alphabet</code> parameter.
     * The best way to create a <code>GDRAFactory</code> is with <code>new GDRAFactory(ImmutableSet.of())</code>. The
     * parameter only exists because this method is inherited by the abstract {@link AutomatonFactory} class.
     *
     * @param alphabet This parameter only exists for reasons of conformity with the base class
     *                 <code>AutomatonFactory</code>. It is ignored and never used.
     */
    public GDRAFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    /**
     * This method returns a {@link GDRA} (Generalized Deterministic Rabin Automaton). It computes the set of states,
     * the transitions and the acceptance sets.
     * @param parserResult The formula (phi), alphabet (sigma) and the set of G-subformulas that the method needs to
     *                     create a GDRA. An {@link ltl2rabin.LTL.LTLFactory.Result} is produced by any
     *                     {@link LTLFactory}, for example an {@link LTLFactoryFromString}.
     * @return A complete {@link GDRA}, generated from the {@link Formula} given in the input.
     */
    public GDRA createFrom(LTLFactory.Result parserResult) {
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(parserResult.getAlphabet());
        SubAutomatonFromFormulaFactory subAutomatonFactory = new SubAutomatonFromFormulaFactory(alphabet);

        ImmutableSet<G> gSet = (new ImmutableSet.Builder<G>())
                .addAll(parserResult.getgFormulas()).build();
        ImmutableSet<Set<G>> curlyGSets = (new ImmutableSet.Builder<Set<G>>())
                .addAll(Sets.powerSet(gSet)).build();
        Formula phi = parserResult.getLtlFormula();

        Set<GDRA.Transition> univ = new HashSet<>(); // UNIV = Universe, all transitions in the automaton.
        ImmutableMap.Builder<Pair<PropEquivalenceClass, List<SubAutomaton.State>>, GDRA.State> statesMapBuilder = new ImmutableMap.Builder<>();
        // This map stores an acceptance pair Acc_r^G (psi) for every combination of r, curlyG and psi.
        Map<Pair<Pair<Integer, Set<G>>, Formula>, Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyGPsis = new HashMap<>();
        // This map stores all the Acc_r^G pairs for combinations of curlyG and r.
        Map<Map<Formula, Integer>, Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> curlyGRankToAccMap = new HashMap<>();

        // All possible combinations of curlyG and their possible ranks:
        ImmutableList.Builder<Map<Formula, Integer>> curlyGRanksBuilder = new ImmutableList.Builder<>();
        for (Set<G> curlyG : curlyGSets) {
            // First create all possible pairs of the psis and their ranks ...
            ImmutableList.Builder<Set<Pair<Formula, Integer>>> psiAndRankPairs = new ImmutableList.Builder<>();
            curlyG.forEach(gPsi -> {
                Formula psi = gPsi.getOperand();
                ImmutableSet.Builder<Pair<Formula, Integer>> pairBuilder = new ImmutableSet.Builder<>();
                for (int i = 0; i <= subAutomatonFactory.createFrom(psi).getMaxRank(); i++) {
                    pairBuilder.add(new Pair<>(psi, i));
                }
                psiAndRankPairs.add(pairBuilder.build());
            });
            // ... and then form the cartesian product
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
        ImmutableList.Builder<SubAutomaton.State> initialLabelSubStatesBuilder = new ImmutableList.Builder<>();
        // for each subformula from gSet add the initial state of the corresponding SubAutomaton
        gSet.forEach(subFormula -> {
            initialLabelSubStatesBuilder.add(subAutomatonFactory.createFrom(subFormula.getOperand()).getInitialState());
        });
        Pair<PropEquivalenceClass, List<SubAutomaton.State>> initialLabel
                = new Pair<>(new PropEquivalenceClass(phi), initialLabelSubStatesBuilder.build());
        GDRA.State initialState = new GDRA.State(initialLabel);
        statesMapBuilder.put(initialLabel, initialState);

        // Generate all other states:
        Queue<GDRA.State> statesToBeExpanded = new ConcurrentLinkedQueue<>();
        statesToBeExpanded.add(initialState);
        while (!statesToBeExpanded.isEmpty()) {
            GDRA.State temp = statesToBeExpanded.poll();

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
                ImmutableList.Builder<SubAutomaton.State> newLabelSubStatesBuilder = new ImmutableList.Builder<>();
                temp.getLabel().getSecond().forEach(subState -> newLabelSubStatesBuilder.add(subState.readLetter(letter)));
                final Pair<PropEquivalenceClass, List<SubAutomaton.State>> newLabel = new Pair<>(newLabelLTL, newLabelSubStatesBuilder.build());
                // Get the state from the existing state map. If it does not exist yet, create it.
                GDRA.State newState = statesMapBuilder.build().get(newLabel);
                if (null == newState) {
                    newState = new GDRA.State(newLabel);
                    statesMapBuilder.put(newLabel, newState);
                    // if the states map did not contain the newState then enqueue it to visit and expand it later
                    statesToBeExpanded.offer(newState);
                }
                temp.setTransition(letter, newState);

                final GDRA.Transition tempTransition = new GDRA.Transition(temp, letter, newState);
                univ.add(tempTransition);

                // This loop checks whether the transition is in Acc_r^curlyG(psi) or not
                curlyGRanks.forEach(formulaToRankMap -> {
                    Set<G> curlyG = ImmutableSet.copyOf(formulaToRankMap.keySet().stream().map(G::new).collect(Collectors.toSet()));

                    formulaToRankMap.forEach((psi, rank) -> {
                        int rankForPsi = formulaToRankMap.get(psi);
                        SubAutomaton ra = subAutomatonFactory.createFrom(psi);
                        // The acceptance sets of the SubAutomaton:
                        Set<SubAutomaton.Transition> succeedAtRank = ra.succeed(rankForPsi, curlyG);
                        Set<SubAutomaton.Transition> failMergeAtRank = ra.failMerge(rankForPsi, curlyG);

                        Pair<Pair<Integer, Set<G>>, Formula> keyForAccRCurlyGPsis = new Pair<>(new Pair<>(rankForPsi, curlyG), psi);
                        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accRCurlyGPsi = accRCurlyGPsis.get(keyForAccRCurlyGPsis); // Acc_r^G (psi)
                        if (null == accRCurlyGPsi) {
                            accRCurlyGPsi = new Pair<>(new HashSet<>(), new HashSet<>());
                            accRCurlyGPsis.put(keyForAccRCurlyGPsis, accRCurlyGPsi);

                            // Add the newly created Acc_r^G (psi) to Acc_r^G
                            Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> accRCurlyG = curlyGRankToAccMap.get(formulaToRankMap);
                            accRCurlyG.add(accRCurlyGPsi);
                        }

                        for (SubAutomaton.Transition subAutomatonSuccTransition : succeedAtRank) {
                            if (subAutomatonSuccTransition.getLetter().equals(letter)
                                    && tempTransition.getFrom().getLabel().getSecond().contains(subAutomatonSuccTransition.getFrom())) {
                                accRCurlyGPsi.getSecond().add(tempTransition);
                            }
                        }
                        for (SubAutomaton.Transition subAutomatonAvoidTransition : failMergeAtRank) {
                            if (subAutomatonAvoidTransition.getLetter().equals(letter)
                                    && tempTransition.getFrom().getLabel().getSecond().contains(subAutomatonAvoidTransition.getFrom())) {
                                accRCurlyGPsi.getFirst().add(tempTransition);
                            }
                        }
                    });
                });

            }
        }
        ImmutableSet<GDRA.State> states = ImmutableSet.copyOf(statesMapBuilder.build().values());

        // Now that all states and transitions have been generated, we can construct M_r^curlyG:
        curlyGRanks.forEach(curlyGRanking -> {
            Set<G> curlyG = ImmutableSet.copyOf(curlyGRanking.keySet().stream().map(G::new).collect(Collectors.toSet()));
            Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> mRG = new Pair<>(new HashSet<>(), univ);


            states.forEach(state -> {
                List<Formula> conjunctList = new ArrayList<>();
                state.getLabel().getSecond().forEach(subState -> {
                    Formula psi = subState.getPsi();
                    if (curlyG.contains(new G(psi))) {
                        int rankForPsi = curlyGRanking.get(psi);
                        conjunctList.add(new G(psi));
                        conjunctList.addAll(subState.succeedingFormulas(rankForPsi));
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

        // Finally "convert" the map into an immutable set.
        ImmutableSet<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> acc = ImmutableSet.copyOf(curlyGRankToAccMap.values());
        return new GDRA(states, initialState, acc, alphabet);
    }
}
