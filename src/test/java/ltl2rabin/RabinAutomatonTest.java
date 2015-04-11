package ltl2rabin;

import com.google.common.collect.Sets;
import ltl2rabin.AutomatonMockFactory.StateTransition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class RabinAutomatonTest {
    LTLVariable variable_a;
    LTLVariable variable_b;
    LTLVariable variable_c;
    LTLFactoryFromString factory;
    AutomatonMockFactory.MAMockFactory automatonMockFactory = new AutomatonMockFactory.MAMockFactory();

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
        Set<String> alphabet = automatonMockFactory.generateAlphabet(1);
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

        MojmirAutomaton<LTLPropEquivalenceClass, String> mockMA = automatonMockFactory.mockMe(2, transitions, sinks);

        RabinAutomaton<LTLPropEquivalenceClass, String> ra = new RabinAutomaton(mockMA, alphabet);
        assertEquals(1, ra.getStates().size());

        List<Set<String>> word1 = createWord("", "a");
        List<Set<String>> word2 = createWord("a");
        assertEquals(ra.run(word1), ra.run(word2));
    }

    @Test
    public void test2() {
        // Example from the paper, figure 3
        Set<String> alphabet = automatonMockFactory.generateAlphabet(3);
        Set<StateTransition> transitions = new HashSet<>();

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

        MojmirAutomaton<LTLPropEquivalenceClass, String> mockMA = automatonMockFactory.mockMe(4, transitions, sinks);

        RabinAutomaton<LTLPropEquivalenceClass, String> ra = new RabinAutomaton(mockMA, alphabet);
        assertEquals(2, ra.getStates().size());

        for (RabinAutomaton.State s : ra.getStates()) {
            System.out.println("Ranking for RA state " + s.getLabel());
            for (Object m : s.getMojmirStates()) {
                System.out.println(((MojmirAutomaton.State)m).getLabel().toString());
            }
        }

        List<Set<String>> emptyWord = createWord("");
        assertEquals(ra.getStates().get(0), ra.run(emptyWord));

        List<Set<String>> wordB = createWord("b");
        assertEquals(ra.getStates().get(0), ra.run(wordB));

        List<Set<String>> wordA = createWord("a");
        assertEquals(ra.getStates().get(1), ra.run(wordA));

        List<Set<String>> wordABC = createWord("a", "bc");
        assertEquals(ra.getStates().get(0), ra.run(wordABC));

        List<Set<String>> wordACEtc = createWord("ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac", "ac");
        assertEquals(ra.getStates().get(1), ra.run(wordACEtc));
    }

    List<Set<String>> createWord(String... letters) {
        List<Set<String>> result = new ArrayList<>();
        for (String l : letters) {
            char[] parts = l.toCharArray();
            List<String> partsAsStrings = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                partsAsStrings.add("" + parts[i]);
            }
            result.add(new HashSet<>(partsAsStrings));
        }
        return result;
    }
}