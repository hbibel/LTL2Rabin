package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EquivalenceOfLTLsTest {
    List<LTLVariable> variables;
    List<LTLPropositionalEquivalenceWrapper> wrappers;
    int variableCount = 2; // should not surpass 26. Otherwise change variable generation in the setUp() method.

    @Before
    public void setUp () {
        variables = new ArrayList<>();
        wrappers = new ArrayList<>();
        // generate necessary variables
        for (int i = 0; i < variableCount; i++) {
            variables.add(new LTLVariable(Character.toString((char) ('a' + i))));
            wrappers.add(new LTLPropositionalEquivalenceWrapper(variables.get(i)));
        }
    }

    @After
    public void tearDown() {
        variables.clear();
        wrappers.clear();
    }

    @Test
    public void booleanPropositionalEquivalenceTest() {
        LTLFormula f1 = new LTLBoolean(true);
        LTLFormula f2 = new LTLBoolean(false);
        LTLFormula f3 = new LTLBoolean(true);
        LTLPropositionalEquivalenceWrapper w1 = new LTLPropositionalEquivalenceWrapper(f1);
        LTLPropositionalEquivalenceWrapper w2 = new LTLPropositionalEquivalenceWrapper(f2);
        LTLPropositionalEquivalenceWrapper w3 = new LTLPropositionalEquivalenceWrapper(f3);

        // true = true (different objects)
        assertTrue(w1.equals(w3));
        // true != false
        assertFalse(w1.equals(w2));
    }

    @Test
    public void variablePropositionalEquivalenceTest() {
        LTLVariable a = new LTLVariable("a");
        LTLVariable notA = new LTLVariable("a", true);
        LTLPropositionalEquivalenceWrapper w = new LTLPropositionalEquivalenceWrapper(a);
        LTLPropositionalEquivalenceWrapper notW = new LTLPropositionalEquivalenceWrapper(notA);

        // a = a (different objects)
        assertTrue(w.equals(wrappers.get(0)));
        // a != !a
        assertFalse(notW.equals(wrappers.get(0)));
        // a != b
        assertFalse(w.equals(wrappers.get(1)));
    }

    @Test
    public void operatorPropositionalEquivalenceTest() {
        List<Function<LTLFormula,LTLFormula> > gfxOperatorConstructors = Arrays.asList(
                LTLFOperator::new,
                LTLGOperator::new,
                LTLXOperator::new
        );
        gfxOperatorConstructors.forEach(constructor -> {
            LTLPropositionalEquivalenceWrapper wa = new LTLPropositionalEquivalenceWrapper(constructor.apply(variables.get(0)));
            LTLPropositionalEquivalenceWrapper wa2 = new LTLPropositionalEquivalenceWrapper(constructor.apply(variables.get(0)));
            LTLPropositionalEquivalenceWrapper wb = new LTLPropositionalEquivalenceWrapper(constructor.apply(variables.get(1)));

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
        LTLPropositionalEquivalenceWrapper w1 = new LTLPropositionalEquivalenceWrapper(new LTLUOperator(variables.get(0), variables.get(1)));
        LTLPropositionalEquivalenceWrapper w2 = new LTLPropositionalEquivalenceWrapper(new LTLUOperator(variables.get(0), variables.get(1)));
        LTLPropositionalEquivalenceWrapper w3 = new LTLPropositionalEquivalenceWrapper(new LTLUOperator(variables.get(1), variables.get(0)));

        // a U b = a U b
        assertTrue(w1.equals(w2));
        // a U b != b U a
        assertFalse(w1.equals(w3));
    }

    @Test
    public void andPropositionalEquivalenceTest() {

    }

    /********************************
     * STRUCTURAL EQUIVALENCE TESTS *
     ********************************/

    @Test
    public void booleanStructuralEquivalenceTest() {
        LTLFormula f1 = new LTLBoolean(true);
        LTLFormula f2 = new LTLBoolean(false);
        LTLFormula f3 = new LTLBoolean(true);

        // true = true (different objects)
        assertTrue(f1.equals(f3));
        // true != false
        assertFalse(f1.equals(f2));
    }

    @Test
    public void variableStructuralEquivalenceTest() {
        LTLVariable a = new LTLVariable("a");
        LTLVariable notA = new LTLVariable("a", true);

        // a = a (different objects)
        assertTrue(a.equals(variables.get(0)));
        // a != !a
        assertFalse(notA.equals(variables.get(0)));
        // a != b
        assertFalse(a.equals(variables.get(1)));
    }

    @Test
    public void operatorStructuralEquivalenceTest() {
        List<Function<LTLFormula,LTLFormula> > gfxOperatorConstructors = Arrays.asList(
                LTLFOperator::new,
                LTLGOperator::new,
                LTLXOperator::new
        );
        gfxOperatorConstructors.forEach(constructor -> {
            LTLFormula fa = constructor.apply(variables.get(0));
            LTLFormula fa2 = constructor.apply(variables.get(0));
            LTLFormula fb = constructor.apply(variables.get(1));

            // G a = G a (different objects)
            assertTrue(fa.equals(fa2));
            // G a = G a (same object)
            assertTrue(fa.equals(fa));
            // G a != G b
            assertFalse(fa.equals(fb));
        });
    }

    @Test
    public void uOperatorStructuralEquivalenceTest() {
        LTLUOperator w1 = new LTLUOperator(variables.get(0), variables.get(1));
        LTLUOperator w2 = new LTLUOperator(variables.get(0), variables.get(1));
        LTLUOperator w3 = new LTLUOperator(variables.get(1), variables.get(0));

        // a U b = a U b
        assertTrue(w1.equals(w2));
        // a U b != b U a
        assertFalse(w1.equals(w3));
    }
/*
    @Test
    public void testCase2() {
        LTLFormula f1 = new LTLFOperator(new LTLVariable("a"));
        LTLFormula f2 = new LTLFOperator(new LTLVariable("a"));
        LTLFormula f3 = new LTLFOperator(new LTLVariable("b"));
        LTLFormula a1 = new LTLAnd(f1, f3);
        LTLFormula a2 = new LTLAnd(f3, f1);
        LTLFormula o1 = new LTLOr(f1, f3);
        LTLFormula o2 = new LTLOr(f3, f1);
        LTLFormula v1 = new LTLVariable("a");
        LTLFormula b1 = new LTLBoolean(true);
        LTLFormula a3 = new LTLAnd(v1, b1);
        LTLFormula o3 = new LTLOr(v1, b1);
        LTLFormula v2 = new LTLVariable("b");
        LTLFormula v3 = new LTLVariable("c");
        ArrayList<LTLFormula> aArgs1 = new ArrayList<>();
        aArgs1.add(v1); aArgs1.add(v2); aArgs1.add(v3);
        ArrayList<LTLFormula> aArgs2 = new ArrayList<>();
        aArgs2.add(v3); aArgs2.add(v1); aArgs2.add(v2);
        LTLFormula a4 = new LTLAnd(aArgs1);
        LTLFormula a5 = new LTLAnd(aArgs2);

        // F a = F a (different objects)
        assertTrue(f1.propositionallyEquivalent(f2));
        // F a != F b
        // assertFalse(f1.propositionallyEquivalent(f3));
        // (F a) & (F b) = (F b) & (F a)
        assertTrue(a1.propositionallyEquivalent(a2));
        // (F a) | (F b) = (F b) | (F a)
        assertTrue(o1.propositionallyEquivalent(o2));
        // "a" & tt = "a"
        assertTrue(a3.propositionallyEquivalent(v1));
        // "a" | tt = tt
        assertTrue(o3.propositionallyEquivalent(b1));
        // "a" | tt != "a"
        assertFalse(v1.propositionallyEquivalent(o3));
        // "a" & "b" & "c" = "c" & "a" & "b"
        assertTrue(a4.propositionallyEquivalent(a5));
        // (F (X a)) & b = (F
    }

    @Test
    public void testCase3() {
        LTLVariable v1 = new LTLVariable("a");
        LTLVariable v2 = new LTLVariable("b");
        LTLAnd a1 = new LTLAnd(v1, v2);
        LTLAnd a2 = new LTLAnd(v2, v1);

        assertTrue(a1.propositionallyEquivalent(a2));
    }*/
}