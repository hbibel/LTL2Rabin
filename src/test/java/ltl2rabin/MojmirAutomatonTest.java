package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MojmirAutomatonTest {
    MojmirAutomatonFactoryFromString automatonFactory;

    @Before
    public void setUp() {
        automatonFactory = new MojmirAutomatonFactoryFromString();
    }

    @Test
    public void testCase1() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | (b U c)", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase2() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a | b | c", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase3() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("X b", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(4, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase4() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("F b", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(2, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase5() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a & b & c", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase6() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a U b", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(3, mojmirAutomaton.getStates().size());
    }

    @Test
    public void testCase7() throws Exception {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> mojmirAutomaton = automatonFactory.createFrom("a", ImmutableSet.copyOf(Collections.<Set<String>>emptySet()));
        assertEquals(3, mojmirAutomaton.getStates().size());
    }


}