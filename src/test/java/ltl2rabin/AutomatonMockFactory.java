package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ltl2rabin.LTL.LTLPropEquivalenceClass;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked") // TODO: Might want to check assignments
public abstract class AutomatonMockFactory<T> {
    // TODO: Remake entire class
    public abstract T mockMe(int numStates, Collection<StateTransition> transitions);

    public static class MAMockFactory extends AutomatonMockFactory<MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>> {

        public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMe(int numStates, Collection<StateTransition> transitions, Collection<Integer> sinks,
                                      Collection<Integer> acceptingStates, Set<Set<String>> alphabet) {
            MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> result = mock(MojmirAutomaton.class);
            ImmutableList.Builder<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> stateBuilder = new ImmutableList.Builder<>();
            for (int i = 0; i < numStates; i++) {
                stateBuilder.add(mock(MojmirAutomaton.State.class));
            }
            ImmutableList<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = stateBuilder.build();

            when(result.getInitialState()).thenReturn(states.get(0));

            sinks.forEach(i -> {
                when(states.get(i).isSink()).thenReturn(true);
            });
            //acceptingStates.forEach(i -> {
            //    when(states.get(i).isAccepting()).thenReturn(true);
            //});

            for (StateTransition t : transitions) {
                when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
            }

            when(result.getMaxRank()).thenReturn(states.size() - 1);

            ImmutableSet<Set<String>> immutableAlphabet = ImmutableSet.copyOf(alphabet);
            when(result.getAlphabet()).thenReturn(immutableAlphabet);

            return result;
        }

        public MojmirAutomaton mockMe(int numStates, Collection<StateTransition> transitions) {
            Collection<Integer> emptyList = Collections.EMPTY_LIST;
            return mockMe(numStates, transitions, emptyList, emptyList, Collections.EMPTY_SET);
        }
    }
/*
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
    }*/

    public static ImmutableSet<Set<String>> generateAlphabet (int numLetters) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numLetters; i++) {
            result.add(Character.toString((char) ('a' + i)));
        }
        return ImmutableSet.copyOf(Sets.powerSet(result));
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
