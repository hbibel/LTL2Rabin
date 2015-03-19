package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "G " + operand.toString();
    }

    @Override
    public LTLFormula af(Collection<String> letters) {
        ArrayList<LTLFormula> andParameter = new ArrayList<>();
        andParameter.add(this);
        LTLFormula newConjunct = operand.af(letters);
        if (newConjunct.equals(new LTLBoolean(true))) return andParameter.get(0);
        else if (newConjunct.equals(new LTLBoolean(false))) return new LTLBoolean(false);
        andParameter.add(newConjunct);
        return new LTLAnd(andParameter);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return this.operand.equals(((LTLGOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(operand).toHashCode();
    }
}
