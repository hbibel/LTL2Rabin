package ltl2rabin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GDRATest {

    @Test
    public void basicTest() {
        GDRA gdra = new GDRAFactory().createFrom("a");
        assertEquals(3, gdra.getStates().size());
        Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>> mPiPsi = (new ArrayList<>(gdra.getGdraCondition())).get(0);
        assertEquals(4, mPiPsi.size());
    }

    @Test
    public void simpleGTest() {
        GDRA gdra = new GDRAFactory().createFrom("a & (G b)");
        assertEquals(3, gdra.getStates().size());
    }

    private Pair<Set<GDRA.Transition>, Set<GDRA.Transition>> flatten (Set<Set<Pair<Set<GDRA.Transition>, Set<GDRA.Transition>>>> acc) {
        Set<GDRA.Transition> avoid = new HashSet<>();
        Set<GDRA.Transition> reach = new HashSet<>();

        acc.forEach(setOfPairs -> {
            setOfPairs.forEach(pairOfSets -> {
                avoid.addAll(pairOfSets.getFirst());
                reach.addAll(pairOfSets.getSecond());
            });
        });

        return new Pair<>(avoid, reach);
    }
}
