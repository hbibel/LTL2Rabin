package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from, ImmutableSet<Set<String>> alphabet) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(parserResult.getLtlFormula(), Collections.<LTLFormula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        MojmirAutomatonFactoryFromLTL factoryFromLTL = new MojmirAutomatonFactoryFromLTL();
        cachedResult = factoryFromLTL.createFrom(parserResult.getLtlFormula(), alphabet);
        putIntoCache(new Pair<>(parserResult.getLtlFormula(), Collections.<LTLFormula>emptySet()), cachedResult);
        return cachedResult;
    }
}
