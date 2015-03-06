package ltl2rabin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a logical conjunction (&) in an LTL formula.
 */
public class LTLAnd extends LTLFormula {
    private ArrayList<LTLFormula> conjuncts;

    /**
     * The only valid constructor for LTLAnd
     * @param conjuncts The LTL formulae that are connected by the conjunction
     */
    public LTLAnd(ArrayList<LTLFormula> conjuncts) {
        this.conjuncts = conjuncts;
    }

    public LTLAnd(LTLFormula l, LTLFormula r) {
        ArrayList<LTLFormula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        conjuncts = params;
    }

    public LTLAnd() {
        throw new IllegalArgumentException("Empty constructor LTLAnd() has been called!");
    }

    @Override
    public String toString() {
        String result = "(";
        for (LTLFormula f : conjuncts) {
            result = result + f.toString() + " & ";
        }
        return result.substring(0, result.length()-3) + ")";
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        ArrayList<LTLFormula> result = new ArrayList<LTLFormula>();
        for (LTLFormula f : conjuncts) {
            result.add(f.after(tokens));
        }
        return new LTLAnd(result);
    }
}
