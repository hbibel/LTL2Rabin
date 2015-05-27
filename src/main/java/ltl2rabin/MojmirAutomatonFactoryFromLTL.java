package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MojmirAutomatonFactoryFromLTL extends MojmirAutomatonFactory<LTLFormula> {

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(LTLFormula from, ImmutableSet<Set<String>> alphabet) {
        // TODO: Use Builders for immutable collections
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(from, Collections.<LTLFormula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(from);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = super.reach(initialState, alphabet, Collections.<LTLFormula>emptySet());
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        cachedResult = new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), alphabet);
        putIntoCache(new Pair<>(from, Collections.<LTLFormula>emptySet()), cachedResult);
        return cachedResult;
    }
}
