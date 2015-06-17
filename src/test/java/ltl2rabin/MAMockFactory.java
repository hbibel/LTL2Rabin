package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClass;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MAMockFactory {
    public MAMock createMAMock(ImmutableSet<Set<String>> alphabet, Formula initialLabel) {
        return new MAMock(alphabet, initialLabel);
    }

    // Working functions:
    // - getStates()
    // - getAlphabet()
    // (-) getMaxRank()
    // - getInitialState()
    // - isAcceptingState(new State(new PropEquivalenceClass(Formula blah)))
    public static class MAMock {
        private ImmutableSet<Set<String>> alphabet;
        private List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> states = new ArrayList<>();
        private List<PropEquivalenceClass> acceptingLabels = new ArrayList<>();
        private MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState;

        private MAMock(ImmutableSet<Set<String>> alphabet, Formula initialLabel) {
            this.alphabet = alphabet;
            initialState = (MojmirAutomaton.State<PropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.State.class);
            when(initialState.getLabel()).thenReturn(new PropEquivalenceClass(initialLabel));
            when(initialState.isSink()).thenReturn(false);
            states.add(initialState);
        }

        public MojmirAutomaton<PropEquivalenceClass, Set<String>> toMA() {
            MojmirAutomaton<PropEquivalenceClass, Set<String>> result = (MojmirAutomaton<PropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.class);
            when(result.getStates()).thenReturn(ImmutableSet.copyOf(states));
            when(result.getAlphabet()).thenReturn(alphabet);
            when(result.getInitialState()).thenReturn(initialState);
            when(result.isAcceptingState(anyObject())).thenAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] args = invocationOnMock.getArguments();
                    MojmirAutomaton.State<PropEquivalenceClass, Set<String>> arg = (MojmirAutomaton.State<PropEquivalenceClass, Set<String>>) args[0];
                    PropEquivalenceClass label = arg.getLabel();
                    return acceptingLabels.contains(label);
                }
            });
            return result;
        }

        public void addState(Formula label, boolean isSink) {
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> newState = (MojmirAutomaton.State<PropEquivalenceClass, Set<String>>) mock(MojmirAutomaton.State.class);
            when(newState.getLabel()).thenReturn(new PropEquivalenceClass(label));
            when(newState.isSink()).thenReturn(isSink);
            if (isSink) {
                alphabet.forEach(letter -> {
                    when(newState.readLetter(letter)).thenReturn(newState);
                });
            }
            states.add(newState);
        }

        public void whenReadingToken(Formula from, String token, Formula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenNotReadingToken(Formula from, String token, Formula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> !letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenReadingLetter(Formula from, Set<String> letter, Formula to) {
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClass, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClass(to))).findAny().get();
            when(fromState.readLetter(letter)).thenReturn(toState);
        }

        public void setStateAccepting(Formula label) {
            acceptingLabels.add(new PropEquivalenceClass(label));
        }
    }
}
