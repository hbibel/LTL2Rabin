package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

    public Iterator<LTLFormula> getIterator() {
        return conjuncts.iterator();
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
    public LTLFormula af(Collection<String> letters) {
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
        // An empty disjunction list means that all conjuncts resolved to true
        if (0 == newConjuncts.size()) return new LTLBoolean(true);
        // Only one conjunct? Then we don't need an "and".
        if (1 == newConjuncts.size()) return newConjuncts.get(0);
        return new LTLAnd(newConjuncts);
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

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(911, 19);
        for (LTLFormula c : conjuncts) {
            hashCodeBuilder.append(c);
        }
        return hashCodeBuilder.toHashCode();
    }
}
