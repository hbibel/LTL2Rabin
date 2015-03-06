package ltl2rabin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class LTLOr extends LTLFormula {
    private ArrayList<LTLFormula> disjuncts;

    /**
     * The only valid constructor for LTLOr
     * @param disjuncts The LTL formulae that are connected by the disjunction
     */
    public LTLOr(ArrayList<LTLFormula> disjuncts) {
        this.disjuncts = disjuncts;
    }

    public LTLOr(LTLFormula l, LTLFormula r) {
        ArrayList<LTLFormula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        disjuncts = params;
    }

    public LTLOr() {
        throw new IllegalArgumentException("Empty constructor LTLOr() has been called!");
    }


    @Override
    public String toString() {
        String result = "(";
        for (LTLFormula f : disjuncts) {
            result = result + f.toString() + " | ";
        }
        // cut the last | and close the parentheses
        return result.substring(0, result.length()-3) + ")";
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        ArrayList<LTLFormula> result = new ArrayList<LTLFormula>();
        for (LTLFormula f : disjuncts) {
            result.add(f.after(tokens));
        }
        return new LTLOr(result);
    }
}
