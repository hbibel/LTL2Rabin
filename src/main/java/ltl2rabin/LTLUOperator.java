package ltl2rabin;

import java.util.Objects;

/**
 * This class represents the U (until) operator in an LTL formula.
 */
public class LTLUOperator extends LTLFormula {
    private final LTLFormula left;
    private final LTLFormula right;

    /**
     * The only valid constructor for LTLUOperator
     * @param left The LTLFormula left of the U operator
     * @param right The LTLFormula right of the U operator
     */
    public LTLUOperator(final LTLFormula left, final LTLFormula right) {
        this.left = left;
        this.right = right;
    }

    public LTLFormula getLeft() {
        return left;
    }

    public LTLFormula getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "( (" + left.toString() + ") U (" + right.toString() + ") )";
    }

    @Override
    public void accept(ILTLFormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.left.equals(((LTLUOperator)obj).left)
                && this.right.equals(((LTLUOperator)obj).right) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), left, right);
    }
}
