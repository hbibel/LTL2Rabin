package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.LTLPropEquivalenceClass;

import java.util.*;

public class MojmirAutomatonFactoryFromLTL extends MojmirAutomatonFactory<Formula> {

    public MojmirAutomatonFactoryFromLTL(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(Formula from) {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(from, Collections.<Formula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(from);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = super.reach(initialState, super.getAlphabet(), Collections.<Formula>emptySet());
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        cachedResult = new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), super.getAlphabet());
        putIntoCache(new Pair<>(from, Collections.<Formula>emptySet()), cachedResult);
        return cachedResult;
    }
}
