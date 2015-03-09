package ltl2rabin;

import org.junit.Test;

import java.util.HashSet;

public class MojmirAutomatonTest {

    @Test
    public void testReach() throws Exception {
        LTLListener ltlListener = Main.stringToLTLFormula("a | (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton(ltlOr, new AfFunction(), alphabet);

        int a = 1 + 1;
    }
}