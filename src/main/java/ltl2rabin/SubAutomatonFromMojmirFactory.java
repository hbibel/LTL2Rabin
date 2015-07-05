package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.PropEquivalenceClass;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A factory of this type will create SubAutomata (which are state rankings for Mojmir automatons) out of a given
 * <code>MojmirAutomaton</code>.
 */
public class SubAutomatonFromMojmirFactory extends RabinAutomatonFactory<MojmirAutomaton<PropEquivalenceClass, Set<String>>,
        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>,
        Set<String>> {
    public SubAutomatonFromMojmirFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    /**
     *
     * @param from    The Mojmir automaton whose states are to be ranked
     * @return        The SubAutomaton, by definition 4.19 in the paper "From LTL to Deterministic Automata."
     */
    public SubAutomaton createFrom(MojmirAutomaton<PropEquivalenceClass, Set<String>> from) {
        ListOrderedSet<SubAutomaton.State> states = new ListOrderedSet<>();
        SubAutomaton.State initialState;
        int maxRank = 0;

        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> initialStateRanking = new ArrayList<>(Collections.singletonList(from.getInitialState()));
        initialState = new SubAutomaton.State(initialStateRanking, from.getInitialState().getLabel().getRepresentative());
        states.add(initialState);

        ConcurrentLinkedQueue<SubAutomaton.State> queue = new ConcurrentLinkedQueue<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            SubAutomaton.State tempState = queue.poll();
            if (tempState.getTransitions().size() != getAlphabet().size()) {
                // tempState does not have transitions for any letter ==> tempState has not been visited before
                List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> tempStateRanking = tempState.getLabel();
                for (Set<String> letter : getAlphabet()) {
                    List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> newStateRanking = Stream.concat(tempStateRanking.stream()
                            .map(e -> e.readLetter(letter)), Stream.of(initialStateRanking.get(0)))
                            // According to the javadocs: For ordered streams, the selection of distinct elements is
                            // stable (for duplicated elements, the element appearing first in the encounter order is
                            // preserved.)
                            .distinct()
                            .filter(e -> !e.isSink())
                            .collect(Collectors.toList());
                    SubAutomaton.State newState = new SubAutomaton.State(newStateRanking, from.getInitialState().getLabel().getRepresentative());
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
        ImmutableSet<SubAutomaton.State> immutableStates = ImmutableSet.copyOf(states);

        return new SubAutomaton(immutableStates, initialState, getAlphabet(), maxRank);
    }
}
