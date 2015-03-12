package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        return new LTLOr(right.after(tokens), new LTLAnd(left.after(tokens), this));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return this.left.equals(((LTLUOperator)obj).left) && this.right.equals(((LTLUOperator)obj).right) ;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(left).append(right).toHashCode();
    }
}
