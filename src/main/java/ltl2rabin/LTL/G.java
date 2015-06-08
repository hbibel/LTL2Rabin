package ltl2rabin.LTL;

import java.util.Objects;

/**
 * This class represents the G (globally) operator in LTL.
 */
public class G extends Formula {
    private final Formula operand;

    /**
     * The only valid constructor for G.
     * @param operand The Formula following the operator
     */
    public G(Formula operand) {
        this.operand = operand;
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "G (" + operand.toString() + ")";
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((G)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, this.getClass());
    }
}
