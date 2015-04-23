package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked") // TODO: Might want to check assignments
public abstract class AutomatonMockFactory<T> {
    public abstract T mockMe(int numStates, Collection<StateTransition> transitions);

    public static class MAMockFactory extends AutomatonMockFactory<MojmirAutomaton> {

        public MojmirAutomaton mockMe(int numStates, Collection<StateTransition> transitions, Collection<Integer> sinks,
                                      Collection<Integer> acceptingStates) {
            MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> result = mock(MojmirAutomaton.class);
            ArrayList<MojmirAutomaton.State> states = new ArrayList<>();
            for (int i = 0; i < numStates; i++) {
                states.add(mock(MojmirAutomaton.State.class));
                when(states.get(i).getLabel()).thenReturn("mq" + i);
            }

            when(result.getInitialState()).thenReturn(states.get(0));

            HashSet<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>.State> sinkStates = new HashSet<>();
            sinks.forEach(i -> {
                sinkStates.add(states.get(i));
                when(states.get(i).isSink()).thenReturn(true);
            });
            acceptingStates.forEach(i -> {
                when(states.get(i).isAccepting()).thenReturn(true);
            });

            when(result.getSinks()).thenReturn(sinkStates);

            for (StateTransition t : transitions) {
                when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
            }

            when(result.getMaxRank()).thenReturn(states.size());

            return result;
        }

        public MojmirAutomaton mockMe(int numStates, Collection<StateTransition> transitions) {
            Collection<Integer> emptyList = Collections.EMPTY_LIST;
            return mockMe(numStates, transitions, emptyList, emptyList);
        }
    }

    public static class RAMockFactory extends AutomatonMockFactory<RabinAutomaton> {
        @Override
        public RabinAutomaton mockMe(int numStates, Collection<StateTransition> transitions) {
            RabinAutomaton<LTLPropEquivalenceClass, Set<String>> result = mock(RabinAutomaton.class);
            ArrayList<RabinAutomaton<LTLPropEquivalenceClass, Set<String>>.State> states = new ArrayList<>();
            for (int i = 0; i < numStates; i++) {
                states.add(mock(RabinAutomaton.State.class));
            }

            for (StateTransition t : transitions) {
                when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
            }

            ListOrderedSet<RabinAutomaton<LTLPropEquivalenceClass, Set<String>>.State> stateListOrderedSet = ListOrderedSet.listOrderedSet(states);
            when(result.getStates()).thenReturn(stateListOrderedSet);
            when(result.getInitialState()).thenReturn(states.get(0));

            return result;
        }
    }

    public static Set<Set<String>> generateAlphabet (int numLetters) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numLetters; i++) {
            result.add(Character.toString((char) ('a' + i)));
        }
        return Sets.powerSet(result);
    }

    public static class StateTransition {
        int from;
        Set<String> letter;
        int to;

        StateTransition(int from, Set<String> letter, int to) {
            this.from = from;
            this.letter = letter;
            this.to = to;
        }
    }

    public static List<Set<String>> createWord(String... letters) {
        List<Set<String>> result = new ArrayList<>();
        for (String l : letters) {
            char[] parts = l.toCharArray();
            List<String> partsAsStrings = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                partsAsStrings.add("" + parts[i]);
            }
            result.add(new HashSet<>(partsAsStrings));
        }
        return result;
    }
}
