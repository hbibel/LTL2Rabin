package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SlaveTest {
    LTLVariable aVariable;
    LTLVariable bVariable;
    LTLVariable cVariable;
    LTLFormula tt;
    LTLFormula ff;
    LTLFactoryFromString ltlFactoryFromString;
    MAMockFactory maMockFactory;

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
        aVariable = new LTLVariable("a");
        bVariable = new LTLVariable("b");
        cVariable = new LTLVariable("c");
        tt = new LTLBoolean(true);
        ff = new LTLBoolean(false);
        maMockFactory = new MAMockFactory();
        ltlFactoryFromString = new LTLFactoryFromString();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test1() {
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(1));
        SlaveFromMojmirFactory rabinAutomatonFactory = new SlaveFromMojmirFactory(alphabet);

        LTLFormula initlabel = new LTLOr(ImmutableList.of(aVariable, bVariable, cVariable));
        MAMockFactory.MAMock m = maMockFactory.createMAMock(alphabet, initlabel);
        m.addState(tt, true);
        m.addState(ff, true);
        m.whenReadingToken(initlabel, "a", tt);
        m.whenReadingToken(initlabel, "b", tt);
        m.whenReadingToken(initlabel, "c", tt);
        m.whenReadingLetter(initlabel, Collections.emptySet(), ff);
        m.setStateAccepting(tt);

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMA = m.toMA();

        Slave ra = rabinAutomatonFactory.createFrom(mockMA);
        assertEquals(1, ra.getStates().size());

        // check for correct rabin pair
        ImmutableSet.Builder<Slave.Transition> failBuyBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Slave.Transition> succeedBuilder = new ImmutableSet.Builder<>();
        for (int i = 0; i < mockMA.getMaxRank(); i++) {
            failBuyBuilder.addAll(ra.failBuy(i));
            succeedBuilder.addAll(ra.succeed(i));
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
        // Example from the paper, figure 3
        ImmutableSet<Set<String>> alphabet = ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3));
        SlaveFromMojmirFactory rabinAutomatonFactory = new SlaveFromMojmirFactory(alphabet);

        LTLUOperator bUc = new LTLUOperator(bVariable, cVariable);
        LTLFormula initialLabel = new LTLAnd(aVariable, new LTLXOperator(bUc));
        MAMockFactory.MAMock m = maMockFactory.createMAMock(alphabet, initialLabel);
        m.addState(bUc, false);
        m.addState(ff, true);
        m.addState(tt, true);
        m.whenReadingToken(initialLabel, "a", bUc);
        m.whenNotReadingToken(initialLabel, "a", ff);
        m.whenReadingLetter(bUc, ImmutableSet.of("b"), bUc);
        m.whenReadingLetter(bUc, ImmutableSet.of("a", "b"), bUc);
        m.whenReadingLetter(bUc, ImmutableSet.of("a"), ff);
        m.whenReadingLetter(bUc, Collections.emptySet(), ff);
        m.whenReadingToken(bUc, "c", tt);
        m.setStateAccepting(tt);
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mockMA = m.toMA();

        Slave ra = rabinAutomatonFactory.createFrom(mockMA);
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


        // check for correct rabin pair
        ImmutableSet.Builder<Slave.Transition> failBuyBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<Slave.Transition> succeedBuilder = new ImmutableSet.Builder<>();
        for (int i = 0; i < mockMA.getMaxRank(); i++) {
            failBuyBuilder.addAll(ra.failBuy(i));
            succeedBuilder.addAll(ra.succeed(i));
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
    }
}
