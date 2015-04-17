package ltl2rabin;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<LTLPropEquivalenceClass, Set<String>, LTLPropEquivalenceClass> {

    public static final AfGFunction afgfunction = new AfGFunction();

    @Override
    public LTLPropEquivalenceClass apply(LTLPropEquivalenceClass equivalenceClass, Set<String> letter) {
        LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
        LTLFormula formula = equivalenceClass.getRepresentative();
        return new LTLPropEquivalenceClass(visitor.afG(formula));
    }
}
