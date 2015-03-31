package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

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
    public LTLFormula af(Collection<String> letters) {
        return operand;
    }

    @Override
    public LTLFormula afG(Collection<String> letters) {
        return af(letters);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((LTLXOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(this.getClass()).append(operand).toHashCode();
    }
}
