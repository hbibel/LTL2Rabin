package ltl2rabin.LTL;

import ltl2rabin.ILTLFormulaVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a logical conjunction (&) in an LTL formula.
 */
public class And extends Formula {
    private final List<Formula> conjuncts;

    /**
     * The only valid constructor for And
     * @param conjuncts The LTL formulae that are connected by the conjunction
     */
    public And(List<Formula> conjuncts) {
        this.conjuncts = conjuncts;
    }

    public And(Formula l, Formula r) {
        List<Formula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        conjuncts = params;
    }

    public List<Formula> getConjuncts() {
        return conjuncts;
    }

    public Iterator<Formula> getIterator() {
        return conjuncts.iterator();
    }

    @Override
    public Formula accept(ILTLFormulaVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String result = "(";
        for (Formula f : conjuncts) {
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

        And ltlAnd = (And) o;

        return conjuncts != null ? conjuncts.equals(ltlAnd.conjuncts) : ltlAnd.conjuncts == null;
    }
}
