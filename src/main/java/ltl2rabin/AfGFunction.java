package ltl2rabin;

import ltl2rabin.LTL.Formula;
import ltl2rabin.LTL.LTLAfGVisitor;
import ltl2rabin.LTL.PropEquivalenceClass;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<PropEquivalenceClass, Set<String>, PropEquivalenceClass> {

    public static final AfGFunction afgfunction = new AfGFunction();

    @Override
    public PropEquivalenceClass apply(PropEquivalenceClass equivalenceClass, Set<String> letter) {
        LTLAfGVisitor visitor = new LTLAfGVisitor(letter);
        Formula formula = equivalenceClass.getRepresentative();
        return new PropEquivalenceClass(visitor.afG(formula));
    }
}
