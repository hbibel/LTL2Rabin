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
        if (0 == disjuncts.size()) {
            return "ff";
        }
        else if (1 == disjuncts.size()) {
            return disjuncts.get(0).toString();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Formula> disjunctsIterator = disjuncts.iterator();
        while (disjunctsIterator.hasNext()) {
            Formula operand = disjunctsIterator.next();
            if (operand instanceof And || operand instanceof Variable || operand instanceof Boolean || operand instanceof Or) {
                builder.append(operand.toString());
            }
            else if (operand instanceof U) {
                builder.append("(").append(operand.toString()).append(")");
            }
            else {
                // prefix operator
                Formula operandOfOperand;
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
                    builder.append(operatorOfOperand).append("(").append(operandOfOperand.toString()).append(")");
                }
            }

            if (disjunctsIterator.hasNext()) {
                builder.append(" | ");
            }
        }
        return builder.toString();
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
