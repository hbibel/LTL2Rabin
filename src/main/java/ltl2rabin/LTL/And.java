package ltl2rabin.LTL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a logical conjunction (&) in an LTL formula.
 */
public class And extends Formula {
    private final List<Formula> operands;

    /**
     * @param operands The LTL formulae that are connected by the conjunction. For example, the conjunction
     *                  a & (X b) has the operands a and (X b)
     */
    public And(List<Formula> operands) {
        this.operands = operands;
    }

    /**
     * This constructor is for an conjunction with two operands.
     * @param l The left operand. For example, for a & b, a is the left operand.
     * @param r The left operand. For example, for a & b, b is the right operand.
     */
    public And(Formula l, Formula r) {
        List<Formula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        operands = params;
    }

    public List<Formula> getOperands() {
        return operands;
    }

    /**
     * Use this method to iterate over the operands.
     *
     * @return The iterator over all operands
     */
    public Iterator<Formula> getIterator() {
        return operands.iterator();
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (0 == operands.size()) {
            return "tt"; // an empty conjunction is, by definition, true
        }
        else if (1 == operands.size()) {
            return operands.get(0).toString();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Formula> operandListIterator = operands.iterator();
        while (operandListIterator.hasNext()) {
            Formula operand = operandListIterator.next();
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

            if (operandListIterator.hasNext()) {
                builder.append(" & ");
            }
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), operands);
    }

    // Since this equals method tests for structural equivalence, a & b does NOT equal b & a.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        And ltlAnd = (And) o;

        return operands != null ? operands.equals(ltlAnd.operands) : ltlAnd.operands == null;
    }
}
