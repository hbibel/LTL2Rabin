package ltl2rabin;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
    public void test() {
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
/*
    @Test
    public void testCase1() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(2, rabinAutomaton.getStates().size());

        LTLUOperator u = new LTLUOperator(variable_b, variable_c);
        LTLOr o = new LTLOr(variable_a, u);
        List<LTLFormula> state1List = new ArrayList<>();
        state1List.add(u); state1List.add(o);
        List<LTLFormula> state2List = new ArrayList<>();
        state2List.add(o);
        List<String> expectedResultList = new ArrayList<>();
        expectedResultList.add(rabinStateStringFromList(state1List));expectedResultList.add(rabinStateStringFromList(state2List));
        assertEquals(expectedResultList.toString(), rabinAutomaton.getStates().toString());
    }

    @Test
    public void testCase2() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | b | c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(1, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("X b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(2, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("F b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(1, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a & b & c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(1, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a U b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(1, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<>(mojmirAutomaton, alphabet);

        assertEquals(1, rabinAutomaton.getStates().size());
    }*/
}