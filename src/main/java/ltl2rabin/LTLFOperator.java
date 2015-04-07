package ltl2rabin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * This class represents the F (finally) operator in LTL.
 */
public class LTLFOperator extends LTLFormula {
    private final LTLFormula operand;

    public LTLFOperator(LTLFormula operand) {
        this.operand = operand;
    }

    public LTLFormula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "F (" + operand.toString() + ")";
    }

    @Override
    public LTLFormula af(final Collection<String> letters) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(this);
        LTLFormula newDisjunct = this.operand.af(letters);
        if (newDisjunct.equals(new LTLBoolean(true))) return new LTLBoolean(true);
        else if (newDisjunct.equals(new LTLBoolean(false))) return orParameter.get(0);
        orParameter.add(newDisjunct);
        return new LTLOr(orParameter);
    }

    @Override
    public LTLFormula afG(Collection<String> letters) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(this);
        orParameter.add(this.operand.afG(letters));
        return new LTLOr(orParameter);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((LTLFOperator)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, this.getClass());
    }
}
