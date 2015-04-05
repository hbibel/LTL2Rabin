package ltl2rabin;

import org.junit.Test;

import java.util.HashSet;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class MojmirAutomatonTest {

    @Test
    public void testCase1() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase2() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | b | c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("X b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("F b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(2, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a & b & c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a U b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton<LTLFormula, String>(ltlOr, new AfFunction(), alphabet);

        assertEquals(3, mojmirAutomaton.getStates().size());
    }


}