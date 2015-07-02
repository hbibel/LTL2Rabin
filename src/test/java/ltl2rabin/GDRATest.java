package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.*;
import ltl2rabin.LTL.Boolean;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GDRATest {

    @Test
    public void basicTest() {
        final String input = "a";
        LTLFactory<String> ltlFactoryFromString = new LTLFactoryFromString();
        final LTLFactory.Result parserResult = ltlFactoryFromString.buildLTL(input);
        GDRA gdra = new GDRAFactory(parserResult.getAlphabet()).createFrom(parserResult);
        assertEquals(3, gdra.getStates().size());
//        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> mPiPsi = (new ArrayList<>(gdra.getGdraCondition())).get(0);
//        assertEquals(4, mPiPsi.size());
    }

    @Test
    public void simpleGTest() {
        LTLFactory<String> ltlFactoryFromString = new LTLFactoryFromString();
        final String input = "a & (G b)";
        final LTLFactory.Result parserResult = ltlFactoryFromString.buildLTL(input);
        GDRA gdra = new GDRAFactory(parserResult.getAlphabet()).createFrom(parserResult);
        assertEquals(3, gdra.getStates().size());
    }

    @Test
    public void paperExample() {
        /* The big example from the paper */
        String input = "b | (X G (a | (X (b U c))))";
        Formula psi = new Or(new Variable("a"), new X(new U(new Variable("b"), new Variable("c"))));
        Formula phi = new LTLFactoryFromString().buildLTL(input).getLtlFormula();

        LTLFactory<String> ltlFactoryFromString = new LTLFactoryFromString();

        final LTLFactory.Result parserResult = ltlFactoryFromString.buildLTL(input);
        GDRA gdra = new GDRAFactory(ImmutableSet.of()).createFrom(parserResult);
        assertEquals(8, gdra.getStates().size());

        ImmutableSet<? extends Set<String>> alphabet = gdra.getAlphabet();
        final Set<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> gdraCondition = gdra.getGdraCondition();
        Set<GDRA.Transition> univ = new HashSet<>();
        gdra.getStates().forEach(q -> {
            alphabet.forEach(l -> {
                univ.add(new GDRA.Transition((GDRA.State) q, l, (GDRA.State) q.readLetter(l)));
            });
        });

        // states:
        GDRA.State q0sr0 = gdra.getInitialState();
        GDRA.State q1sr0 = q0sr0.readLetter(ImmutableSet.of("a", "b"));
        GDRA.State q1sr1 = q0sr0.readLetter(ImmutableSet.of("b", "c"));
        GDRA.State q2sr0 = q0sr0.readLetter(ImmutableSet.of("a"));
        GDRA.State q2sr1 = q0sr0.readLetter(Collections.emptySet());
        GDRA.State q3sr1 = q2sr0.readLetter(Collections.emptySet());
        GDRA.State q4sr0 = q3sr1.readLetter(ImmutableSet.of("a"));
        GDRA.State q4sr1 = q3sr1.readLetter(Collections.emptySet());

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
        Set<GDRA.Transition> mPsi0Avoid = new HashSet<>();
        gdra.getStates().forEach(q -> {
            if (q.getLabel().getFirst().equals(new PropEquivalenceClass(phi))
                    || q.getLabel().getFirst().equals(new PropEquivalenceClass(new Boolean(false)))) { // q = q0 or q4
                alphabet.forEach(l -> {
                    mPsi0Avoid.add(new GDRA.Transition((GDRA.State) q, l, (GDRA.State) q.readLetter(l)));
                });
            }
        });
        assertEquals(24, mPsi0Avoid.size());
        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> expectedMPsi0 = new Pair<>(mPsi0Avoid, univ);
        // Acc^{psi}_(psi->0) (psi)
        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> expectedAccPsi0Psi = new Pair<>(new HashSet<>(), new HashSet<>());
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q2sr1, Collections.emptySet(), q3sr1));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q1sr1, Collections.emptySet(), q1sr1));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q3sr1, Collections.emptySet(), q4sr1));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q4sr1, Collections.emptySet(), q4sr1));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q2sr1, ImmutableSet.of("a"), q2sr0));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q1sr1, ImmutableSet.of("a"), q1sr0));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q3sr1, ImmutableSet.of("a"), q4sr0));
        expectedAccPsi0Psi.getFirst().add(new GDRA.Transition(q4sr1, ImmutableSet.of("a"), q4sr0));
        gdra.getStates().forEach(state -> {
            if (state.equals(q0sr0) || state.equals(q2sr0) || state.equals(q1sr0) || state.equals(q4sr0)) {
                alphabet.forEach(letter -> {
                    if (letter.contains("a")) { // t1
                        expectedAccPsi0Psi.getSecond().add(new GDRA.Transition((GDRA.State) state, letter, (GDRA.State) state.readLetter(letter)));
                    }
                });
            }
            if (state.equals(q1sr1) || state.equals(q2sr1) || state.equals(q3sr1) || state.equals(q4sr1)) {
                alphabet.forEach(letter -> {
                    if (letter.contains("c")) { // t5 + t7
                        expectedAccPsi0Psi.getSecond().add(new GDRA.Transition((GDRA.State) state, letter, (GDRA.State) state.readLetter(letter)));
                    }
                });
            }
        });
        // Acc^{psi}_(psi->0)
        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> expectedAccPsi0 = ImmutableSet.of(expectedMPsi0, expectedAccPsi0Psi);
        expectedGDRACondition.add(expectedAccPsi0);

        // M^{psi}_(psi->1)
        Set<GDRA.Transition> mPsi1Avoid = new HashSet<>();
        gdra.getStates().forEach(q -> {
            if (!(q.getLabel().getFirst().equals(new PropEquivalenceClass(new Boolean(true)))
                    || q.getLabel().getFirst().equals(new PropEquivalenceClass(new G(psi))))) { // qi != q1 or q2
                alphabet.forEach(l -> {
                    mPsi1Avoid.add(new GDRA.Transition((GDRA.State) q, l, (GDRA.State) q.readLetter(l)));
                });
            }
        });
        assertEquals(32, mPsi1Avoid.size());
        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> expectedMPsi1 = new Pair<>(mPsi1Avoid, univ);

        // Acc^{psi}_(psi->1) (psi)
        Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> expectedAccPsi1Psi = new Pair<>(new HashSet<>(), new HashSet<>());
        gdra.getStates().forEach(state -> {
            if (state.equals(q1sr1) || state.equals(q2sr1) || state.equals(q3sr1) || state.equals(q4sr1)) {
                alphabet.forEach(letter -> {
                    if (letter.isEmpty() || letter.equals(ImmutableSet.of("b")) || letter.equals(ImmutableSet.of("a"))) { // t3, t6, t8
                        expectedAccPsi1Psi.getFirst().add(new GDRA.Transition((GDRA.State) state, letter, (GDRA.State) state.readLetter(letter)));
                    }
                });
            }
        });
        gdra.getStates().forEach(state -> {
            if (state.equals(q1sr1) || state.equals(q2sr1) || state.equals(q3sr1) || state.equals(q4sr1)) {
                alphabet.forEach(letter -> {
                    if (letter.equals(ImmutableSet.of("a", "b")) || (letter.contains("a") && letter.contains("c")) || letter.equals(ImmutableSet.of("a"))) { // t4, t7, t8
                        expectedAccPsi1Psi.getSecond().add(new GDRA.Transition((GDRA.State) state, letter, (GDRA.State) state.readLetter(letter)));
                    }
                });
            }
        });
        assertEquals(12, expectedAccPsi1Psi.getFirst().size());
        assertEquals(16, expectedAccPsi1Psi.getSecond().size());

        // Acc^{psi}_(psi->1)
        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> expectedAccPsi1 = ImmutableSet.of(expectedMPsi1, expectedAccPsi1Psi);
        expectedGDRACondition.add(expectedAccPsi1);

        java.lang.Boolean checkedPairs[] = new java.lang.Boolean[5];
        for (int i = 0; i < 5; i++) {
            checkedPairs[i] = false;
        }
        gdraCondition.forEach(pairs -> {
            if (1 == pairs.size()) {
                // M^{}_()
                pairs.forEach(m0 -> {
                    assertEquals(expectedM0, m0);
                    checkedPairs[0] = true;
                });
            }
            else {
                // pairs is either Acc^psi_0 or Acc^psi_1
                boolean isAccPsi1 = false;
                for (Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accPair : pairs) {
                    isAccPsi1 = isAccPsi1 || ((accPair.getFirst().size() == 32) && (accPair.getSecond().size() == 64));
                    if ((accPair.getFirst().size() == 32) && (accPair.getSecond().size() == 64)) {
                        assertEquals(expectedMPsi1, accPair);
                        checkedPairs[1] = true;
                    }
                    if ((accPair.getFirst().size() == 24) && (accPair.getSecond().size() == 64)) {
                        assertEquals(expectedMPsi0, accPair);
                        checkedPairs[2] = true;
                    }
                }
                if (isAccPsi1) {
                    // pairs is Acc^psi_1
                    for (Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accPair : pairs) {
                        if (!((accPair.getFirst().size() == 32) && (accPair.getSecond().size() == 64))) {
                            // Acc^{psi}_(psi->1) (psi)
                            assertEquals(expectedAccPsi1Psi, accPair);
                            checkedPairs[3] = true;
                        }
                    }
                }
                else {
                    // pairs is Acc^psi_0
                    for (Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> accPair : pairs) {
                        if (!((accPair.getFirst().size() == 24) && (accPair.getSecond().size() == 64))) {
                            // Acc^{psi}_(psi->0) (psi)
                            assertEquals(expectedAccPsi0Psi, accPair);
                            checkedPairs[4] = true;
                        }
                    }
                }
            }
        });

        for (java.lang.Boolean isPairChecked : checkedPairs) {
            assertTrue(isPairChecked);
        }
    }
}