package ltl2rabin;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class MojmirAutomatonFactoryFromString extends AutomatonFactory<String, LTLPropEquivalenceClass, Set<String>> {

    @Override
    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String from) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFactory.Result parserResult = ltlFactory.buildLTL(from);

        MojmirAutomatonFactoryFromLTL factoryFromLTL = new MojmirAutomatonFactoryFromLTL();
        return factoryFromLTL.createFrom(new Pair<>(parserResult.getLtlFormula(), parserResult.getAlphabet()));
    }
}
