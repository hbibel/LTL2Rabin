package ltl2rabin;

import java.util.Set;
import java.util.function.BiFunction;

public class AfGFunction implements BiFunction<LTLFormula, Set<String>, LTLFormula> {

    @Override
    public LTLFormula apply(LTLFormula formula, Set<String> strings) {
        return formula.af(strings);
    }
}
