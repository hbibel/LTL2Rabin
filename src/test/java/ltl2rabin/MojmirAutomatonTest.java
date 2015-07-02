package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MojmirAutomatonTest {
    MojmirAutomatonFactoryFromString automatonFactory;
    MojmirAutomatonFactoryFromFormula automatonFactoryWithSet;

    Formula aVariable;
    Formula bVariable;
    Formula cVariable;
    Formula tt;
    Formula ff;

    @Before
    public void setUp() {
        automatonFactory = new MojmirAutomatonFactoryFromString(ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3)));
        automatonFactoryWithSet = new MojmirAutomatonFactoryFromFormula(ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3)));
        aVariable = new Variable("a");
        bVariable = new Variable("b");
        cVariable = new Variable("c");
        tt = new Boolean(true);
        ff = new Boolean(false);
    }

    @Test
    public void basicTest() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a");

        assertEquals(new PropEquivalenceClass(aVariable), mojmirAutomaton.getInitialState().getLabel());
        // assertEquals(2, mojmirAutomaton.getMaxRank());
        assertEquals(3, mojmirAutomaton.getStates().size());
        mojmirAutomaton.getStates().forEach(mState -> {
            if (mState.equals(new MojmirAutomaton.State<PropEquivalenceClass, Set<String>>(new PropEquivalenceClass(tt)))) {
                assertTrue(mState.isAcceptingState(Collections.emptySet()));
            }
            else {
                assertFalse(mState.isAcceptingState(Collections.emptySet()));
            }
        });
        assertEquals(new PropEquivalenceClass(tt), mojmirAutomaton.getInitialState().readLetter(ImmutableSet.of("a")).getLabel());
    }

    @Test
    public void allOperands() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> m = automatonFactory.createFrom("F ( ( a | b ) & ( a U ( b & ( X ( c ) ) ) ) & ( !c ) )");

        assertEquals(5, m.getStates().size());
        Formula aOrB = new Or(aVariable, bVariable);
        Formula bAndXc = new And(bVariable, new X(cVariable));
        Formula phi1 = new U(aVariable, bAndXc);
        Formula notC = new Variable("c", true);
        Formula phi = new F(new And(ImmutableList.of(aOrB, phi1, notC)));

        // every state label exists:
        ImmutableList.of(phi, new Or(phi, phi1), new Or(phi, cVariable), new Or(phi, new Or(cVariable, phi1)), tt)
                .forEach(ltlFormula -> {
                    assertTrue(m.getStates().contains(new MojmirAutomaton.State<PropEquivalenceClass, Set<String>>(new PropEquivalenceClass(ltlFormula))));
                });
    }

    @Test
    public void curlyGTest() throws Exception {
        Formula phi = new Or( // (a & G b) | G (a & b)
                new And(aVariable, new G(bVariable)),
                new G(new And(aVariable, bVariable))
        );
        ImmutableSet<G> curlyG1 = ImmutableSet.of(new G(bVariable));
        ImmutableSet<G> curlyG2 = ImmutableSet.of(new G(bVariable), new G(new And(aVariable, bVariable)));
        ImmutableSet<G> curlyG3 = ImmutableSet.of(new G(new And(aVariable, bVariable)));
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactoryWithSet.createFrom(phi);
        Formula gBOrGAAndB = new Or(new G(bVariable), new G(new And(aVariable, bVariable)));

        assertEquals(3, mojmirAutomaton.getStates().size());
        ImmutableList.of(phi, gBOrGAAndB, new G(new And(aVariable, bVariable)))
                .forEach(ltlFormula -> {
                    assertTrue(mojmirAutomaton.getStates().contains(new MojmirAutomaton.State<PropEquivalenceClass, Set<String>>(new PropEquivalenceClass(ltlFormula))));
                });
        mojmirAutomaton.getStates().forEach(mState -> {
            // the state labeled with (G b) | G (a & b) is accepting, the others are not
            if (mState.getLabel().equals(new PropEquivalenceClass(gBOrGAAndB))) {
                // the state labeled with (G b) | G (a & b) accepts under curlyG1, curlyG2 and curlyG3
                assertTrue(mState.isAcceptingState(curlyG1));
                assertTrue(mState.isAcceptingState(curlyG2));
                assertTrue(mState.isAcceptingState(curlyG3));
            }
            else if (mState.getLabel().equals(new PropEquivalenceClass(new G(new And(aVariable, bVariable))))) {
                // the state labeled with G (a & b) accepts under curlyG2 and curlyG3
                assertFalse(mState.isAcceptingState(curlyG1));
                assertTrue(mState.isAcceptingState(curlyG2));
                assertTrue(mState.isAcceptingState(curlyG3));
            }
            else {
                // the initial state, labeled with phi, accepts under curlyG2 and curlyG3
                assertFalse(mState.isAcceptingState(curlyG1));
                assertTrue(mState.isAcceptingState(curlyG2));
                assertTrue(mState.isAcceptingState(curlyG3));
            }
        });
    }

    @Test
    public void example33() throws Exception {
        /* This is example 3.3 from the paper. */
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | (b U c)");

        Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> expectedStates = new HashSet<>();
        expectedStates.add(new MojmirAutomaton.State<>(new PropEquivalenceClass(new LTLFactoryFromString().buildLTL("a | (b U c)").getLtlFormula()))); // q1
        expectedStates.add(new MojmirAutomaton.State<>(new PropEquivalenceClass(new LTLFactoryFromString().buildLTL("b U c").getLtlFormula()))); // q2
        expectedStates.add(new MojmirAutomaton.State<>(new PropEquivalenceClass(new LTLFactoryFromString().buildLTL("tt").getLtlFormula()))); // q3
        expectedStates.add(new MojmirAutomaton.State<>(new PropEquivalenceClass(new LTLFactoryFromString().buildLTL("ff").getLtlFormula()))); // q4
        assertEquals(4, mojmirAutomaton.getStates().size());
        expectedStates.forEach(expectedState -> {
            assertTrue(mojmirAutomaton.getStates().contains(expectedState));
        });

    }

    @Test
    public void testCase2() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | b | c");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("X b");
        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("F b");
        assertEquals(2, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a & b & c");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a U b");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a");
        assertEquals(3, mojmirAutomaton.getStates().size());
    }


}