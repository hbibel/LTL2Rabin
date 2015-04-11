package ltl2rabin;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked") // TODO: Might want to check assignments
public abstract class AutomatonMockFactory<T> {
    public abstract T mockMe(int numStates, Collection<StateTransition> transitions);

    public static class MAMockFactory extends AutomatonMockFactory<MojmirAutomaton> {

        public MojmirAutomaton mockMe(int numStates, Collection<StateTransition> transitions, Collection<Integer> sinks) {
            MojmirAutomaton<LTLPropEquivalenceClass, String> result = mock(MojmirAutomaton.class);
            ArrayList<MojmirAutomaton.State> states = new ArrayList<>();
            for (int i = 0; i < numStates; i++) {
                states.add(mock(MojmirAutomaton.State.class));
                when(states.get(i).getLabel()).thenReturn("mq" + i);
            }

            when(result.getInitialState()).thenReturn(states.get(0));

            HashSet<MojmirAutomaton<LTLPropEquivalenceClass, String>.State> sinkStates = new HashSet<>();
            sinks.forEach(i -> sinkStates.add(states.get(i)));
            when(result.getSinks()).thenReturn(sinkStates);

            for (StateTransition t : transitions) {
                when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
            }

            return result;
        }

        public MojmirAutomaton mockMe(int numStates, Collection<StateTransition> transitions) {
            Collection<Integer> sinks = Collections.EMPTY_LIST;
            return mockMe(numStates, transitions, sinks);
        }
    }

    public static class RAMockFactory extends AutomatonMockFactory<RabinAutomaton> {
        @Override
        public RabinAutomaton mockMe(int numStates, Collection<StateTransition> transitions) {
            RabinAutomaton<LTLPropEquivalenceClass, String> result = mock(RabinAutomaton.class);
            ArrayList<RabinAutomaton.State> states = new ArrayList<>();
            for (int i = 0; i < numStates; i++) {
                states.add(mock(RabinAutomaton.State.class));
            }

            for (StateTransition t : transitions) {
                when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
            }

            return result;
        }
    }

    public Set<String> generateAlphabet (int numLetters) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numLetters; i++) {
            result.add(Character.toString((char) ('a' + i)));
        }
        return result;
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
}
