package ltl2rabin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MojmirAutomatonTest {
    MojmirAutomatonFactoryFromString automatonFactory;
    MojmirAutomatonFactoryFromLTLAndSet automatonFactoryWithSet;

    Formula aVariable;
    Formula bVariable;
    Formula cVariable;
    Formula tt;
    Formula ff;

    @Before
    public void setUp() {
        automatonFactory = new MojmirAutomatonFactoryFromString(ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3)));
        automatonFactoryWithSet = new MojmirAutomatonFactoryFromLTLAndSet(ImmutableSet.copyOf(AutomatonMockFactory.generateAlphabet(3)));
        aVariable = new Variable("a");
        bVariable = new Variable("b");
        cVariable = new Variable("c");
        tt = new Boolean(true);
        ff = new Boolean(false);
    }

    @Test
    public void basicTest() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a");

        assertEquals(new LTLPropEquivalenceClass(aVariable), mojmirAutomaton.getInitialState().getLabel());
        // assertEquals(2, mojmirAutomaton.getMaxRank());
        assertEquals(3, mojmirAutomaton.getStates().size());
        assertTrue(mojmirAutomaton.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(tt))));
        assertFalse(mojmirAutomaton.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(ff))));
        assertFalse(mojmirAutomaton.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(aVariable))));
        mojmirAutomaton.getStates().forEach(state -> {
            LTLPropEquivalenceClass label = state.getLabel();
            if (!label.equals(new LTLPropEquivalenceClass(tt))) {
                assertFalse(mojmirAutomaton.isAcceptingState(state));
            }
        });
        assertEquals(new LTLPropEquivalenceClass(tt), mojmirAutomaton.getInitialState().readLetter(ImmutableSet.of("a")).getLabel());
        assertEquals(0, mojmirAutomaton.getMaxRank());
    }

    @Test
    public void allOperands() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> m = automatonFactory.createFrom("F ( ( a | b ) & ( a U ( b & ( X ( c ) ) ) ) & ( !c ) )");

        assertEquals(5, m.getStates().size());
        Formula aOrB = new Or(aVariable, bVariable);
        Formula bAndXc = new And(bVariable, new X(cVariable));
        Formula phi1 = new U(aVariable, bAndXc);
        Formula notC = new Variable("c", true);
        Formula phi = new F(new And(ImmutableList.of(aOrB, phi1, notC)));

        // every state label exists:
        ImmutableList.of(phi, new Or(phi, phi1), new Or(phi, cVariable), new Or(phi, new Or(cVariable, phi1)), tt)
                .forEach(ltlFormula -> {
                    assertTrue(m.getStates().contains(new MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>(new LTLPropEquivalenceClass(ltlFormula))));
                    // only the state labeled 'tt' is accepting, the others are not
                    if (!ltlFormula.equals(tt)) {
                        assertFalse(m.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(ltlFormula))));
                    }
                    else {
                        assertTrue(m.isAcceptingState((new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(tt)))));
                    }
                });
        assertEquals(3, m.getMaxRank());
    }

    @Test
    public void curlyGTest() throws Exception {
        Formula phi = new Or(
                new And(aVariable, new G(bVariable)),
                new G(new And(aVariable, bVariable))
        );
        ImmutableSet<Formula> curlyG = ImmutableSet.of(bVariable);
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactoryWithSet.createFrom(new Pair<>(phi, curlyG));
        Formula phi1 = new Or(new G(bVariable), new G(new And(aVariable, bVariable)));

        assertEquals(3, mojmirAutomaton.getStates().size());
        ImmutableList.of(phi, phi1, new G(new And(aVariable, bVariable)))
                .forEach(ltlFormula -> {
                    assertTrue(mojmirAutomaton.getStates().contains(new MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>(new LTLPropEquivalenceClass(ltlFormula))));
                    // only the state labeled with phi_1 is accepting, the others are not
                    if (!ltlFormula.equals(phi1)) {
                        assertFalse(mojmirAutomaton.isAcceptingState(new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(ltlFormula))));
                    }
                    else {
                        assertTrue(mojmirAutomaton.isAcceptingState((new MojmirAutomaton.State<>(new LTLPropEquivalenceClass(phi1)))));
                    }
                });
        assertEquals(0, mojmirAutomaton.getMaxRank());
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