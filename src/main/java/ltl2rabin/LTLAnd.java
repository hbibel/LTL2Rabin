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
            LTLFormula temp = f.after(tokens);
            // false & something = false
            if (temp instanceof LTLBoolean) {
                if (!((LTLBoolean) temp).getValue()) return new LTLBoolean(false);
                else continue;
            }
            result.add(temp);
        }
        // An empty disjunction list means that all conjuncts resolved to false
        if (0 == result.size()) return new LTLBoolean(false);
        // Only one conjunct? Then we don't need an "and".
        if (1 == result.size()) return result.get(0);
        return new LTLAnd(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        if (this.conjuncts.size() != ((LTLAnd)obj).conjuncts.size()) return false;
        boolean equality = true;
        for (int i = 0; i < this.conjuncts.size(); i++) {
            equality = equality && this.conjuncts.get(i).equals(((LTLAnd)obj).conjuncts.get(i));
        }
        return equality;
    }
}
