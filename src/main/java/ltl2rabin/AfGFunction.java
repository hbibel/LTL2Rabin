package ltl2rabin;

import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.LTLAfGVisitor;
import ltl2rabin.LTL.LTLPropEquivalenceClass;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<LTLPropEquivalenceClass, Set<String>, LTLPropEquivalenceClass> {

    public static final AfGFunction afgfunction = new AfGFunction();

    @Override
    public LTLPropEquivalenceClass apply(LTLPropEquivalenceClass equivalenceClass, Set<String> letter) {
        LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
        Formula formula = equivalenceClass.getRepresentative();
        return new LTLPropEquivalenceClass(visitor.afG(formula));
    }
}
