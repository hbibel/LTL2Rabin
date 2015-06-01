package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    public MojmirAutomatonFactoryFromString(ImmutableSet<Set<String>> dontCare) {
        super(dontCare);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);
        ImmutableSet<Set<String>> generatedAlphabet = parserResult.getAlphabet();

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(parserResult.getLtlFormula(), Collections.<LTLFormula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        MojmirAutomatonFactoryFromLTL factoryFromLTL = new MojmirAutomatonFactoryFromLTL(generatedAlphabet);
        cachedResult = factoryFromLTL.createFrom(parserResult.getLtlFormula());
        putIntoCache(new Pair<>(parserResult.getLtlFormula(), Collections.<LTLFormula>emptySet()), cachedResult);
        return cachedResult;
    }
}
