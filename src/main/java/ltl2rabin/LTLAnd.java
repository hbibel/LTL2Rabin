package ltl2rabin;

/**
 * This class represents a logical conjunction (&) in an LTL formula.
 */
public class LTLAnd extends LTLFormula {
    private LTLFormula left;
    private LTLFormula right;

    /**
     * The only valid constructor for LTLAnd
     * @param left The LTLFormula left of the conjunction
     * @param right The LTLFormula right of the conjunction
     */
    public LTLAnd(LTLFormula left, LTLFormula right) {
        this.left = left;
        this.right = right;
    }

    public LTLAnd() {
        throw new IllegalArgumentException("Empty constructor LTLAnd() has been called!");
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " & " + right.toString() + ")";
    }
}
