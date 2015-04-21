package ltl2rabin;

import java.util.function.Function;

public class StateAcceptanceFunction implements Function<MojmirAutomaton<LTLPropEquivalenceClass, ?>.State, Boolean> {
    @Override
    public Boolean apply(MojmirAutomaton<LTLPropEquivalenceClass, ?>.State state) {
        return state.getLabel().equals(new LTLPropEquivalenceClass(new LTLBoolean(true)));
    }
}
