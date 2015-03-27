package ltl2rabin;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

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
    public LTLFormula af(Collection<String> letters) {
        LTLFormula afLeftSide = left.af(letters);
        LTLFormula afRightSide = right.af(letters);

        /*
        r   l   result
        ----------------
        ff  ff  ff
        ff  tt  this
        tt  ff  tt
        tt  tt  tt
        x   ff  afRightSide
        x   tt  afRightSide OR this
        tt  x   afLeftSide AND this
        tt  x   tt
        x   x   afRightSide OR (afLeftSide AND this)

        x = "is not instance of LTLBoolean"
         */
        if (afRightSide instanceof LTLBoolean) {
            if (((LTLBoolean) afRightSide).getValue()) return new LTLBoolean(true);
            if (afLeftSide instanceof LTLBoolean) {
                if(((LTLBoolean) afLeftSide).getValue()) return this;
                else return new LTLBoolean(false);
            }
            else {
                return new LTLAnd(afLeftSide, this);
            }
        }
        if (afLeftSide instanceof LTLBoolean) {
            if (!((LTLBoolean) afLeftSide).getValue()) return afRightSide;
            else return new LTLOr(afRightSide, this);
        }
        return new LTLOr(afRightSide, new LTLAnd(afLeftSide, this));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.left.equals(((LTLUOperator)obj).left)
                && this.right.equals(((LTLUOperator)obj).right) ;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(left).append(right).toHashCode();
    }
}
