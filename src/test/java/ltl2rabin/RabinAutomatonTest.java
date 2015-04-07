package ltl2rabin;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        MojmirAutomaton<LTLPropEquivalenceClass, String> mockMA = (MojmirAutomaton<LTLPropEquivalenceClass, String>) mock(MojmirAutomaton.class);
        MojmirAutomaton<LTLPropEquivalenceClass, String>.State mockInitMAState = mock(MojmirAutomaton.State.class);
        MojmirAutomaton<LTLPropEquivalenceClass, String>.State mock2ndMAState = mock(MojmirAutomaton.State.class);
        when(mockMA.getInitialState()).thenReturn(mockInitMAState);
        HashSet<MojmirAutomaton<ltl2rabin.LTLPropEquivalenceClass, java.lang.String>.State> sinks = new HashSet<>();
        sinks.add(mock2ndMAState);
        when(mockMA.getSinks()).thenReturn(sinks);
        Set<String> alphabet = Main.stringToLTLFormula("a").getTerminalSymbols();
        Set<Set<String>> letters = Sets.powerSet(alphabet);
        for (Set<String> l : letters) {
            when(mockInitMAState.readLetter(l)).thenReturn(mock2ndMAState);
        }
        RabinAutomaton<LTLPropEquivalenceClass, String> ra = new RabinAutomaton(mockMA, alphabet);
        assertEquals(1, ra.getStates().size());
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