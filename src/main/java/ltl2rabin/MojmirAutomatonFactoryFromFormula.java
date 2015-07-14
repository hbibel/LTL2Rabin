package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.*;

/**
 * A <code>MojmirAutomatonFactoryFromFormula</code> provides a {@link #createFrom(Formula)} method that takes a
 * {@link Formula} and returns a {@link MojmirAutomaton}.
 *
 * <p>This factory does not necessarily create <b>new</b> objects. Once created, a {@link MojmirAutomaton} created by a
 * factory gets cached and will be returned again if the {@link #createFrom(Formula)} method is called again with the
 * same parameters.
 *
 * @see ltl2rabin.MojmirAutomatonFactory
 */
public class MojmirAutomatonFactoryFromFormula extends MojmirAutomatonFactory<Formula> {

    public MojmirAutomatonFactoryFromFormula(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
    }

    /**
     * Create a {@link MojmirAutomaton} from an initial formula. The initial {@link ltl2rabin.MojmirAutomaton.State}
     * will be labelled with this formula, resp. its {@link PropEquivalenceClass}. Calling this method several times
     * with the same parameter will return the same object, since the automaton will be cached.
     *
     * @param from    The initial {@link Formula}
     * @return A {@link MojmirAutomaton}, whose initial state will be labelled with the {@link Formula} <b>from</b>.
     */
    @Override
    public MojmirAutomaton<PropEquivalenceClass, Set<String>> createFrom(Formula from) {
        MojmirAutomaton<PropEquivalenceClass, Set<String>> cachedResult = getFromCache(from);
        if (null != cachedResult) {
            return cachedResult;
        }

        PropEquivalenceClass initialLabel = new PropEquivalenceClass(from);
        MojmirAutomaton.State<PropEquivalenceClass, Set<String>> initialState =
                new MojmirAutomaton.State<>(initialLabel);
        Set<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>reachResult = super.reach(initialState, super.getAlphabet());
        ImmutableSet<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>> states = ImmutableSet.copyOf(reachResult);

        cachedResult = new MojmirAutomaton<>(states, initialState, super.getAlphabet());
        putIntoCache(from, cachedResult);
        return cachedResult;
    }
}
