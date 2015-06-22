package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClassWithBeeDeeDee;

import java.util.*;

public class MojmirAutomatonFactoryFromLTL extends MojmirAutomatonFactory<Formula> {

    public MojmirAutomatonFactoryFromLTL(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> createFrom(Formula from) {
        MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> cachedResult = getFromCache(from);
        if (null != cachedResult) {
            return cachedResult;
        }

        PropEquivalenceClassWithBeeDeeDee initialLabel = new PropEquivalenceClassWithBeeDeeDee(from);
        MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        ImmutableSet<MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>> states = super.reach(initialState, super.getAlphabet());

        cachedResult = new MojmirAutomaton<>(states, initialState, super.getAlphabet());
        putIntoCache(from, cachedResult);
        return cachedResult;
    }
}
