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
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (0 == conjuncts.size()) {
            return "tt";
        }
        else if (1 == conjuncts.size()) {
            return conjuncts.get(0).toString();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Formula> conjunctsIterator = conjuncts.iterator();
        while (conjunctsIterator.hasNext()) {
            Formula operand = conjunctsIterator.next();
            if (operand instanceof And || operand instanceof Variable || operand instanceof Boolean) {
                builder.append(operand.toString());
            }
            else if (operand instanceof U || operand instanceof Or) {
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
