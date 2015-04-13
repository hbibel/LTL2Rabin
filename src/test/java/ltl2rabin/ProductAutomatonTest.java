package ltl2rabin;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ProductAutomatonTest {
    AutomatonMockFactory.RAMockFactory factory = new AutomatonMockFactory.RAMockFactory();

    @Test
    public void threeRAsWithTwoAlternatingStates() {
        Set<String> alphabet = AutomatonMockFactory.generateAlphabet(1);

        // All three RAs have the same transition function:
        Set<AutomatonMockFactory.StateTransition> transitions = new HashSet<>();
        Set<Set<String>> letters = Sets.powerSet(alphabet);
        for (Set<String> letter : letters) {
            transitions.add(new AutomatonMockFactory.StateTransition(0, letter, 1));
            transitions.add(new AutomatonMockFactory.StateTransition(1, letter, 0));
        }

        RabinAutomaton mockRA1 = factory.mockMe(2, transitions);
        RabinAutomaton mockRA2 = factory.mockMe(2, transitions);
        RabinAutomaton mockRA3 = factory.mockMe(2, transitions);

        ArrayList<RabinAutomaton> ras = new ArrayList<>(Arrays.asList(mockRA1, mockRA2, mockRA3));

        ProductAutomaton productAutomaton = new ProductAutomaton(ras, alphabet);

        assertEquals(2, productAutomaton.getStates().size());

        List<Set<String>> emptyWord = AutomatonMockFactory.createWord();
        List<Set<String>> twoLetterWord = AutomatonMockFactory.createWord("a", "a");
        List<Set<String>> oneLetterWord = AutomatonMockFactory.createWord("a");
        // assertEquals(productAutomaton.run(emptyWord), productAutomaton.run(twoLetterWord));
        // assertFalse(productAutomaton.run(emptyWord).equals(productAutomaton.run(oneLetterWord)));
    }
}