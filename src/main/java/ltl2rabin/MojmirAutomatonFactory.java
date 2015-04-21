package ltl2rabin;

import java.util.Set;

public class MojmirAutomatonFactory<T, U> extends AutomatonFactory<T, U> {

    @Override
    public MojmirAutomaton<T, U> createFrom(T from) {
        return null;
    }

    public MojmirAutomaton<LTLPropEquivalenceClass, Set<String>> createFrom(String ltlFormula) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFormula f = ltlFactory.buildLTL(ltlFormula);
        MojmirAutomatonFactory<LTLPropEquivalenceClass, Set<String>> mojmirAutomatonFactory = new MojmirAutomatonFactory<>();
        return mojmirAutomatonFactory.createFrom(new LTLPropEquivalenceClass(f));
    }
}
