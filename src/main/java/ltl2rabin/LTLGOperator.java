package ltl2rabin;

import java.util.Collection;

/**
 * This class represents the G (globally) operator in LTL.
 */
public class LTLGOperator extends LTLFormula {
    private LTLFormula operand;

    /**
     * The only valid constructor for LTLGOperator.
     * @param operand The LTLFormula following the operator
     */
    public LTLGOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLGOperator() {
        throw new IllegalArgumentException("Empty constructor LTLGOperator() called!");
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "G " + operand.toString();
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        return new LTLAnd(this, operand.after(tokens));
    }
}
