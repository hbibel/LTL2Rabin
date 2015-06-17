package ltl2rabin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlaveFromMojmirFactory extends RabinAutomatonFactory<MojmirAutomaton<PropEquivalenceClass, Set<String>>,
        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>,
        Set<String>> {
    public SlaveFromMojmirFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    public Slave createFrom(MojmirAutomaton<PropEquivalenceClass, Set<String>> from) {
        ListOrderedSet<Slave.State> states = new ListOrderedSet<>();
        Slave.State initialState;
        int maxRank = 0;

        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> initialMojmirStates = new ArrayList<>(Collections.singletonList(from.getInitialState()));
        initialState = new Slave.State(initialMojmirStates);
        states.add(initialState);

        ConcurrentLinkedQueue<Slave.State> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            Slave.State tempState = queue.poll();
            if (tempState.getTransitions().size() != getAlphabet().size()) {
                // tempState has transitions for any letter ==> tempState has been visited before
                List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> tempMojmirStates = tempState.getLabel();
                for (Set<String> letter : getAlphabet()) {
                    List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> newMojmirStateList = Stream.concat(tempMojmirStates.stream()
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

                    queue.add(newState);
                }
                maxRank = tempState.getLabel().size() - 1 > maxRank ? tempState.getLabel().size() - 1 : maxRank;
            }
        }
        ImmutableSet<Slave.State> immutableStates = ImmutableSet.copyOf(states);

        return new Slave(immutableStates, initialState, getAlphabet(), maxRank);
    }/* // TODO: Remove

    private boolean isInFail(Slave.Transition transition, MojmirAutomaton<PropEquivalenceClass, Set<String>> from) {
        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> tempMojmirStates = transition.getFrom().getLabel();
        Set<String> letter = transition.getLetter();

        return tempMojmirStates.stream().anyMatch(mState -> {
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> nextMState = mState.readLetter(letter);
            return nextMState.isSink() && !from.isAcceptingState(nextMState);
        });
    }

    private int mergeRank(Slave.Transition transition, MojmirAutomaton<PropEquivalenceClass, Set<String>> from) {
        for (int rank = 0; rank <= from.getMaxRank(); rank++) {
            if (transition.getFrom().getLabel().size() < rank + 1) {
                return -1;
            }
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mFromState = transition.getFrom().getLabel().get(rank); // q
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mToState = mFromState.readLetter(transition.getLetter()); // q'
            if (mToState.equals(from.getInitialState())) {
                return rank;
            }
            else {
                List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> mStatesAfterReadingLetter = transition.getFrom().getLabel().stream()
                        .filter(ms -> !ms.equals(mFromState))
                        .map(ms -> ms.readLetter(transition.getLetter()))
                        .filter(ms -> ms.equals(mToState))
                        .collect(Collectors.toList());
                if (mStatesAfterReadingLetter.size() > 0) {
                    return rank;
                }
            }
        }
        return -1;
    }

    private int succeedRank(Slave.Transition transition, MojmirAutomaton<PropEquivalenceClass, Set<String>> from) {
        for (int rank = 0; rank <= from.getMaxRank(); rank++) {
            if (transition.getFrom().getLabel().size() < rank + 1) {
                return -1;
            }
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mFromState = transition.getFrom().getLabel().get(rank); // q
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> mToState = mFromState.readLetter(transition.getLetter()); // q'
            if ((!from.isAcceptingState(mFromState) || mFromState.equals(from.getInitialState()))
                    && from.isAcceptingState(mToState)) {
                return rank;
            }
        }
        return -1;
    }*/
}
