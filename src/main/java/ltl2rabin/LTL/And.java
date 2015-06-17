package ltl2rabin.LTL;

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
     * @param conjuncts The LTL formulae that are connected by the conjunction. For example, the conjunction
     *                  a & (X b) has the conjuncts a and (X b)
     */
    public And(List<Formula> conjuncts) {
        this.conjuncts = conjuncts;
    }

    /**
     * This constructor is for an conjunction with two conjuncts.
     * @param l The left conjunct. For example, for a & b, a is the left conjunct.
     * @param r The left conjunct. For example, for a & b, b is the right conjunct.
     */
    public And(Formula l, Formula r) {
        List<Formula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        conjuncts = params;
    }

    public List<Formula> getConjuncts() {
        return conjuncts;
    }

    /**
     * Use this method to iterate over the conjuncts.
     *
     * @return The iterator over all conjuncts
     */
    public Iterator<Formula> getIterator() {
        return conjuncts.iterator();
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (0 == conjuncts.size()) {
            return "tt"; // an empty conjunction is, by definition, true
        }
        else if (1 == conjuncts.size()) {
            return conjuncts.get(0).toString();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Formula> conjunctsIterator = conjuncts.iterator();
        while (conjunctsIterator.hasNext()) {
            Formula operand = conjunctsIterator.next();
            if (operand instanceof Variable || operand instanceof Boolean) {
                builder.append(operand.toString()); // ... & a & ..., no parentheses needed
            }
            else if (operand instanceof And || operand instanceof U || operand instanceof Or) {
                // (a & b) & c is structurally different from a & b & c, this is important for parsing. Logically,
                // nested conjunctions don't have to be put in parentheses.
                // And has higher priority than U and Or, so those have to be put in parentheses.
                builder.append("(").append(operand.toString()).append(")");
            }
            else {
                // operand is an prefix operator
                Formula operandOfOperand; // The last prefix operator binds strongest.
                String operatorOfOperand;
                if (operand instanceof G) {
                    operandOfOperand = ((G) operand).getOperand();
                    operatorOfOperand = "G ";
                }
                else if (operand instanceof F) {
                    operandOfOperand = ((F) operand).getOperand();
                    operatorOfOperand = "F ";
                }
                else {
                    operandOfOperand = ((X) operand).getOperand();
                    operatorOfOperand = "X ";
                }

                if (operandOfOperand instanceof Variable || operandOfOperand instanceof Boolean
                        || operandOfOperand instanceof G || operandOfOperand instanceof F || operandOfOperand instanceof X) {
                    builder.append("(").append(operatorOfOperand).append(operandOfOperand.toString()).append(")");
                }
                else {
                    builder.append("(").append(operatorOfOperand).append("(").append(operandOfOperand.toString()).append("))");
                }
            }

            if (conjunctsIterator.hasNext()) {
                builder.append(" & ");
            }
        }
        return builder.toString();
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
