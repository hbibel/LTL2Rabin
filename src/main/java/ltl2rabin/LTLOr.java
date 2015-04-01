package ltl2rabin;

import net.sf.javabdd.BDD;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class LTLOr extends LTLFormula {
    private final List<LTLFormula> disjuncts;
    private BDD cachedBDD = null;

    /**
     *
     * @param disjuncts The LTL formulae that are connected by the disjunction
     */
    public LTLOr(List<LTLFormula> disjuncts) {
        this.disjuncts = disjuncts;
    }

    public LTLOr(final LTLFormula l, final LTLFormula r) {
        // In case you wonder why I created the mergeTwoArguments method: A constructor call (this(...)) must be the
        // first statement in a constructor.
        this(mergeTwoArguments(l, r));
    }

    private static List<LTLFormula> mergeTwoArguments(final LTLFormula l, final LTLFormula r) {
        List<LTLFormula> params = new ArrayList<>();
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
            result = result + "(" + f.toString() + ") | ";
        }
        // cut the last | and close the parentheses
        return result.substring(0, result.length()-3) + ")";
    }

    @Override
    public LTLFormula af(final Collection<String> letters) {
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
    public LTLFormula afG(Collection<String> letters) {
        return af(letters);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(911, 19).append(this.getClass());
        disjuncts.forEach(hashCodeBuilder::append);
        return hashCodeBuilder.toHashCode();
    }
}
