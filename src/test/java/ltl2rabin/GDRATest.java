package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GDRATest {

    @Test
    public void basicTest() {
        GDRA gdra = new GDRAFactory().createFrom("a");
        assertEquals(3, gdra.getStates().size());
//        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> mPiPsi = (new ArrayList<>(gdra.getGdraCondition())).get(0);
//        assertEquals(4, mPiPsi.size());
    }

    @Test
    public void simpleGTest() {
        GDRA gdra = new GDRAFactory().createFrom("a & (G b)");
        assertEquals(3, gdra.getStates().size());
    }

    @Test
    public void paperExample() {
        /* The big example from the paper */
        String input = "b | (X G (a | (X (b U c))))";
        Formula psi = new Or(new Variable("a"), new X(new U(new Variable("b"), new Variable("c"))));
        GDRA gdra = new GDRAFactory().createFrom(input);
        assertEquals(8, gdra.getStates().size());

        ImmutableSet<? extends Set<String>> alphabet = gdra.getAlphabet();
        final Set<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> gdraCondition = gdra.getGdraCondition();
        Set<GDRA.Transition> univ = new HashSet<>();
        gdra.getStates().forEach(q -> {
            alphabet.forEach(l -> {
                univ.add(new GDRA.Transition((GDRA.State) q, l, (GDRA.State) q.readLetter(l)));
            });
        });

        Set<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> expectedGDRACondition = new HashSet<>();
        // M^{}_()
        Set<GDRA.Transition> m0Avoid = new HashSet<>();
        gdra.getStates().forEach(q -> {
            if (!q.getLabel().getFirst().equals(new PropEquivalenceClass(new Boolean(true)))) {
                alphabet.forEach(l -> {
                    m0Avoid.add(new GDRA.Transition((GDRA.State) q, l, (GDRA.State) q.readLetter(l)));
                });
            }
        });
        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> expectedM0 = new Pair<>(m0Avoid, univ);
        // Acc^{}_()
        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> expectedAcc0 = ImmutableSet.of(expectedM0);
        expectedGDRACondition.add(expectedAcc0);
        assertTrue(gdraCondition.contains(expectedAcc0)); // TODO: Remove when expectedGDRACondition is finished

        // M^{psi}_(psi->0)

    }
}
