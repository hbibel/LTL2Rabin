package ltl2rabin;

import java.util.*;

/**
 * This class represents a logical conjunction (&) in an LTL formula.
 */
public class LTLAnd extends LTLFormula {
    private final List<LTLFormula> conjuncts;

    /**
     * The only valid constructor for LTLAnd
     * @param conjuncts The LTL formulae that are connected by the conjunction
     */
    public LTLAnd(List<LTLFormula> conjuncts) {
        this.conjuncts = conjuncts;
    }

    public LTLAnd(LTLFormula l, LTLFormula r) {
        List<LTLFormula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        conjuncts = params;
    }

    public Iterator<LTLFormula> getIterator() {
        return conjuncts.iterator();
    }

    @Override
    public String toString() {
        String result = "(";
        for (LTLFormula f : conjuncts) {
            result = result + "(" + f.toString() + ") & ";
        }
        return result.substring(0, result.length()-3) + ")";
    }

    @Override
    public LTLFormula af(final Collection<String> letters) {
        ArrayList<LTLFormula> newConjuncts = new ArrayList<>();
        for (LTLFormula f : conjuncts) {
            LTLFormula temp = f.af(letters);
            // false & something = false
            if (temp instanceof LTLBoolean) {
                if (!((LTLBoolean) temp).getValue()) return new LTLBoolean(false);
                else continue;
            }
            newConjuncts.add(temp);
        }
        return new LTLAnd(newConjuncts);
    }

    @Override
    public LTLFormula afG(final Collection<String> letters) {
        ArrayList<LTLFormula> newConjuncts = new ArrayList<>();
        for (LTLFormula f : conjuncts) {
            LTLFormula temp = f.afG(letters);
            newConjuncts.add(temp);
        }
        return new LTLAnd(newConjuncts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), conjuncts);
    }

    // Since this equals method tests for structural equivalence, a & b does NOT equal b & a.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LTLAnd ltlAnd = (LTLAnd) o;

        return conjuncts != null ? conjuncts.equals(ltlAnd.conjuncts) : ltlAnd.conjuncts == null;
    }
}
