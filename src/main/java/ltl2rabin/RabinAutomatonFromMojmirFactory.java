package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabinAutomatonFromMojmirFactory extends RabinAutomatonFactory<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>,
        List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>,
        Set<String>> {
    public RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> createFrom(MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> from, ImmutableSet<Set<String>> alphabet) {
        ListOrderedSet<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>> states = new ListOrderedSet<>();
        RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> initialState;
        ImmutableSet.Builder<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> transitionBuilder = new ImmutableSet.Builder<>();
        Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>> fail = new ListOrderedSet<>();
        List<Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> buy = new ArrayList<>();
        List<Set<Automaton.Transition<RabinAutomaton.State<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>>, Set<String>>>> succeed = new ArrayList<>();

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
                    if (nextMState.isSink() && !from.isAcceptingState(nextMState)) {
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
                        .filter(transition -> !from.isAcceptingState(transition.getFrom().getLabel().get(finalRank))
                                || transition.getFrom().getLabel().get(finalRank).equals(from.getInitialState()))
                        .filter(transition -> from.isAcceptingState(transition.getFrom().getLabel().get(finalRank).readLetter(letter)))
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
