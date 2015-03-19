package ltl2rabin;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EquivalenceOfLTLsTest {
    @Test
    public void testCase1() {
        LTLFormula f1 = new LTLBoolean(true);
        LTLFormula f2 = new LTLBoolean(false);
        LTLFormula f3 = new LTLBoolean(true);

        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(f1, f3));
        assertFalse(EquivalenceOfLTLs.arePropositionallyEquivalent(f1, f2));
    }

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
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(f1, f2));
        // F a != F b
        assertFalse(EquivalenceOfLTLs.arePropositionallyEquivalent(f1, f3));
        // (F a) & (F b) = (F b) & (F a)
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(a1, a2));
        // (F a) | (F b) = (F b) | (F a)
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(o1, o2));
        // "a" & tt = "a"
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(a3, v1));
        // "a" | tt = tt
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(o3, b1));
        // "a" | tt != "a"
        assertFalse(EquivalenceOfLTLs.arePropositionallyEquivalent(v1, o3));
        // "a" & "b" & "c" = "c" & "a" & "b"
        assertTrue(EquivalenceOfLTLs.arePropositionallyEquivalent(a4, a5));
    }

    @Test
    public void testCase3() {
        LTLVariable v1 = new LTLVariable("a");
        LTLVariable v2 = new LTLVariable("b");
        LTLAnd a1 = new LTLAnd(v1, v2);
        LTLAnd a2 = new LTLAnd(v2, v1);

        assertTrue(a1.equals(a2));
    }
}