package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MojmirAutomatonTest {
    MojmirAutomatonFactoryFromString automatonFactory;
    MAMockFactory mockFactory = new MAMockFactory();


    LTLFormula a;
    LTLFormula b;
    LTLFormula c;
    LTLFormula tt;
    LTLFormula ff;

    @Before
    public void setUp() {
        automatonFactory = new MojmirAutomatonFactoryFromString(ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3)));
        a = new LTLVariable("a");
        b = new LTLVariable("b");
        c = new LTLVariable("c");
        tt = new LTLBoolean(true);
        ff = new LTLBoolean(false);
    }

    @Test
    public void testCase0() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a");

        MAMockFactory.MAMock m = mockFactory.createMAMock(AutomatonMockFactory.generateAlphabet(1), a);
        m.addState(tt, true);
        m.addState(ff, true);
        m.setStateAccepting(tt);
        m.whenNotReadingToken(a, "a", ff);
        m.whenReadingToken(a, "a", tt);
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mock = m.toMA();

        assertEquals(mock.getInitialState().getLabel(), mojmirAutomaton.getInitialState().getLabel());
        assertEquals(mock.getMaxRank(), mojmirAutomaton.getMaxRank());
        assertEquals(mock.getStates().size(), mojmirAutomaton.getStates().size());
        assertTrue(mojmirAutomaton.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(tt))));
        mojmirAutomaton.getStates().forEach(state -> {
            LTLPropEquivalenceClass label = state.getLabel();
            if (!label.equals(new LTLPropEquivalenceClass(tt))) {
                assertFalse(mojmirAutomaton.isAcceptingState(state));
            }
        });
    }

    @Test
    public void testCase1() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | (b U c)");
        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase2() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | b | c");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("X b");
        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("F b");
        assertEquals(2, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a & b & c");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a U b");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }


}