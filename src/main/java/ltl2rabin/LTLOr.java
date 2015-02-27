package ltl2rabin;

/**
 * This class represents a logical disjunction (|) in an LTL formula.
 */
public class LTLOr extends LTLFormula {
    private LTLFormula left;
    private LTLFormula right;

    /**
     * The only valid constructor for LTLOr
     * @param left The LTLFormula left of the disjunction
     * @param right The LTLFormula right of the disjunction
     */
    public LTLOr(LTLFormula left, LTLFormula right) {
        this.left = left;
        this.right = right;
    }

    public LTLOr() {
        throw new IllegalArgumentException("Empty constructor LTLOr() has been called!");
    }


    @Override
    public String toString() {
        return "(" + left.toString() + " | " + right.toString() + ")";
    }
}
