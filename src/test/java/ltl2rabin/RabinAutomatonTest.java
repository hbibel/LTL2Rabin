package ltl2rabin;

import ltl2rabin.AutomatonMockFactory.StateTransition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

// TODO: Test Rabin pairs
public class RabinAutomatonTest {
    LTLVariable variable_a;
    LTLVariable variable_b;
    LTLVariable variable_c;
    LTLFactoryFromString ltlFactoryFromString;
    AutomatonMockFactory.MAMockFactory automatonMockFactory = new AutomatonMockFactory.MAMockFactory();
    RabinAutomatonFactory rabinAutomatonFactory = new RabinAutomatonFactory();

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

        ltlFactoryFromString = new LTLFactoryFromString();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test1() {
        Set<Set<String>> alphabet = AutomatonMockFactory.generateAlphabet(1);
        Set<StateTransition> transitions = new HashSet<>();
        HashSet<String> letter = new HashSet<>();
        letter.add("a");
        transitions.add(new StateTransition(0, letter, 1));
        letter = new HashSet<>();
        transitions.add(new StateTransition(0, letter, 1));
        letter = new HashSet<>();
        letter.add("a");
        transitions.add(new StateTransition(1, letter, 1));
        letter = new HashSet<>();
        transitions.add(new StateTransition(1, letter, 1));
        List<Integer> sinks = new ArrayList<>();
        sinks.add(1);
        List<Integer> acceptingStates = new ArrayList<>();

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMA = automatonMockFactory.mockMe(2, transitions, sinks, acceptingStates, alphabet);

        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(1, ra.getStates().size());

        List<Set<String>> word1 = AutomatonMockFactory.createWord("", "a");
        List<Set<String>> word2 = AutomatonMockFactory.createWord("a");
        assertEquals(ra.run(word1), ra.run(word2));
    }

    @Test
    public void test2() {
        // Example from the paper, figure 3
        Set<Set<String>> alphabet = AutomatonMockFactory.generateAlphabet(3);
        Set<StateTransition> transitions = new HashSet<>();

        for (Set<String> l : alphabet) {
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
        List<Integer> acceptingStates = new ArrayList<>();
        acceptingStates.add(3);

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMA = automatonMockFactory.mockMe(4, transitions, sinks, acceptingStates, alphabet);

        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(2, ra.getStates().size());

        List<Set<String>> emptyWord = AutomatonMockFactory.createWord("");
        assertEquals(ra.getInitialState(), ra.run(emptyWord));

        List<Set<String>> wordB = AutomatonMockFactory.createWord("b");
        assertEquals(ra.getInitialState(), ra.run(wordB));

        List<Set<String>> wordA = AutomatonMockFactory.createWord("a");
        assertNotEquals(ra.getInitialState(), ra.run(wordA));

        List<Set<String>> wordABC = AutomatonMockFactory.createWord("a", "bc");
        assertEquals(ra.getInitialState(), ra.run(wordABC));

        List<Set<String>> wordACEtc = AutomatonMockFactory.createWord("ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac");
        assertNotEquals(ra.getInitialState(), ra.run(wordACEtc));
    }

    @Test
    public void test3() {
        Set<Set<String>> alphabet = AutomatonMockFactory.generateAlphabet(2);
        Set<StateTransition> transitions = new HashSet<>();

        for (Set<String> l : alphabet) {
            transitions.add(new StateTransition(4, l, 4));
            transitions.add(new StateTransition(3, l, 3));
            transitions.add(new StateTransition(2, l, 3));
            transitions.add(new StateTransition(1, l, 3));
        }
        Set<String> l = new HashSet<>();
        l.add("a");
        transitions.add(new StateTransition(0, l, 2));
        l = new HashSet<>();
        l.add("b");
        transitions.add(new StateTransition(0, l, 1));
        l = new HashSet<>();
        transitions.add(new StateTransition(0, l, 4));
        l = new HashSet<>();
        l.add("a");
        l.add("b");
        transitions.add(new StateTransition(0, l, 4));

        List<Integer> sinks = new ArrayList<>();
        sinks.add(3);
        sinks.add(4);
        List<Integer> acceptingStates = new ArrayList<>(); // TODO

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMA = automatonMockFactory.mockMe(5, transitions, sinks, acceptingStates, alphabet);

        RabinAutomaton<List<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, Set<String>> ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(3, ra.getStates().size());

        List<Set<String>> emptyWord = AutomatonMockFactory.createWord("");
        assertEquals(ra.getInitialState(), ra.run(emptyWord));

        List<Set<String>> wordAB = AutomatonMockFactory.createWord("ab");
        assertEquals(ra.getInitialState(), ra.run(wordAB));
        assertEquals(ra.run(wordAB), ra.run(emptyWord));

        List<Set<String>> wordBBBB = AutomatonMockFactory.createWord("b", "b", "b", "b", "b", "b", "b");
        List<Set<String>> wordBBBBB = AutomatonMockFactory.createWord("b", "b", "b", "b", "b", "b", "b", "b");
        List<Set<String>> wordAAAA = AutomatonMockFactory.createWord("a", "a", "a", "a", "a", "a", "a", "a", "a", "a");
        List<Set<String>> wordAAAAB = AutomatonMockFactory.createWord("a", "a", "a", "a", "a", "a", "a", "a", "a", "b");
        assertFalse(ra.run(wordBBBB).equals(ra.run(wordAAAA)));
        assertEquals(ra.run(wordBBBB), ra.run(wordBBBBB));
        assertEquals(ra.run(wordAAAAB), ra.run(wordBBBB));
    }

}