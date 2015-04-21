package ltl2rabin;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MojmirAutomatonTest {
    MojmirStateAcceptanceFunction<LTLPropEquivalenceClass> f;

    @Before
    public void setUp() {
        f = new MojmirStateAcceptanceFunction<>();
    }

    @Test
    public void testCase1() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(4, classyMojmir.getStates().size());
    }

    @Test
    public void testCase2() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | b | c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(3, classyMojmir.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("X b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(4, classyMojmir.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("F b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(2, classyMojmir.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a & b & c");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(3, classyMojmir.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a U b");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(3, classyMojmir.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        Set<Set<String>> alphabet = Sets.powerSet(ltlListener.getTerminalSymbols());

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> classyMojmir = new MojmirAutomaton<>(
                new LTLPropEquivalenceClass(ltlOr), new AfGFunction(), alphabet, f);
        assertEquals(3, classyMojmir.getStates().size());
    }


}