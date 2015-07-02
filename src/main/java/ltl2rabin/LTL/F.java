package ltl2rabin.LTL;

import java.util.Objects;

/**
 * This class represents the F (finally) operator in LTL.
 */
public class F extends Formula {
    private final Formula operand;

    /**
     *
     * @param operand The formula the F operator is applied to.
     */
    public F(Formula operand) {
        this.operand = operand;
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        if (operand instanceof G || operand instanceof F || operand instanceof X
                || operand instanceof Variable || operand instanceof Boolean) {
            return "F " + operand.toString();
        }
        else {
            return "F (" + operand.toString() + ")";
        }
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((F)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, this.getClass());
    }
}
