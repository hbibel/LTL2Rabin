package ltl2rabin.LTL;

import ltl2rabin.ILTLFormulaVisitor;

import java.util.Objects;

/**
 * This class represents the U (until) operator in an LTL formula.
 */
public class U extends Formula {
    private final Formula left;
    private final Formula right;

    /**
     * The only valid constructor for U
     * @param left The Formula left of the U operator
     * @param right The Formula right of the U operator
     */
    public U(final Formula left, final Formula right) {
        this.left = left;
        this.right = right;
    }

    public Formula getLeft() {
        return left;
    }

    public Formula getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "( (" + left.toString() + ") U (" + right.toString() + ") )";
    }

    @Override
    public Formula accept(ILTLFormulaVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.left.equals(((U)obj).left)
                && this.right.equals(((U)obj).right) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), left, right);
    }
}
