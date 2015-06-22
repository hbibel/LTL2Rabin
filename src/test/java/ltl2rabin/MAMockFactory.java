package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClassWithBeeDeeDee;
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
    // - isAcceptingState(new State(new PropEquivalenceClassWithBeeDeeDee(Formula blah)))
    public static class MAMock {
        private ImmutableSet<Set<String>> alphabet;
        private List<MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> states = new ArrayList<>();
        private MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> initialState;

        private MAMock(ImmutableSet<Set<String>> alphabet, Formula initialLabel) {
            this.alphabet = alphabet;
            initialState = (MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>) mock(MojmirAutomaton.State.class);
            when(initialState.getLabel()).thenReturn(new PropEquivalenceClassWithBeeDeeDee(initialLabel));
            when(initialState.isSink()).thenReturn(false);
            states.add(initialState);
        }

        public MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> toMA() {
            MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> result = (MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>>) mock(MojmirAutomaton.class);
            when(result.getStates()).thenReturn(ImmutableSet.copyOf(states));
            when(result.getAlphabet()).thenReturn(alphabet);
            when(result.getInitialState()).thenReturn(initialState);
            /* when(result.isAcceptingState(anyObject())).thenAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] args = invocationOnMock.getArguments();
                    MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> arg = (MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>) args[0];
                    PropEquivalenceClassWithBeeDeeDee label = arg.getLabel();
                    return acceptingLabels.contains(label);
                }
            });*/
            return result;
        }

        public void addState(Formula label, boolean isSink, Set<Set<Formula>> acceptingCurlyGs) {
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> newState = (MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>) mock(MojmirAutomaton.State.class);
            when(newState.getLabel()).thenReturn(new PropEquivalenceClassWithBeeDeeDee(label));
            when(newState.isSink()).thenReturn(isSink);
            when(newState.isAcceptingState(anyObject())).thenAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] args = invocationOnMock.getArguments();
                    Set<Formula> arg = (Set<Formula>) args[0];
                    return acceptingCurlyGs.contains(arg);
                }
            });
            if (isSink) {
                alphabet.forEach(letter -> {
                    when(newState.readLetter(letter)).thenReturn(newState);
                });
            }
            states.add(newState);
        }

        public void addState(Formula label, boolean isSink) {
            addState(label, isSink, Collections.emptySet());
        }

        public void whenReadingToken(Formula from, String token, Formula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenNotReadingToken(Formula from, String token, Formula to) {
            Set<Set<String>> letters = alphabet.stream().filter(letter -> !letter.contains(token)).collect(Collectors.toSet());
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(to))).findAny().get();
            letters.forEach(letter -> {
                when(fromState.readLetter(letter)).thenReturn(toState);
            });
        }

        public void whenReadingLetter(Formula from, Set<String> letter, Formula to) {
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> fromState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(from))).findAny().get();
            MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> toState = states.stream().filter(state -> state.getLabel().equals(new PropEquivalenceClassWithBeeDeeDee(to))).findAny().get();
            when(fromState.readLetter(letter)).thenReturn(toState);
        }
    }
}
