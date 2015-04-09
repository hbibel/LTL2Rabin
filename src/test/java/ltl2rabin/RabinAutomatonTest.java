package ltl2rabin;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RabinAutomatonTest {
    LTLVariable variable_a;
    LTLVariable variable_b;
    LTLVariable variable_c;
    LTLFactoryFromString factory;

    private String mojmirStateStringFromLTL(LTLFormula f) {
        return "state(" + f.toString() + ")";
    }
    private String rabinStateStringFromList(List<LTLFormula> l) {
        String result = "State{mojmirStates=[";
        Iterator<LTLFormula> it = l.iterator();
        while (it.hasNext()) {
            LTLFormula next = it.next();
            result += mojmirStateStringFromLTL(next);
            if (it.hasNext()) result += ", ";
        }
        result += "]}";
        return result;
    }

    @Before
    public void setUp() throws Exception {
        variable_a = new LTLVariable("a");
        variable_b = new LTLVariable("b");
        variable_c = new LTLVariable("c");

        factory = new LTLFactoryFromString();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test1() {
        Set<String> alphabet = generateAlphabet(1);
        Set<StateTransition> transitions = new HashSet<>();
        HashSet<String> letter = new HashSet<>();
        letter.add("a");
        transitions.add(new StateTransition(0, letter, 1));
        letter.clear();
        transitions.add(new StateTransition(0, letter, 1));
        letter.clear();
        letter.add("a");
        transitions.add(new StateTransition(1, letter, 1));
        letter.clear();
        transitions.add(new StateTransition(1, letter, 1));
        List<Integer> sinks = new ArrayList<>();
        sinks.add(1);

        MojmirAutomaton<LTLPropEquivalenceClass, String> mockMA = mockMA(2, transitions, sinks);

        RabinAutomaton<LTLPropEquivalenceClass, String> ra = new RabinAutomaton(mockMA, alphabet);
        assertEquals(1, ra.getStates().size());
    }

    @Test
    public void test2() {
        // Example from the paper, figure 3
        Set<String> alphabet = generateAlphabet(3);
        Set<StateTransition> transitions = new HashSet<>();
        HashSet<String> letter = new HashSet<>();

        Set<Set<String>> letters = Sets.powerSet(alphabet);
        for (Set<String> l : letters) {
            if (l.contains("a")) {
                transitions.add(new StateTransition(0, l, 1));
            }
            else {
                transitions.add(new StateTransition(0, l, 2));
            }
            if (l.contains("c")) {
                transitions.add(new StateTransition(1, l, 3));
            }
            else if (l.contains("b")) {
                transitions.add(new StateTransition(1, l, 1));
            }
            else {
                transitions.add(new StateTransition(1, l, 2));
            }
            transitions.add(new StateTransition(2, l, 2));
            transitions.add(new StateTransition(3, l, 3));
        }
        List<Integer> sinks = new ArrayList<>();
        sinks.add(2);
        sinks.add(3);

        MojmirAutomaton<LTLPropEquivalenceClass, String> mockMA = mockMA(4, transitions, sinks);

        RabinAutomaton<LTLPropEquivalenceClass, String> ra = new RabinAutomaton(mockMA, alphabet);
        assertEquals(2, ra.getStates().size());
        int counter = 0;
        for (RabinAutomaton.State s : ra.getStates()) {
            System.out.println("Ranking for RA state #" + counter++);
            for (Object m : s.getMojmirStates()) {
                System.out.println(((MojmirAutomaton.State)m).getLabel().toString());
            }
        }
    }

    private class StateTransition {
        int from;
        Set<String> letter;
        int to;

        StateTransition(int from, Set<String> letter, int to) {
            this.from = from;
            this.letter = letter;
            this.to = to;
        }
    }

    private MojmirAutomaton mockMA(int numStates, Collection<StateTransition> transitions, Collection<Integer> sinks) {
        MojmirAutomaton<LTLPropEquivalenceClass, String> result = mock(MojmirAutomaton.class);
        ArrayList<MojmirAutomaton.State> states = new ArrayList<>();
        for (int i = 0; i < numStates; i++) {
            states.add(mock(MojmirAutomaton.State.class));
            when(states.get(i).getLabel()).thenReturn("q" + i);
        }

        when(result.getInitialState()).thenReturn(states.get(0));

        HashSet<MojmirAutomaton<ltl2rabin.LTLPropEquivalenceClass, java.lang.String>.State> sinkStates = new HashSet<>();
        sinks.forEach(i -> sinkStates.add(states.get(i)));
        when(result.getSinks()).thenReturn(sinkStates);

        for (StateTransition t : transitions) {
            when(states.get(t.from).readLetter(t.letter)).thenReturn(states.get(t.to));
        }

        return result;
    }

    Set<String> generateAlphabet (int numLetters) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numLetters; i++) {
            result.add(Character.toString((char) ('a' + i)));
        }
        return result;
    }
}