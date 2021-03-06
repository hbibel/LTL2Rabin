package ltl2rabin.LTL;

import java.util.Objects;

/**
 * This class represents the X (next) operator in LTL. It is a unary operator and thus contains a reference to
 * exactly one operand.
 */
public class X extends Formula {
    private final Formula operand;

    /**
     * @param operand The Formula following the operator
     */
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

    /**
     * Accept a visitor. See for example {@link LTLAfGVisitor}.
     * @param visitor    An object implementing the IVisitor interface.
     * @return           Whatever the visitor does.
     */
    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    /**
     * Tests for structural equivalence. E.g., "<i>F (a &amp; b)</i>" is structurally equivalent to
     * "<i>F (a &amp; b)</i>", but not to "<i>F (b &amp; a)</i>".
     *
     * @param obj    The object this is compared to.
     * @return True, if the objects are structurally equivalent. False otherwise.
     */
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
