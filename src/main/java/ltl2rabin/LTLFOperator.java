package ltl2rabin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents the F (finally) operator in LTL.
 */
public class LTLFOperator extends LTLFormula {
    private LTLFormula operand;

    /**
     * The only valid constructor for LTLFOperator.
     * @param operand The LTLFormula following the operator
     */
    public LTLFOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLFOperator() {
        throw new IllegalArgumentException("Empty constructor LTLFOperator() called!");
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "F " + operand.toString();
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(this);
        orParameter.add(this.operand.after(tokens));
        return new LTLOr(orParameter);
    }
}
