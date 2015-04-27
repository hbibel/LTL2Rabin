package ltl2rabin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabinAutomatonFactory extends AutomatonFactory {
    @Override
    public Automaton createFrom(Object from) {
        return null;
    }

    public RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> createFrom(MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> from) {
        ListOrderedSet<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> states = new ListOrderedSet<>();
        RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> initialState;
        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> transitionBuilder = new ImmutableSet.Builder<>();
        Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> fail = new ListOrderedSet<>();
        List<Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> buy = new ArrayList<>();
        List<Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> succeed = new ArrayList<>();
        ImmutableSet<Set<String>> alphabet = from.getAlphabet();

        List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> initialMojmirStates = new ArrayList<>(Collections.singletonList(from.getInitialState()));
        initialState = new RabinAutomaton.State<>(initialMojmirStates);
        states.add(initialState);

        ConcurrentLinkedQueue<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> tempState = queue.poll();
            if (tempState.getTransitions().size() != alphabet.size()) {
                // tempState has transitions for any letter ==> tempState has been visited before
                List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> tempMojmirStates = tempState.getLabel();
                for (Set<String> letter : alphabet) {
                    List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> newMojmirStateList = Stream.concat(tempMojmirStates.stream()
                            .map(e -> e.readLetter(letter)), Stream.of(initialMojmirStates.get(0)))
                            // According to the javadocs: For ordered streams, the selection of distinct elements is
                            // stable (for duplicated elements, the element appearing first in the encounter order is
                            // preserved.)
                            .distinct()
                            .filter(e -> !e.isSink())
                            .collect(Collectors.toList());
                    RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> newState = new RabinAutomaton.State<>(newMojmirStateList);
                    int indexOfExistingState = states.indexOf(newState);
                    if (indexOfExistingState != -1) {
                        newState = states.get(indexOfExistingState);
                    }
                    else {
                        states.add(newState);
                    }
                    tempState.setTransition(letter, newState);
                    transitionBuilder.add(new Automaton.Transition<>(tempState, letter, newState));
                    queue.add(newState);
                }
            }
        }

        ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> immutableTransitions = transitionBuilder.build();
        ImmutableSet<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> immutableStates = ImmutableSet.copyOf(states);

        // a token moves from mojmir state q into non-acc sink q' ==> fail
        immutableStates.forEach(rState -> {
            rState.getLabel().forEach(mState -> {
                alphabet.forEach(letter -> {
                    MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> nextMState = mState.readLetter(letter);
                    if (nextMState.isSink() && !nextMState.isAccepting()) {
                        fail.add(new Automaton.Transition<>(rState, letter, rState.readLetter(letter)));
                    }
                });
            });
        });

        // one token with rank = i buys another or gets bought
        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> buyI = new ListOrderedSet<>();
            final int finalRank = rank;
            immutableTransitions.forEach(transition -> {
                if (buysOrGetBought(transition, finalRank, from.getInitialState())) {
                    buyI.add(transition);
                }
            });
            buy.add(buyI);
        }

        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> succeedI = new ListOrderedSet<>();
            final int finalRank = rank;
            for (Set<String> letter : alphabet) {
                succeedI.addAll(immutableTransitions.stream()
                        .filter(transition -> transition.getFrom().getLabel().size() > finalRank)
                        .filter(transition -> !transition.getFrom().getLabel().get(finalRank).isAccepting()
                                || transition.getFrom().getLabel().get(finalRank).equals(from.getInitialState()))
                        .filter(transition -> transition.getFrom().getLabel().get(finalRank).readLetter(letter).isAccepting())
                        .filter(transition -> transition.getLetter().equals(letter))
                        .collect(Collectors.toList()));
            }
            succeed.add(succeedI);
        }

        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> avoidBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> reachBuilder = new ImmutableSet.Builder<>();
        // TODO: Merge unnecessary fail buy and succeed loops into this one
        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            buy.get(rank).addAll(fail);
            avoidBuilder.addAll(buy.get(rank));
            reachBuilder.addAll(succeed.get(rank));
        }
        Pair<ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>, ImmutableSet<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> rabinPair
                = new Pair<>(avoidBuilder.build(), reachBuilder.build());

        return new RabinAutomaton<>(immutableStates, initialState, rabinPair, alphabet);
    }

    public RabinAutomaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(LTLFormula ltlFormula) {
        // TODO
        return null;
    }

    private RabinAutomaton<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> createFrom(Iterable<RabinAutomaton<?, Set<String>>> rabinAutomata,
                                                                                     ImmutableSet<Set<String>> alphabet) {
        // create the product automaton of a collection of rabin automata
        List<RabinAutomaton.State<?, Set<String>>> initialRaStates = new ArrayList<>();
        rabinAutomata.iterator().forEachRemaining(rabinAutomaton -> initialRaStates.add(rabinAutomaton.getInitialState()));
        RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> initialState = new RabinAutomaton.State<>(initialRaStates);
        List<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> resultStates = new ArrayList<>();

        Queue<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> stateQueue = new ConcurrentLinkedQueue<>();
        stateQueue.add(initialState);
        while (!stateQueue.isEmpty()) {
            RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> tempState = stateQueue.poll();
            for (Set<String> letter : alphabet) {
                ImmutableList.Builder<RabinAutomaton.State<?, Set<String>>> tempRaStates = new ImmutableList.Builder<>();
                tempState.getLabel().forEach(rs -> tempRaStates.add(rs.readLetter(letter)));
                RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>> newState = new RabinAutomaton.State<>(tempRaStates.build());
                int index = resultStates.indexOf(newState);
                if (index >= 0) {
                    newState = resultStates.get(index);
                }
                else {
                    resultStates.add(newState);
                }
                tempState.setTransition(letter, newState);
                if (newState.transitionCount() < alphabet.size()) {
                    stateQueue.add(newState);
                }
            }
        }

        ImmutableCollection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> states = ImmutableSet.copyOf(resultStates);

        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>> avoidBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>> reachBuilder = new ImmutableSet.Builder<>();
        for (RabinAutomaton<?, Set<String>> ra : rabinAutomata) {
            for (Automaton.Transition<RabinAutomaton.State, Set<String>> avoidTransition : ra.getRabinPair().getFirst()) {
                Collection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> avoidFrom = states.stream().filter(s -> s.getLabel().contains(avoidTransition.getFrom())).collect(Collectors.toList());
                avoidFrom.forEach(s -> {
                    avoidBuilder.add(new Automaton.Transition<>(s, avoidTransition.getLetter(), s.readLetter(avoidTransition.getLetter())));
                });
            }
            for (Automaton.Transition<RabinAutomaton.State, Set<String>> reachTransition : ra.getRabinPair().getSecond()) {
                Collection<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>> reachFrom = states.stream().filter(s -> s.getLabel().contains(reachTransition.getFrom())).collect(Collectors.toList());
                reachFrom.forEach(s -> {
                    reachBuilder.add(new Automaton.Transition<>(s, reachTransition.getLetter(), s.readLetter(reachTransition.getLetter())));
                });
            }
        }
        Pair<ImmutableSet<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>>,
                ImmutableSet<Automaton.Transition<RabinAutomaton.State<Collection<RabinAutomaton.State<?, Set<String>>>, Set<String>>, Set<String>>>> rabinPair
                = new Pair<>(avoidBuilder.build(), reachBuilder.build());

        return new RabinAutomaton<>(states, initialState, rabinPair, alphabet);
    }

    private boolean buysOrGetBought(Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>> transition,
                                    int rank, MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> mInitialState) {
        if (rank >= transition.getFrom().getLabel().size()) {
            return false;
        }
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> mFromState = transition.getFrom().getLabel().get(rank); // q
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> mToState = mFromState.readLetter(transition.getLetter()); // q'
        if (mToState.equals(mInitialState)) {
            return true;
        }
        else {
            List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> mStatesAfterReadingLetter = transition.getFrom().getLabel().stream()
                    .filter(ms -> !ms.equals(mFromState))
                    .map(ms -> ms.readLetter(transition.getLetter()))
                    .filter(ms -> ms.equals(mToState))
                    .collect(Collectors.toList());
            if (mStatesAfterReadingLetter.size() > 0) {
                return true;
            }
        }
        return false;
    }
}
