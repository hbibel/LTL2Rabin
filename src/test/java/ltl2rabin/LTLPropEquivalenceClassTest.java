package ltl2rabin;

import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
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
    List<Variable> variables;
    List<PropEquivalenceClassWithBeeDeeDee> equivalenceClasses;
    int variableCount = 3; // should not surpass 26. Otherwise change variable generation in the setUp() method.
    Formula tt;
    Formula ff;

    @Before
    public void setUp () {
        variables = new ArrayList<>();
        equivalenceClasses = new ArrayList<>();
        // generate necessary variables
        for (int i = 0; i < variableCount; i++) {
            variables.add(new Variable(Character.toString((char) ('a' + i))));
            equivalenceClasses.add(new PropEquivalenceClassWithBeeDeeDee(variables.get(i)));
        }
        tt = new Boolean(true);
        ff = new Boolean(false);
    }

    @After
    public void tearDown() {
        equivalenceClasses.clear();
        variables.clear();
    }

    @Test
    public void booleanPropositionalEquivalenceTest() {
        Formula f1 = new Boolean(true);
        Formula f2 = new Boolean(false);
        Formula f3 = new Boolean(true);
        PropEquivalenceClassWithBeeDeeDee w1 = new PropEquivalenceClassWithBeeDeeDee(f1);
        PropEquivalenceClassWithBeeDeeDee w2 = new PropEquivalenceClassWithBeeDeeDee(f2);
        PropEquivalenceClassWithBeeDeeDee w3 = new PropEquivalenceClassWithBeeDeeDee(f3);

        // true = true (different objects)
        assertTrue(w1.equals(w3));
        // true != false
        assertFalse(w1.equals(w2));

        // true || false || true == true || true || true
        Formula or = new Or(Arrays.asList(new Boolean(true), new Boolean(false), new Boolean(true)));
        Formula or2 = new Or(Arrays.asList(new Boolean(true), new Boolean(true), new Boolean(true)));
        assertTrue(new PropEquivalenceClassWithBeeDeeDee(or).equals(w1));
    }

    @Test
    public void variablePropositionalEquivalenceTest() {
        Variable a = new Variable("a");
        Variable notA = new Variable("a", true);
        PropEquivalenceClassWithBeeDeeDee w = new PropEquivalenceClassWithBeeDeeDee(a);
        PropEquivalenceClassWithBeeDeeDee notW = new PropEquivalenceClassWithBeeDeeDee(notA);

        // a = a (different objects)
        assertTrue(w.equals(equivalenceClasses.get(0)));
        // a != !a
        assertFalse(notW.equals(equivalenceClasses.get(0)));
        // a != b
        assertFalse(w.equals(equivalenceClasses.get(1)));
    }

    @Test
    public void operatorPropositionalEquivalenceTest() {
        List<Function<Formula,Formula>> gfxOperatorConstructors = Arrays.asList(
                F::new,
                G::new,
                X::new
        );
        gfxOperatorConstructors.forEach(constructor -> {
            PropEquivalenceClassWithBeeDeeDee wa = new PropEquivalenceClassWithBeeDeeDee(constructor.apply(variables.get(0)));
            PropEquivalenceClassWithBeeDeeDee wa2 = new PropEquivalenceClassWithBeeDeeDee(constructor.apply(variables.get(0)));
            PropEquivalenceClassWithBeeDeeDee wb = new PropEquivalenceClassWithBeeDeeDee(constructor.apply(variables.get(1)));

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
        PropEquivalenceClassWithBeeDeeDee w1 = new PropEquivalenceClassWithBeeDeeDee(new U(variables.get(0), variables.get(1)));
        PropEquivalenceClassWithBeeDeeDee w2 = new PropEquivalenceClassWithBeeDeeDee(new U(variables.get(0), variables.get(1)));
        PropEquivalenceClassWithBeeDeeDee w3 = new PropEquivalenceClassWithBeeDeeDee(new U(variables.get(1), variables.get(0)));

        // a U b = a U b
        assertTrue(w1.equals(w2));
        // a U b != b U a
        assertFalse(w1.equals(w3));
    }

    @Test
    public void andPropositionalEquivalenceTest() {
        PropEquivalenceClassWithBeeDeeDee c1 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), variables.get(1))); // a & b
        PropEquivalenceClassWithBeeDeeDee c2 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(1), variables.get(0))); // b & a
        ArrayList<Formula> l1 = new ArrayList<>(Arrays.asList(variables.get(0), variables.get(1), variables.get(2)));
        PropEquivalenceClassWithBeeDeeDee c3 = new PropEquivalenceClassWithBeeDeeDee(new And(l1)); // a & b & c
        PropEquivalenceClassWithBeeDeeDee c4 = new PropEquivalenceClassWithBeeDeeDee(new And(new And(variables.get(0), variables.get(1)), variables.get(2))); // (a & b) & c
        PropEquivalenceClassWithBeeDeeDee c5 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), new And(variables.get(1), variables.get(2)))); // a & (b & c)
        PropEquivalenceClassWithBeeDeeDee c6 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), variables.get(2))); // a & c

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
        PropEquivalenceClassWithBeeDeeDee c1 = new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(1))); // a | b
        PropEquivalenceClassWithBeeDeeDee c2 = new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(1), variables.get(0))); // b | a
        ArrayList<Formula> l1 = new ArrayList<>(Arrays.asList(variables.get(0), variables.get(1), variables.get(2)));
        PropEquivalenceClassWithBeeDeeDee c3 = new PropEquivalenceClassWithBeeDeeDee(new Or(l1)); // a | b | c
        PropEquivalenceClassWithBeeDeeDee c4 = new PropEquivalenceClassWithBeeDeeDee(new Or(new Or(variables.get(0), variables.get(1)), variables.get(2))); // (a | b) | c
        PropEquivalenceClassWithBeeDeeDee c5 = new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), new Or(variables.get(1), variables.get(2)))); // a | (b | c)
        PropEquivalenceClassWithBeeDeeDee c6 = new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(2))); // a | c

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

        Formula bORc = new Or(variables.get(1), variables.get(2)); // b | c
        PropEquivalenceClassWithBeeDeeDee c1 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), bORc)); // a & (b | c)
        Formula aANDb = new And(variables.get(0), variables.get(1)); // a & b
        Formula aANDc = new And(variables.get(0), variables.get(2)); // a & c
        PropEquivalenceClassWithBeeDeeDee c2 = new PropEquivalenceClassWithBeeDeeDee(new Or(aANDb, aANDc));
        // a & (b | c) = (a & b) | (a & c)
        assertTrue(c1.equals(c2));

        PropEquivalenceClassWithBeeDeeDee c3 = new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), new Boolean(false))); // a | false
        PropEquivalenceClassWithBeeDeeDee c4 = new PropEquivalenceClassWithBeeDeeDee(variables.get(0)); // a
        // a | false = a
        assertTrue(c3.equals(c4));
        // a & true = a
        PropEquivalenceClassWithBeeDeeDee c5 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), new Boolean(true))); // a & true
        assertTrue(c4.equals(c5));

        PropEquivalenceClassWithBeeDeeDee c6 = new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(1), bORc)); // b & (b | c)
        // b & (b | c) = b
        assertTrue(c6.equals(new PropEquivalenceClassWithBeeDeeDee(variables.get(1))));
    }

    @Test
    public void implicationTest() {
        // a & b |= a
        assertTrue(new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(0)));
        // a & b |= b
        assertTrue(new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(1)));

        // a | b |/= a
        assertFalse(new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(0)));
        // a | b |/= b
        assertFalse(new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(1))).implies(equivalenceClasses.get(1)));
        // a |= a | b
        assertTrue(equivalenceClasses.get(1).implies(new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(1)))));

        // a & b |= a | b
        assertTrue(new PropEquivalenceClassWithBeeDeeDee(new And(variables.get(0), variables.get(1))).implies(
                new PropEquivalenceClassWithBeeDeeDee(new Or(variables.get(0), variables.get(1)))));

        // tt |/= a
        assertFalse(new PropEquivalenceClassWithBeeDeeDee(tt).implies(equivalenceClasses.get(0)));
        // a |= tt
        assertTrue(equivalenceClasses.get(0).implies(new PropEquivalenceClassWithBeeDeeDee(tt)));
        // tt |= tt
        assertTrue(new PropEquivalenceClassWithBeeDeeDee(tt).implies(new PropEquivalenceClassWithBeeDeeDee(tt)));
        // tt |/= ff
        assertFalse(new PropEquivalenceClassWithBeeDeeDee(tt).implies(new PropEquivalenceClassWithBeeDeeDee(ff)));

        // a |/= ff
        assertFalse(new PropEquivalenceClassWithBeeDeeDee(variables.get(0)).implies(new PropEquivalenceClassWithBeeDeeDee(ff)));
    }
}