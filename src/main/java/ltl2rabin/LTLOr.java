package ltl2rabin;

import net.sf.javabdd.BDD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    public void accept(ILTLFormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), disjuncts);
    }

    // Since this equals method tests for structural equivalence, a & b does NOT equal b & a.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LTLOr ltlOr = (LTLOr) o;

        return disjuncts != null ? disjuncts.equals(ltlOr.disjuncts) : ltlOr.disjuncts == null;
    }
}
