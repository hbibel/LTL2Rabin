package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A <code>SubAutomatonFromFormulaFactory</code> can create a SubAutomaton from an <code>LTL</code> object, using its alphabet.
 * Once constructed, every generated automaton will be put into a cache, so constructing a SubAutomaton several
 * times from the same formula will be efficient.
 */
public class SubAutomatonFromFormulaFactory extends RabinAutomatonFactory<Formula,
        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>,
        Set<String>> {
    private final MojmirAutomatonFactoryFromFormula mojmirAutomatonFactory;
    private final SubAutomatonFromMojmirFactory subAutomatonFromMojmirFactory;
    private static HashMap<Formula, SubAutomaton> subAutomata = new HashMap<>();

    /**
     *
     * @param alphabet    The set of letters that will be used to construct a SubAutomaton automaton.
     */
    public SubAutomatonFromFormulaFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
        mojmirAutomatonFactory = new MojmirAutomatonFactoryFromFormula(alphabet);
        subAutomatonFromMojmirFactory = new SubAutomatonFromMojmirFactory(alphabet);
    }

    public SubAutomaton createFrom(Formula psi) {
        SubAutomaton result = getFromCache(psi);
        if (null == result) {
            MojmirAutomaton<PropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactory.createFrom(psi);
            result = subAutomatonFromMojmirFactory.createFrom(ma);
            putIntoCache(psi, result);
        }
        return result;
    }

    protected static void putIntoCache(Formula psi, SubAutomaton subAutomaton) {
        subAutomata.put(psi, subAutomaton);
    }

    protected static SubAutomaton getFromCache(Formula psi) {
        return subAutomata.get(psi);
    }
}
