package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents the G (globally) operator in LTL.
 */
public class LTLGOperator extends LTLFormula {
    private final LTLFormula operand;

    /**
     * The only valid constructor for LTLGOperator.
     * @param operand The LTLFormula following the operator
     */
    public LTLGOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "G (" + operand.toString() + ")";
    }

    @Override
    public LTLFormula af(final Collection<String> letters) {
        ArrayList<LTLFormula> andParameter = new ArrayList<>();
        andParameter.add(this);
        LTLFormula newConjunct = operand.af(letters);
        andParameter.add(newConjunct);
        return new LTLAnd(andParameter);
    }

    @Override
    public LTLFormula afG(Collection<String> letters) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((LTLGOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(operand).append(this.getClass()).toHashCode();
    }
}
