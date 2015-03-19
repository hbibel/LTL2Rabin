package ltl2rabin;

import org.junit.Test;

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
    }
}