package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "F " + operand.toString();
    }

    @Override
    public LTLFormula after(Collection<String> letters) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(this);
        LTLFormula newDisjunct = this.operand.after(letters);
        if (newDisjunct.equals(new LTLBoolean(true))) return new LTLBoolean(true);
        else if (newDisjunct.equals(new LTLBoolean(false))) return orParameter.get(0);
        orParameter.add(newDisjunct);
        return new LTLOr(orParameter);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return this.operand.equals(((LTLFOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(operand).toHashCode();
    }
}
