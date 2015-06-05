package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.LTLFactory;
import ltl2rabin.LTL.LTLFactoryFromString;
import ltl2rabin.LTL.LTLPropEquivalenceClass;

import java.util.*;

public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    public MojmirAutomatonFactoryFromString(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> cachedResult = getFromCache(new Pair<>(parserResult.getLtlFormula(), Collections.<Formula>emptySet()));
        if (null != cachedResult) {
            return cachedResult;
        }

        MojmirAutomatonFactoryFromLTL factoryFromLTL = new MojmirAutomatonFactoryFromLTL(super.getAlphabet());
        cachedResult = factoryFromLTL.createFrom(parserResult.getLtlFormula());
        putIntoCache(new Pair<>(parserResult.getLtlFormula(), Collections.<Formula>emptySet()), cachedResult);
        return cachedResult;
    }
}
