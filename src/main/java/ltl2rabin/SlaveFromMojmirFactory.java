package ltl2rabin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlaveFromMojmirFactory extends RabinAutomatonFactory<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>,
        List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>,
        Set<String>> {
    public SlaveFromMojmirFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    public Slave createFrom(MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> from) {
        ListOrderedSet<Slave.State> states = new ListOrderedSet<>();
        Slave.State initialState;
        ImmutableSet.Builder<Slave.Transition> transitionBuilder = new ImmutableSet.Builder<>();
        Set<Slave.Transition> fail = new ListOrderedSet<>();
        List<Set<Slave.Transition>> buy = new ArrayList<>();
        List<ImmutableSet<Slave.Transition>> succeed = new ArrayList<>();

        List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> initialMojmirStates = new ArrayList<>(Collections.singletonList(from.getInitialState()));
        initialState = new Slave.State(initialMojmirStates);
        states.add(initialState);

        ConcurrentLinkedQueue<Slave.State> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            Slave.State tempState = queue.poll();
            if (tempState.getTransitions().size() != getAlphabet().size()) {
                // tempState has transitions for any letter ==> tempState has been visited before
                List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> tempMojmirStates = tempState.getLabel();
                for (Set<String> letter : getAlphabet()) {
                    List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> newMojmirStateList = Stream.concat(tempMojmirStates.stream()
                            .map(e -> e.readLetter(letter)), Stream.of(initialMojmirStates.get(0)))
                            // According to the javadocs: For ordered streams, the selection of distinct elements is
                            // stable (for duplicated elements, the element appearing first in the encounter order is
                            // preserved.)
                            .distinct()
                            .filter(e -> !e.isSink())
                            .collect(Collectors.toList());
                    Slave.State newState = new Slave.State(newMojmirStateList);
                    int indexOfExistingState = states.indexOf(newState);
                    if (indexOfExistingState != -1) {
                        newState = states.get(indexOfExistingState);
                    }
                    else {
                        states.add(newState);
                    }
                    tempState.setTransition(letter, newState);
                    transitionBuilder.add(new Slave.Transition(tempState, letter, newState));
                    queue.add(newState);
                }
            }
        }

        ImmutableSet<Slave.Transition> immutableTransitions = transitionBuilder.build();
        ImmutableSet<Slave.State> immutableStates = ImmutableSet.copyOf(states);

        // a token moves from mojmir state q into non-acc sink q' ==> fail
        immutableStates.forEach(rState -> {
            rState.getLabel().forEach(mState -> {
                getAlphabet().forEach(letter -> {
                    MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> nextMState = mState.readLetter(letter);
                    if (nextMState.isSink() && !from.isAcceptingState(nextMState)) {
                        fail.add(new Slave.Transition(rState, letter, rState.readLetter(letter)));
                    }
                });
            });
        });

        // one token with rank = i buys another or gets bought
        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            Set<Slave.Transition> buyI = new ListOrderedSet<>();
            final int finalRank = rank;
            immutableTransitions.forEach(transition -> {
                if (buysOrGetBought(transition, finalRank, from.getInitialState())) {
                    buyI.add(transition);
                }
            });
            buy.add(buyI);
        }

        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            ImmutableSet.Builder<Slave.Transition> succeedI = new ImmutableSet.Builder<>();
            final int finalRank = rank;
            for (Set<String> letter : getAlphabet()) {
                succeedI.addAll(immutableTransitions.stream()
                        .filter(transition -> transition.getFrom().getLabel().size() > finalRank)
                        .filter(transition -> !from.isAcceptingState(transition.getFrom().getLabel().get(finalRank))
                                || transition.getFrom().getLabel().get(finalRank).equals(from.getInitialState()))
                        .filter(transition -> from.isAcceptingState(transition.getFrom().getLabel().get(finalRank).readLetter(letter)))
                        .filter(transition -> transition.getLetter().equals(letter))
                                .collect(Collectors.toList()));
            }
            succeed.add(succeedI.build());
        }

        ImmutableMap.Builder<Integer, ImmutableSet<Slave.Transition>> failBuyIBuilder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<Integer, ImmutableSet<Slave.Transition>> succeedIBuilder = new ImmutableMap.Builder<>();


        for (int rank = 0; rank < from.getMaxRank(); rank++) {
            failBuyIBuilder.put(rank, new ImmutableSet.Builder<Slave.Transition>().addAll(fail).addAll(buy.get(rank)).build());
            succeedIBuilder.put(rank, succeed.get(rank));
        }
        Pair<ImmutableMap<Integer, ImmutableSet<Slave.Transition>>, ImmutableMap<Integer, ImmutableSet<Slave.Transition>>> failBuySucceed
                = new Pair<>(failBuyIBuilder.build(), succeedIBuilder.build());

        return new Slave(immutableStates, initialState, failBuySucceed, getAlphabet());
    }

    private boolean buysOrGetBought(Slave.Transition transition,
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
