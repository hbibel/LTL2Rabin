package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MojmirAutomatonFactoryFromLTL extends MojmirAutomatonFactory<LTLFormula> {

    public MojmirAutomatonFactoryFromLTL(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(LTLFormula from) {
        // TODO: Use Builders for immutable collections
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(from, Collections.<LTLFormula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(from);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = super.reach(initialState, super.getAlphabet(), Collections.<LTLFormula>emptySet());
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        cachedResult = new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), super.getAlphabet());
        putIntoCache(new Pair<>(from, Collections.<LTLFormula>emptySet()), cachedResult);
        return cachedResult;
    }
}
