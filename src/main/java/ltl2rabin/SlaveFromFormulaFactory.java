package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A <code>SlaveFromFormulaFactory</code> can create a Slave from an <code>LTL</code> object, using its alphabet.
 * Once constructed, every generated automaton will be put into a cache, so constructing a Slave several
 * times from the same formula will be efficient.
 */
public class SlaveFromFormulaFactory extends RabinAutomatonFactory<Formula,
        List<MojmirAutomaton.State<PropEquivalenceClass, Set<String>>>,
        Set<String>> {
    private final MojmirAutomatonFactoryFromFormula mojmirAutomatonFactory;
    private final SlaveFromMojmirFactory slaveFromMojmirFactory;
    private static HashMap<Formula, Slave> slaves = new HashMap<>();

    /**
     *
     * @param alphabet    The set of letters that will be used to construct a Slave automaton.
     */
    public SlaveFromFormulaFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
        mojmirAutomatonFactory = new MojmirAutomatonFactoryFromFormula(alphabet);
        slaveFromMojmirFactory = new SlaveFromMojmirFactory(alphabet);
    }

    public Slave createFrom(Formula psi) {
        Slave result = getFromCache(psi);
        if (null == result) {
            MojmirAutomaton<PropEquivalenceClass, Set<String>> ma = mojmirAutomatonFactory.createFrom(psi);
            result = slaveFromMojmirFactory.createFrom(ma);
            putIntoCache(psi, result);
        }
        return result;
    }

    protected static void putIntoCache(Formula psi, Slave slave) {
        slaves.put(psi, slave);
    }

    protected static Slave getFromCache(Formula psi) {
        return slaves.get(psi);
    }
}
