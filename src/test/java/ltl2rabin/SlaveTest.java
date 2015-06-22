package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SlaveTest {
    Variable aVariable;
    Variable bVariable;
    Variable cVariable;
    Formula tt;
    Formula ff;
    LTLFactoryFromString ltlFactoryFromString;
    MAMockFactory maMockFactory;

    private String mojmirStateStringFromLTL(Formula f) {
        return "state(" + f.toString() + ")";
    }
    private String rabinStateStringFromList(List<Formula> l) {
        String result = "State{mojmirStates=[";
        Iterator<Formula> it = l.iterator();
        while (it.hasNext()) {
            Formula next = it.next();
            result += mojmirStateStringFromLTL(next);
            if (it.hasNext()) result += ", ";
        }
        result += "]}";
        return result;
    }

    @Before
    public void setUp() throws Exception {
        aVariable = new Variable("a");
        bVariable = new Variable("b");
        cVariable = new Variable("c");
        tt = new Boolean(true);
        ff = new Boolean(false);
        maMockFactory = new MAMockFactory();
        ltlFactoryFromString = new LTLFactoryFromString();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void basicTest() {
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(2));
        SlaveFromMojmirFactory rabinAutomatonFactory = new SlaveFromMojmirFactory(alphabet);

        Formula initLabel = new Variable("b");
        MAMockFactory.MAMock m = maMockFactory.createMAMock(alphabet, initLabel);
        m.addState(tt, true, ImmutableSet.of(Collections.emptySet()));
        m.addState(ff, true);
        m.whenReadingToken(initLabel, "b", tt);
        m.whenReadingLetter(initLabel, Collections.emptySet(), ff);
        m.whenReadingLetter(initLabel, ImmutableSet.of("a"), ff);

        MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> mockMA = m.toMA();

        Slave ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(1, ra.getStates().size());
        assertEquals(0, ra.getMaxRank());

        // check for correct rabin pair
        Slave.State q0 = ra.getInitialState();
        ImmutableSet<Slave.Transition> fm = ra.failMerge(0, Collections.emptySet());
        assertEquals(ImmutableSet.of(new Slave.Transition(q0, ImmutableSet.of("a"), q0), new Slave.Transition(q0, Collections.emptySet(), q0)), ra.failMerge(0, Collections.emptySet()));
        assertEquals(ImmutableSet.of(new Slave.Transition(q0, ImmutableSet.of("a", "b"), q0), new Slave.Transition(q0, ImmutableSet.of("b"), q0)), ra.succeed(0, Collections.emptySet()));

        List<Set<String>> word1 = AutomatonMockFactory.createWord("", "a");
        List<Set<String>> word2 = AutomatonMockFactory.createWord("a");
        assertEquals(ra.run(word1), ra.run(word2));
    }

    @Test
    public void test1() {
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(1));
        SlaveFromMojmirFactory rabinAutomatonFactory = new SlaveFromMojmirFactory(alphabet);

        Formula initlabel = new Or(ImmutableList.of(aVariable, bVariable, cVariable));
        MAMockFactory.MAMock m = maMockFactory.createMAMock(alphabet, initlabel);
        m.addState(tt, true, ImmutableSet.of(Collections.emptySet()));
        m.addState(ff, true);
        m.whenReadingToken(initlabel, "a", tt);
        m.whenReadingToken(initlabel, "b", tt);
        m.whenReadingToken(initlabel, "c", tt);
        m.whenReadingLetter(initlabel, Collections.emptySet(), ff);

        MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> mockMA = m.toMA();

        Slave ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(1, ra.getStates().size());
        assertEquals(0, ra.getMaxRank());

        // check for correct rabin pair
        ImmutableSet.Builder<Slave.Transition> failBuyBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Slave.Transition> succeedBuilder = new ImmutableSet.Builder<>();
        for (int i = 0; i <= ra.getMaxRank() ; i++) {
            failBuyBuilder.addAll(ra.failMerge(i, Collections.emptySet()));
            succeedBuilder.addAll(ra.succeed(i, Collections.emptySet()));
        }
        ImmutableSet<Slave.Transition> failBuy = failBuyBuilder.build();
        ImmutableSet<Slave.Transition> succeed = succeedBuilder.build();
        alphabet.forEach(letter -> {
            if (letter.isEmpty()) {
                assertTrue(failBuy.contains(new Slave.Transition(ra.getInitialState(), letter, ra.getInitialState())));
                assertFalse(succeed.contains(new Slave.Transition(ra.getInitialState(), letter, ra.getInitialState())));
            }
            else {
                assertTrue(succeed.contains(new Slave.Transition(ra.getInitialState(), letter, ra.getInitialState())));
                assertFalse(failBuy.contains(new Slave.Transition(ra.getInitialState(), letter, ra.getInitialState())));
            }
        });

        List<Set<String>> word1 = AutomatonMockFactory.createWord("", "a");
        List<Set<String>> word2 = AutomatonMockFactory.createWord("a");
        assertEquals(ra.run(word1), ra.run(word2));
    }

    @Test
    public void test2() {
        // Example 4.12 from the paper: a | (b U c)
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3));
        SlaveFromMojmirFactory rabinAutomatonFactory = new SlaveFromMojmirFactory(alphabet);

        U bUc = new U(bVariable, cVariable);
        Formula initialLabel = new And(aVariable, new X(bUc));
        MAMockFactory.MAMock m = maMockFactory.createMAMock(alphabet, initialLabel);
        m.addState(bUc, false);
        m.addState(ff, true);
        m.addState(tt, true, ImmutableSet.of(Collections.emptySet())); // tt accepts
        m.whenReadingToken(initialLabel, "a", bUc);
        m.whenNotReadingToken(initialLabel, "a", ff);
        m.whenReadingLetter(bUc, ImmutableSet.of("b"), bUc);
        m.whenReadingLetter(bUc, ImmutableSet.of("a", "b"), bUc);
        m.whenReadingLetter(bUc, ImmutableSet.of("a"), ff);
        m.whenReadingLetter(bUc, Collections.emptySet(), ff);
        m.whenReadingToken(bUc, "c", tt);
        MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> mockMA = m.toMA();

        Slave ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(2, ra.getStates().size());
        assertEquals(1, ra.getMaxRank());

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

        // check for correct rabin pair
        ImmutableSet.Builder<Slave.Transition> failBuyBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Slave.Transition> succeedBuilder = new ImmutableSet.Builder<>();
        for (int i = 0; i <= ra.getMaxRank(); i++) {
            failBuyBuilder.addAll(ra.failMerge(i, Collections.emptySet()));
            succeedBuilder.addAll(ra.succeed(i, Collections.emptySet()));
        }
        ImmutableSet<Slave.Transition> failBuy = failBuyBuilder.build();
        ImmutableSet<Slave.Transition> succeed = succeedBuilder.build();
        Slave.State q0 = ra.getInitialState();
        Slave.State q1 = q0.readLetter(ImmutableSet.of("a"));
        assertNotEquals(q0, q1);
        Slave.Transition t4ac = new Slave.Transition(q1, ImmutableSet.of("a", "c"), q1);
        Slave.Transition t4abc = new Slave.Transition(q1, ImmutableSet.of("a", "b", "c"), q1);
        Slave.Transition t7c = new Slave.Transition(q1, ImmutableSet.of("c"), q0);
        Slave.Transition t7bc = new Slave.Transition(q1, ImmutableSet.of("b", "c"), q0);
        ImmutableSet<Slave.Transition> succeed1 = ImmutableSet.of(t4ac, t4abc, t7c, t7bc);
        assertEquals(succeed1, succeed);
        ImmutableSet.Builder<Slave.Transition> failBuilder = new ImmutableSet.Builder<>();
        alphabet.forEach(letter -> {
            if (!letter.contains("a")) {
                failBuilder.add(new Slave.Transition(q0, letter, q0)); // t1
            }
            if (ImmutableSet.copyOf(letter).equals(ImmutableSet.of("a")) || ImmutableSet.copyOf(letter).equals(ImmutableSet.of("b"))) {
                failBuilder.add(new Slave.Transition(q1, letter, q1)); // t5, t6
            }
        });
        failBuilder.add(t7bc).add(t7c).add(new Slave.Transition(q1, Collections.emptySet(), q0)); // t7, t8
        ImmutableSet.Builder<Slave.Transition> buyBuilder = new ImmutableSet.Builder<>();
        buyBuilder.add(new Slave.Transition(q1, ImmutableSet.of("a", "b"), q1));
        ImmutableSet<Slave.Transition> expected = failBuilder.addAll(buyBuilder.build()).build();
        assertEquals(expected, failBuy);

        Set<Formula> expectedSucceedingFormulasQ1_1 = ImmutableSet.of(initialLabel);
        assertEquals(expectedSucceedingFormulasQ1_1, ImmutableSet.copyOf(q1.succeedingFormulas(1)));
        Set<Formula> expectedSucceedingFormulasQ0_1 = ImmutableSet.of();
        assertEquals(expectedSucceedingFormulasQ0_1, ImmutableSet.copyOf(q0.succeedingFormulas(1)));
        Set<Formula> expectedSucceedingFormulasQ1_0 = ImmutableSet.of(bUc, initialLabel);
        assertEquals(expectedSucceedingFormulasQ1_0, ImmutableSet.copyOf(q1.succeedingFormulas(0)));

    }
}
