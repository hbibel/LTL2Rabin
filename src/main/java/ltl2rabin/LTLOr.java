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
            LTLFormula temp = f.after(tokens);
            if (temp instanceof LTLBoolean) {
                // true | something = true
                if (((LTLBoolean) temp).getValue()) return new LTLBoolean(true);
                // false | something = something
                if (!((LTLBoolean) temp).getValue()) continue;
            }
            result.add(f.after(tokens));
        }
        // An empty disjunction list means that all disjuncts resolved to false
        if (0 == result.size()) return new LTLBoolean(false);
        // Only one disjunct? Then we don't need an "or".
        if (1 == result.size()) return result.get(0);
        return new LTLOr(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        if (this.disjuncts.size() != ((LTLOr)obj).disjuncts.size()) return false;
        boolean equality = true;
        for (int i = 0; i < this.disjuncts.size(); i++) {
            equality = equality && this.disjuncts.get(i).equals(((LTLOr)obj).disjuncts.get(i));
        }
        return equality;
    }
}
