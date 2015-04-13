package ltl2rabin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    public LTLFormula accept(ILTLFormulaVisitor<LTLFormula> visitor) {
        return visitor.visit(this);
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
