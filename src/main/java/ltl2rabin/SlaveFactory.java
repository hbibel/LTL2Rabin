package ltl2rabin;

import com.google.common.collect.ImmutableSet;
import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.PropEquivalenceClassWithBeeDeeDee;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SlaveFactory extends RabinAutomatonFactory<Formula,
        List<MojmirAutomaton.State<PropEquivalenceClassWithBeeDeeDee, Set<String>>>,
        Set<String>> {
    private final MojmirAutomatonFactoryFromLTL mojmirAutomatonFactory;
    private final SlaveFromMojmirFactory slaveFromMojmirFactory;
    private static HashMap<Formula, Slave> slaves = new HashMap<>();

    public SlaveFactory(ImmutableSet<Set<String>> alphabet) {
        super(alphabet);
        mojmirAutomatonFactory = new MojmirAutomatonFactoryFromLTL(alphabet);
        slaveFromMojmirFactory = new SlaveFromMojmirFactory(alphabet);
    }

    public Slave createFrom(Formula psi) {
        Slave result = getFromCache(psi);
        if (null == result) {
            MojmirAutomaton<PropEquivalenceClassWithBeeDeeDee, Set<String>> ma = mojmirAutomatonFactory.createFrom(psi);
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
