package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.LTLFactory;
import ltl2rabin.LTL.LTLFactoryFromString;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.*;

/**
 * This factory can create a <code>MojmirAutomaton</code> from a <code>String</code>.
 */
public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    public MojmirAutomatonFactoryFromString(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    /**
     *
     * @param from    A string that matches LTL grammar rules.
     * @return        A Mojmir automaton whose initial state is labelled with the LTL formula in the string.
     */
    @Override
    public MojmirAutomaton<PropEquivalenceClass, Set<String>> createFrom(String from) {
        LTLFactory<String> ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        MojmirAutomaton<PropEquivalenceClass, Set<String>> cachedResult = getFromCache(parserResult.getLtlFormula());
        if (null != cachedResult) {
            return cachedResult;
        }

        MojmirAutomatonFactoryFromFormula factoryFromLTL = new MojmirAutomatonFactoryFromFormula(super.getAlphabet());
        cachedResult = factoryFromLTL.createFrom(parserResult.getLtlFormula());
        putIntoCache(parserResult.getLtlFormula(), cachedResult);
        return cachedResult;
    }
}
