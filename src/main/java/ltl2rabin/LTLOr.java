package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class LTLOr extends LTLFormula {
    private ArrayList<LTLFormula> disjuncts;

    /**
     *
     * @param disjuncts The LTL formulae that are connected by the disjunction
     */
    public LTLOr(ArrayList<LTLFormula> disjuncts) {
        this.disjuncts = disjuncts;
    }

    public LTLOr(LTLFormula l, LTLFormula r) {
        // In case you wonder why I created the mergeTwoArguments method: A constructor call (this(...)) must be the
        // first statement in a constructor.
        this(mergeTwoArguments(l, r));
    }

    private static ArrayList<LTLFormula> mergeTwoArguments(LTLFormula l, LTLFormula r) {
        ArrayList<LTLFormula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        return params;
    }

    public Iterator<LTLFormula> getIterator() {
        return disjuncts.iterator();
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
    public LTLFormula af(Collection<String> letters) {
        ArrayList<LTLFormula> result = new ArrayList<>();
        for (LTLFormula f : disjuncts) {
            LTLFormula temp = f.af(letters);
            if (temp instanceof LTLBoolean) {
                // true | something = true
                if (((LTLBoolean) temp).getValue()) return new LTLBoolean(true);
                // false | something = something
                if (!((LTLBoolean) temp).getValue()) continue;
            }
            result.add(f.af(letters));
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

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(911, 19);
        for (LTLFormula d : disjuncts) {
            hashCodeBuilder.append(d);
        }
        return hashCodeBuilder.toHashCode();
    }
}
