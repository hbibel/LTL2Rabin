package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

/**
 * This class represents the X (next) operator in LTL.
 */
public class LTLXOperator extends LTLFormula {
    private LTLFormula operand;

    /**
     * The only valid constructor for LTLXOperator.
     * @param operand The LTLFormula following the operator
     */
    public LTLXOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "X" + operand.toString();
    }

    @Override
    public LTLFormula after(Collection<String> letters) {
        return operand;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return this.operand.equals(((LTLXOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(operand).toHashCode();
    }
}
