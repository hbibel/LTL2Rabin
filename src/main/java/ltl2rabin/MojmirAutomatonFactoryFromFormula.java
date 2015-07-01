package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.*;

public class MojmirAutomatonFactoryFromFormula extends MojmirAutomatonFactory<Formula> {

    public MojmirAutomatonFactoryFromFormula(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<PropEquivalenceClass, Set<String>> createFrom(Formula from) {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> cachedResult = getFromCache(from);
        if (null != cachedResult) {
            return cachedResult;
        }

        PropEquivalenceClass initialLabel = new PropEquivalenceClass(from);
        MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>reachResult = super.reach(initialState, super.getAlphabet(), Collections.<Formula>emptySet());
        ImmutableSet<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult);

        cachedResult = new MojmirAutomaton<>(states, initialState, super.getAlphabet());
        putIntoCache(from, cachedResult);
        return cachedResult;
    }
}
