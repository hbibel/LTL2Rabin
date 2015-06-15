package ltl2rabin.LTL;

import java.util.Objects;

/**
 * This class represents the X (next) operator in LTL.
 */
public class X extends Formula {
    private final Formula operand;

    public X(Formula operand) {
        this.operand = operand;
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        if (operand instanceof G || operand instanceof F || operand instanceof X
                || operand instanceof Variable || operand instanceof Boolean) {
            return "X " + operand.toString();
        }
        else {
            return "X (" + operand.toString() + ")";
        }
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((X)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), operand);
    }
}
