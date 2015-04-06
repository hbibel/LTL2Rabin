package ltl2rabin;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<LTLPropEquivalenceClass, Set<String>, LTLPropEquivalenceClass> {

    @Override
    public LTLPropEquivalenceClass apply(LTLPropEquivalenceClass eqClass, Set<String> strings) {
        return new LTLPropEquivalenceClass(eqClass.getRepresentative().afG(strings));
    }
}
