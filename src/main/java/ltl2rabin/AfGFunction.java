package ltl2rabin;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<LTLPropEquivalenceClass, Set<String>, LTLPropEquivalenceClass> {
    LTLAfGVisitor visitor = new LTLAfGVisitor();

    @Override
    public LTLPropEquivalenceClass apply(LTLPropEquivalenceClass equivalenceClass, Set<String> letter) {
        LTLFormula formula = equivalenceClass.getRepresentative();
        return new LTLPropEquivalenceClass(visitor.afG(formula, letter));
    }
}
