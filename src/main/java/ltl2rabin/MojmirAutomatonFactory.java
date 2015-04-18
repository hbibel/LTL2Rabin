package ltl2rabin;

public class MojmirAutomatonFactory<T, U> extends AutomatonFactory<T, U> {

    @Override
    public MojmirAutomaton<T, U> createFrom(T from) {
        return null;
    }

    public MojmirAutomaton<LTLFormula, U> createFrom(String ltlFormula) {
        LTLFactoryFromString ltlFactory = new LTLFactoryFromString();
        LTLFormula f = ltlFactory.buildLTL(ltlFormula);
        MojmirAutomatonFactory<LTLFormula, U> mojmirAutomatonFactory = new MojmirAutomatonFactory<>();
        return mojmirAutomatonFactory.createFrom(f);
    }
}
