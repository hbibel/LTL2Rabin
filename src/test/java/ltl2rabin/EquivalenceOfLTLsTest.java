package ltl2rabin;

import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
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
    List<Variable> variables;
    int variableCount = 3; // should not surpass 26. Otherwise change variable generation in the setUp() method.

    private Formula get(int i) {
        return variables.get(i);
    }
    @Before
    public void setUp () {
        variables = new ArrayList<>();
        // generate necessary variables
        for (int i = 0; i < variableCount; i++) {
            variables.add(new Variable(Character.toString((char) ('a' + i))));
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
        Formula f1 = new Boolean(true);
        Formula f2 = new Boolean(false);
        Formula f3 = new Boolean(true);

        // true = true (different objects)
        assertTrue(f1.equals(f3));
        // true != false
        assertFalse(f1.equals(f2));
    }

    @Test
    public void variableStructuralEquivalenceTest() {
        Variable a = new Variable("a");
        Variable notA = new Variable("a", true);

        // a = a (different objects)
        assertTrue(a.equals(variables.get(0)));
        // a != !a
        assertFalse(notA.equals(variables.get(0)));
        // a != b
        assertFalse(a.equals(variables.get(1)));
    }

    @Test
    public void operatorStructuralEquivalenceTest() {
        List<Function<Formula,Formula> > gfxOperatorConstructors = Arrays.asList(
                F::new,
                G::new,
                X::new
        );
        gfxOperatorConstructors.forEach(constructor -> {
            Formula fa = constructor.apply(variables.get(0));
            Formula fa2 = constructor.apply(variables.get(0));
            Formula fb = constructor.apply(variables.get(1));

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
        U w1 = new U(variables.get(0), variables.get(1));
        U w2 = new U(variables.get(0), variables.get(1));
        U w3 = new U(variables.get(1), variables.get(0));

        // a U b = a U b
        assertTrue(w1.equals(w2));
        // a U b != b U a
        assertFalse(w1.equals(w3));
    }

    @Test
    public void andOrStructuralEquivalenceTest() {
        List<Function<List<Formula>, Formula>> constructorsAndOr = Arrays.asList(
                And::new,
                Or::new
            );
        for (Function<List<Formula>, Formula> constructor : constructorsAndOr) {
            // a & b = a & b
            ArrayList<Formula> l1 = new ArrayList<>(Arrays.asList(get(0), get(1)));
            Formula f1 = constructor.apply(l1);
            Formula f2 = constructor.apply(l1);
            assertTrue(f2.equals(f1));

            // a & b != b & a
            l1 = new ArrayList<>(Arrays.asList(get(1), get(0)));
            f2 = constructor.apply(l1);
            assertFalse(f1.equals(f2));

            // a & b & c != a & c
            l1 = new ArrayList<>(Arrays.asList(get(0), get(1), get(2)));
            ArrayList<Formula> l2 = new ArrayList<>(Arrays.asList(get(0), get(2)));
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
            ArrayList<Formula> l3 = new ArrayList<>(Arrays.asList(get(0), get(1)));
            Formula f3 = constructor.apply(l3);
            ArrayList<Formula> l4 = new ArrayList<>(Arrays.asList(get(0), f3));
            Formula f4 = constructor.apply(l4);
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