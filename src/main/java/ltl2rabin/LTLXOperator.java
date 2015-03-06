package ltl2rabin;

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

    public LTLXOperator() {
        throw new IllegalArgumentException("Empty constructor LTLXOperator() called!");
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "X" + operand.toString();
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        return operand;
    }
}
