package ltl2rabin;

import java.util.Objects;

/**
 * This class represents the X (next) operator in LTL.
 */
public class LTLXOperator extends LTLFormula {
    private final LTLFormula operand;

    public LTLXOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "X (" + operand.toString() + ")";
    }

    @Override
    public void accept(ILTLFormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((LTLXOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), operand);
    }
}
