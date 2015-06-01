package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Set;

public class MojmirAutomatonFactoryFromLTLAndSet extends MojmirAutomatonFactory<Pair<LTLFormula, ImmutableSet<LTLFormula>>> {
    public MojmirAutomatonFactoryFromLTLAndSet(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(Pair<LTLFormula, ImmutableSet<LTLFormula>> from) {
        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(from.getFirst(), from.getSecond()));
        if (null != cachedResult) {
            return cachedResult;
        }

        LTLFormula formula = from.getFirst();
        ImmutableSet<LTLFormula> curlyG = from.getSecond();

        LTLPropEquivalenceClass initialLabel = new LTLPropEquivalenceClass(formula);
        MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Pair<Set<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>, ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>>> reachResult = super.reach(initialState, getAlphabet(), curlyG);
        ImmutableSet<MojmirAutomaton.State<LTLPropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult.getFirst());

        cachedResult = new MojmirAutomaton<>(states, initialState, reachResult.getSecond(), getAlphabet());
        putIntoCache(new Pair<>(from.getFirst(), from.getSecond()), cachedResult);
        return cachedResult;
    }
}
