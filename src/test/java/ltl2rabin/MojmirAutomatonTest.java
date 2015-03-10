package ltl2rabin;

import org.junit.Test;

import java.util.HashSet;

public class MojmirAutomatonTest {

    @Test
    public void testReach() throws Exception {
        LTLBoolean f = new LTLBoolean(false);
        LTLBoolean f2 = new LTLBoolean(false);
        boolean blah = f.equals(f2);

        LTLListener ltlListener = Main.stringToLTLFormula("a & (b U c)");
        LTLFormula ltlOr = ltlListener.getLtlTree();
        HashSet<String> alphabet = ltlListener.getTerminalSymbols();
        MojmirAutomaton mojmirAutomaton = new MojmirAutomaton(ltlOr, new AfFunction(), alphabet);

        int a = 1 + 1;
    }
}