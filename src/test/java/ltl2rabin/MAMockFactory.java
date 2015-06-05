package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MAMockFactory {
    public MAMock createMAMock(ImmutableSet<Set<String>> alphabet, LTLFormula initialLabel) {
        return new MAMock(alphabet, initialLabel);
    }

    // Working functions:
    // - getStates()
    // - getAlphabet()
    // - getMaxRank()
    // - getInitialState()
    // - isAcceptingState(new State(new LTLPropEquivalenceClass(LTLFormula blah)))
    public static class MAMock {
        private ImmutableSet<Set<String>> alphabet;
        private List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = new ArrayList<>();
        private List<LTLPropEquivalenceClass> acceptingLabels = new ArrayList<>();
        private MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState;

        private MAMock(ImmutableSet<Set<String>> alphabet, LTLFormula initialLabel) {
            this.alphabet = alphabet;
            initialState = (MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.State.class);
            when(initialState.getLabel()).thenReturn(new LTLPropEquivalenceClass(initialLabel));
            when(initialState.isSink()).thenReturn(false);
            states.add(initialState);
        }

        public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> toMA() {
            MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> result = (MojmirAutomaton<LTLPropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.class);
            when(result.getStates()).thenReturn(ImmutableSet.copyOf(states));
            when(result.getAlphabet()).thenReturn(alphabet);
            when(result.getMaxRank()).thenReturn(states.size() - 1); // TODO: - #(sinks)
            when(result.getInitialState()).thenReturn(initialState);
            when(result.isAcceptingState(anyObject())).thenAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] args = invocationOnMock.getArguments();
                    MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> arg = (MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>) args[0];
                    LTLPropEquivalenceClass label = arg.getLabel();
                    return acceptingLabels.contains(label);
                }
            });
            return result;
        }

        public void addState(LTLFormula label, boolean isSink) {
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> newState = (MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.State.class);
            when(newState.getLabel()).thenReturn(new LTLPropEquivalenceClass(label));
            when(newState.isSink()).thenReturn(isSink);
            if (isSink) {
                alphabet.forEach(letter -> {
                    when(newState.readLetter(letter)).thenReturn(newState);
                });
            }
            states.add(newState);
        }

        public void whenReadingToken(LTLFormula from, String token, LTLFormula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenNotReadingToken(LTLFormula from, String token, LTLFormula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> !letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenReadingLetter(LTLFormula from, Set<String> letter, LTLFormula to) {
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new LTLPropEquivalenceClass(to))).findAny().get();
            when(fromState.readLetter(letter)).thenReturn(toState);
        }

        public void setStateAccepting(LTLFormula label) {
            acceptingLabels.add(new LTLPropEquivalenceClass(label));
        }
    }
}
