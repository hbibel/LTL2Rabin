package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LTLPropEquivalenceClassTest {
    List<LTLVariable> variables;
    List<LTLPropEquivalenceClass> equivalenceClasses;
    int variableCount = 3; // should not surpass 26. Otherwise change variable generation in the setUp() method.
    LTLFormula tt;
    LTLFormula ff;

    @Before
    public void setUp () {
        variables = new ArrayList<>();
        equivalenceClasses = new ArrayList<>();
        // generate necessary variables
        for (int i = 0; i < variableCount; i++) {
            variables.add(new LTLVariable(Character.toString((char) ('a' + i))));
            equivalenceClasses.add(new LTLPropEquivalenceClass(variables.get(i)));
        }
        tt = new LTLBoolean(true);
        ff = new LTLBoolean(false);
    }

    @After
    public void tearDown() {
        equivalenceClasses.clear();
        variables.clear();
    }

    @Test
    public void booleanPropositionalEquivalenceTest() {
        LTLFormula f1 = new LTLBoolean(true);
        LTLFormula f2 = new LTLBoolean(false);
        LTLFormula f3 = new LTLBoolean(true);
        LTLPropEquivalenceClass w1 = new LTLPropEquivalenceClass(f1);
        LTLPropEquivalenceClass w2 = new LTLPropEquivalenceClass(f2);
        LTLPropEquivalenceClass w3 = new LTLPropEquivalenceClass(f3);

        // true = true (different objects)
        assertTrue(w1.equals(w3));
        // true != false
        assertFalse(w1.equals(w2));
    }

    @Test
    public void variablePropositionalEquivalenceTest() {
        LTLVariable a = new LTLVariable("a");
        LTLVariable notA = new LTLVariable("a", true);
        LTLPropEquivalenceClass w = new LTLPropEquivalenceClass(a);
        LTLPropEquivalenceClass notW = new LTLPropEquivalenceClass(notA);

        // a = a (different objects)
        assertTrue(w.equals(equivalenceClasses.get(0)));
        // a != !a
        assertFalse(notW.equals(equivalenceClasses.get(0)));
        // a != b
        assertFalse(w.equals(equivalenceClasses.get(1)));
    }

    @Test
    public void operatorPropositionalEquivalenceTest() {
        List<Function<LTLFormula,LTLFormula>> gfxOperatorConstructors = Arrays.asList(
                LTLFOperator::new,
                LTLGOperator::new,
                LTLXOperator::new
        );
        gfxOperatorConstructors.forEach(constructor -> {
            LTLPropEquivalenceClass wa = new LTLPropEquivalenceClass(constructor.apply(variables.get(0)));
            LTLPropEquivalenceClass wa2 = new LTLPropEquivalenceClass(constructor.apply(variables.get(0)));
            LTLPropEquivalenceClass wb = new LTLPropEquivalenceClass(constructor.apply(variables.get(1)));

            // G a = G a (different objects)
            assertTrue(wa.equals(wa2));
            // G a = G a (same object)
            assertTrue(wa.equals(wa));
            // G a != G b
            assertFalse(wa.equals(wb));
        });
    }

    @Test
    public void uOperatorPropositionalEquivalenceTest() {
        LTLPropEquivalenceClass w1 = new LTLPropEquivalenceClass(new LTLUOperator(variables.get(0), variables.get(1)));
        LTLPropEquivalenceClass w2 = new LTLPropEquivalenceClass(new LTLUOperator(variables.get(0), variables.get(1)));
        LTLPropEquivalenceClass w3 = new LTLPropEquivalenceClass(new LTLUOperator(variables.get(1), variables.get(0)));

        // a U b = a U b
        assertTrue(w1.equals(w2));
        // a U b != b U a
        assertFalse(w1.equals(w3));
    }

    @Test
    public void andPropositionalEquivalenceTest() {
        LTLPropEquivalenceClass c1 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), variables.get(1))); // a & b
        LTLPropEquivalenceClass c2 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(1), variables.get(0))); // b & a
        ArrayList<LTLFormula> l1 = new ArrayList<>(Arrays.asList(variables.get(0), variables.get(1), variables.get(2)));
        LTLPropEquivalenceClass c3 = new LTLPropEquivalenceClass(new LTLAnd(l1)); // a & b & c
        LTLPropEquivalenceClass c4 = new LTLPropEquivalenceClass(new LTLAnd(new LTLAnd(variables.get(0), variables.get(1)), variables.get(2))); // (a & b) & c
        LTLPropEquivalenceClass c5 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), new LTLAnd(variables.get(1), variables.get(2)))); // a & (b & c)
        LTLPropEquivalenceClass c6 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), variables.get(2))); // a & c

        // a & b = b & a
        assertTrue(c1.equals(c2));
        // a & b & c != a & b
        assertFalse(c1.equals(c3));
        // a & b & c != a & c
        assertFalse(c3.equals(c6));
        // (a & b) & c = a & (b & c) = a & b & c
        assertTrue(c4.equals(c5));
        assertTrue(c4.equals(c3));
        assertTrue(c5.equals(c3));
    }

    @Test
    public void orPropositionalEquivalenceTest() {
        LTLPropEquivalenceClass c1 = new LTLPropEquivalenceClass(new LTLOr(variables.get(0), variables.get(1))); // a | b
        LTLPropEquivalenceClass c2 = new LTLPropEquivalenceClass(new LTLOr(variables.get(1), variables.get(0))); // b | a
        ArrayList<LTLFormula> l1 = new ArrayList<>(Arrays.asList(variables.get(0), variables.get(1), variables.get(2)));
        LTLPropEquivalenceClass c3 = new LTLPropEquivalenceClass(new LTLOr(l1)); // a | b | c
        LTLPropEquivalenceClass c4 = new LTLPropEquivalenceClass(new LTLOr(new LTLOr(variables.get(0), variables.get(1)), variables.get(2))); // (a | b) | c
        LTLPropEquivalenceClass c5 = new LTLPropEquivalenceClass(new LTLOr(variables.get(0), new LTLOr(variables.get(1), variables.get(2)))); // a | (b | c)
        LTLPropEquivalenceClass c6 = new LTLPropEquivalenceClass(new LTLOr(variables.get(0), variables.get(2))); // a | c

        // a | b = b | a
        assertTrue(c1.equals(c2));
        // a | b | c != a | b
        assertFalse(c1.equals(c3));
        // a | b | c != a | c
        assertFalse(c3.equals(c6));
        // (a | b) | c = a | (b | c) = a | b | c
        assertTrue(c4.equals(c5));
        assertTrue(c4.equals(c3));
        assertTrue(c5.equals(c3));
    }

    @Test
    public void booleanLaws() {
        // Testing against some laws, taken from here: https://en.wikipedia.org/wiki/Boolean_algebra#Laws

        LTLFormula bORc = new LTLOr(variables.get(1), variables.get(2)); // b | c
        LTLPropEquivalenceClass c1 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), bORc)); // a & (b | c)
        LTLFormula aANDb = new LTLAnd(variables.get(0), variables.get(1)); // a & b
        LTLFormula aANDc = new LTLAnd(variables.get(0), variables.get(2)); // a & c
        LTLPropEquivalenceClass c2 = new LTLPropEquivalenceClass(new LTLOr(aANDb, aANDc));
        // a & (b | c) = (a & b) | (a & c)
        assertTrue(c1.equals(c2));

        LTLPropEquivalenceClass c3 = new LTLPropEquivalenceClass(new LTLOr(variables.get(0), new LTLBoolean(false))); // a | false
        LTLPropEquivalenceClass c4 = new LTLPropEquivalenceClass(variables.get(0)); // a
        // a | false = a
        assertTrue(c3.equals(c4));
        // a & true = a
        LTLPropEquivalenceClass c5 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), new LTLBoolean(true))); // a & true
        assertTrue(c4.equals(c5));

        LTLPropEquivalenceClass c6 = new LTLPropEquivalenceClass(new LTLAnd(variables.get(1), bORc)); // b & (b | c)
        // b & (b | c) = b
        assertTrue(c6.equals(new LTLPropEquivalenceClass(variables.get(1))));
    }

    @Test
    public void implicationTest() {
        // a & b |= a
        assertTrue(new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(0)));
        // a & b |= b
        assertTrue(new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(1)));

        // a | b |/= a
        assertFalse(new LTLPropEquivalenceClass(new LTLOr(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(0)));
        // a | b |/= b
        assertFalse(new LTLPropEquivalenceClass(new LTLOr(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(1)));

        // a & b |= a | b
        assertTrue(new LTLPropEquivalenceClass(new LTLAnd(variables.get(0), variables.get(1))).implies(
                new LTLPropEquivalenceClass(new LTLOr(variables.get(0), variables.get(1)))));

        // tt |/= a
        assertFalse(new LTLPropEquivalenceClass(tt).implies(equivalenceClasses.get(0)));
        // tt |= tt
        assertTrue(new LTLPropEquivalenceClass(tt).implies(new LTLPropEquivalenceClass(tt)));
        // tt |/= ff
        assertFalse(new LTLPropEquivalenceClass(tt).implies(new LTLPropEquivalenceClass(ff)));
    }
}