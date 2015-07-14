package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.LTLFactory;
import ltl2rabin.LTL.LTLFactoryFromString;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.*;

/**
 * A <code>MojmirAutomatonFactoryFromString</code> provides a {@link #createFrom(String)} method that takes a
 * String and returns a {@link MojmirAutomaton}. The String has to match LTL grammar rules to be parsable.
 *
 * <p>This factory does not necessarily create <b>new</b> objects. Once created, a {@link MojmirAutomaton} created by a
 * factory gets cached and will be returned again if the {@link #createFrom(String)} method is called again with the
 * same parameters.
 *
 * @see ltl2rabin.MojmirAutomatonFactory
 */
public class MojmirAutomatonFactoryFromString extends MojmirAutomatonFactory<String> {

    public MojmirAutomatonFactoryFromString(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    /**
     * Create a {@link MojmirAutomaton} from a String. The initial {@link ltl2rabin.MojmirAutomaton.State}
     * will be labelled with the {@link Formula} generated from the String <b>from</b>, resp. its
     * {@link PropEquivalenceClass}. Calling this method several times
     * with the same parameter will return the same object, since the automaton will be cached.
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
