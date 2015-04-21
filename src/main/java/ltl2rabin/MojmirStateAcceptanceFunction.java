package ltl2rabin;

import java.util.function.Function;

public class MojmirStateAcceptanceFunction<T> implements Function<MojmirAutomaton<T, ?>.State, Boolean> {
    @Override
    public Boolean apply(MojmirAutomaton<T, ?>.State state) {
        if (state.getLabel() instanceof LTLPropEquivalenceClass) {
            return applyPE((MojmirAutomaton<LTLPropEquivalenceClass, ?>.State) state);
        }
        return false;
    }

    private Boolean applyPE(MojmirAutomaton<LTLPropEquivalenceClass, ?>.State state) {
        return state.getLabel().equals(new LTLPropEquivalenceClass(new LTLBoolean(true)));
    }
}
