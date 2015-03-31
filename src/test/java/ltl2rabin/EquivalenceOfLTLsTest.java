package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        assertTrue(w1.equals(w3));
        assertFalse(w1.equals(w2));
    }

    @Test
    public void variablePropositionalEquivalenceTest() {
        LTLVariable a = new LTLVariable("a");
        LTLVariable notA = new LTLVariable("a", true);
        LTLPropositionalEquivalenceWrapper w = new LTLPropositionalEquivalenceWrapper(a);
        LTLPropositionalEquivalenceWrapper notW = new LTLPropositionalEquivalenceWrapper(notA);

        assertTrue(w.equals(wrappers.get(0)));
        assertFalse(notW.equals(wrappers.get(0)));
        assertFalse(w.equals(wrappers.get(1)));
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