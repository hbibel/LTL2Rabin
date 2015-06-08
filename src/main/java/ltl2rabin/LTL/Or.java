package ltl2rabin.LTL;

import net.sf.javabdd.BDD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class Or extends Formula {
    private final List<Formula> disjuncts;
    private BDD cachedBDD = null;

    /**
     *
     * @param disjuncts The LTL formulae that are connected by the disjunction
     */
    public Or(List<Formula> disjuncts) {
        this.disjuncts = disjuncts;
    }

    public Or(final Formula l, final Formula r) {
        // In case you wonder why I created the mergeTwoArguments method: A constructor call (this(...)) must be the
        // first statement in a constructor.
        this(mergeTwoArguments(l, r));
    }

    private static List<Formula> mergeTwoArguments(final Formula l, final Formula r) {
        List<Formula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        return params;
    }

    public Iterator<Formula> getIterator() {
        return disjuncts.iterator();
    }

    public List<Formula> getDisjuncts() {
        return disjuncts;
    }

    @Override
    public String toString() {
        String result = "(";
        for (Formula f : disjuncts) {
            result = result + "(" + f.toString() + ") | ";
        }
        // cut the last | and close the parentheses
        return result.substring(0, result.length()-3) + ")";
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
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

        Or ltlOr = (Or) o;

        return disjuncts != null ? disjuncts.equals(ltlOr.disjuncts) : ltlOr.disjuncts == null;
    }
}
