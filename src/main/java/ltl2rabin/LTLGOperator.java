package ltl2rabin;

import java.util.ArrayList;
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
        ArrayList<LTLFormula> andParameter = new ArrayList<>();
        andParameter.add(this);
        andParameter.add(operand.after(tokens));
        return new LTLAnd(andParameter);
    }
}
