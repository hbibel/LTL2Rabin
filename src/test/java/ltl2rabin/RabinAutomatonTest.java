package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class RabinAutomatonTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCase1() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton<LTLFormula, String> mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(2, rabinAutomaton.getStates().size());
    }

    @Test
    public void testCase2() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | b | c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("X b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("F b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(2, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a & b & c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a U b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);
        RabinAutomaton<LTLFormula, String> rabinAutomaton = new RabinAutomaton<LTLFormula, String>(mojmirAutomaton, alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }
}