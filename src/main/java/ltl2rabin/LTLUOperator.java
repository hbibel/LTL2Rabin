package ltl2rabin;

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
}
