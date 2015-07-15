package ltl2rabin.LTL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class Or extends Formula {
    private final List<Formula> operands;

    /**
     *
     * @param operands The LTL formulas that are connected by the disjunction
     */
    public Or(List<Formula> operands) {
        this.operands = operands;
    }

    /**
     * Two argument constructor for Or.
     * @param l The disjunct left of the | operator
     * @param r The disjunct right of the | operator
     */
    public Or(final Formula l, final Formula r) {
        this(mergeTwoArguments(l, r));
    }

    private static List<Formula> mergeTwoArguments(final Formula l, final Formula r) {
        List<Formula> params = new ArrayList<>();
        params.add(l);
        params.add(r);
        return params;
    }

    /**
     * Use this method to iterate over the operands.
     *
     * @return The iterator over all operands
     */
    public Iterator<Formula> getIterator() {
        return operands.iterator();
    }

    public List<Formula> getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        if (0 == operands.size()) {
            return "ff";
        }
        else if (1 == operands.size()) {
            return operands.get(0).toString();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Formula> disjunctionIterator = operands.iterator();
        while (disjunctionIterator.hasNext()) {
            Formula operand = disjunctionIterator.next();
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
                    builder.append("(").append(operatorOfOperand).append("(").append(operandOfOperand.toString()).append("))");
                }
            }

            if (disjunctionIterator.hasNext()) {
                builder.append(" | ");
            }
        }
        return builder.toString();
    }

    /**
     * Accept a visitor. See for example {@link LTLAfGVisitor}.
     * @param visitor    An object implementing the IVisitor interface.
     * @return           Whatever the visitor does.
     */
    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), operands);
    }

    /**
     * This method tests for structural equivalence. For example, a | b is NOT structurally equivalent to b | a.
     * @param o If this is not of type Or, the result is false
     * @return Equivalence of this and o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Or ltlOr = (Or) o;

        return operands != null ? operands.equals(ltlOr.operands) : ltlOr.operands == null;
    }
}
