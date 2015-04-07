package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EquivalenceOfLTLsTest {
    List<LTLVariable> variables;
    int variableCount = 3; // should not surpass 26. Otherwise change variable generation in the setUp() method.

    private LTLFormula get(int i) {
        return variables.get(i);
    }
    @Before
    public void setUp () {
        variables = new ArrayList<>();
        // generate necessary variables
        for (int i = 0; i < variableCount; i++) {
            variables.add(new LTLVariable(Character.toString((char) ('a' + i))));
        }
    }

    @After
    public void tearDown() {
        variables.clear();
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

    @Test
    public void andOrStructuralEquivalenceTest() {
        List<Function<List<LTLFormula>, LTLFormula>> constructorsAndOr = Arrays.asList(
                LTLAnd::new,
                LTLOr::new
            );
        for (Function<List<LTLFormula>, LTLFormula> constructor : constructorsAndOr) {
            // a & b = a & b
            ArrayList<LTLFormula> l1 = new ArrayList<>(Arrays.asList(get(0), get(1)));
            LTLFormula f1 = constructor.apply(l1);
            LTLFormula f2 = constructor.apply(l1);
            assertTrue(f2.equals(f1));

            // a & b != b & a
            l1 = new ArrayList<>(Arrays.asList(get(1), get(0)));
            f2 = constructor.apply(l1);
            assertFalse(f1.equals(f2));

            // a & b & c != a & c
            l1 = new ArrayList<>(Arrays.asList(get(0), get(1), get(2)));
            ArrayList<LTLFormula> l2 = new ArrayList<>(Arrays.asList(get(0), get(2)));
            f1 = constructor.apply(l1);
            f2 = constructor.apply(l2);
            assertFalse(f1.equals(f2));

            // a & b & c = a & b & c
            f2 = constructor.apply(l1);
            assertTrue(f1.equals(f2));

            // a & (a & b) = a & (a & b)
            l1 = new ArrayList<>(Arrays.asList(get(0), get(1)));
            f1 = constructor.apply(l1);
            l2 = new ArrayList<>(Arrays.asList(get(0), f1));
            f2 = constructor.apply(l2);
            ArrayList<LTLFormula> l3 = new ArrayList<>(Arrays.asList(get(0), get(1)));
            LTLFormula f3 = constructor.apply(l3);
            ArrayList<LTLFormula> l4 = new ArrayList<>(Arrays.asList(get(0), f3));
            LTLFormula f4 = constructor.apply(l4);
            assertTrue(f2.equals(f4));

            // a = a
            l1 = new ArrayList<>(Collections.singletonList(get(0)));
            f1 = constructor.apply(l1);
            l2 = new ArrayList<>(Collections.singletonList(get(0)));
            f2 = constructor.apply(l2);
            assertTrue(f1.equals(f2));
        }
    }
}