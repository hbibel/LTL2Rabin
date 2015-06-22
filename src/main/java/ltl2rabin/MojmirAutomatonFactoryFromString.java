package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.LTLFactory;
import ltl2rabin.LTL.LTLFactoryFromString;
import ltl2rabin.LTL.PropEquivalenceClassWithBeeDeeDee;

import java.util.*;

public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    public MojmirAutomatonFactoryFromString(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    @Override
    public MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> cachedResult = getFromCache(parserResult.getLtlFormula());
        if (null != cachedResult) {
            return cachedResult;
        }

        MojmirAutomatonFactoryFromLTL factoryFromLTL = new MojmirAutomatonFactoryFromLTL(super.getAlphabet());
        cachedResult = factoryFromLTL.createFrom(parserResult.getLtlFormula());
        putIntoCache(parserResult.getLtlFormula(), cachedResult);
        return cachedResult;
    }
}
