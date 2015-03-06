package ltl2rabin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents the U (until) operator in an LTL formula.
 */
public class LTLUOperator extends LTLFormula {
    private LTLFormula left;
    private LTLFormula right;

    /**
     * The only valid constructor for LTLUOperator
     * @param left The LTLFormula left of the U operator
     * @param right The LTLFormula right of the U operator
     */
    public LTLUOperator(LTLFormula left, LTLFormula right) {
        this.left = left;
        this.right = right;
    }

    public LTLUOperator() {
        throw new IllegalArgumentException("Empty constructor LTLUOperator() has been called!");
    }


    @Override
    public String toString() {
        return "(" + left.toString() + " U " + right.toString() + ")";
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        ArrayList<LTLFormula> andParameter = new ArrayList<>();
        andParameter.add(left.after(tokens));
        andParameter.add(this);
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(right.after(tokens));
        orParameter.add(new LTLAnd(andParameter));
        return new LTLOr(orParameter);
    }
}
