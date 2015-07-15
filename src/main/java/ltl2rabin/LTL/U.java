package ltl2rabin.LTL;

import java.util.Objects;

/**
 * This class represents the U (until) operator in an LTL formula. It is a binary operator and thus contains
 * references to exactly two operands.
 */
public class U extends Formula {
    private final Formula left;
    private final Formula right;

    /**
     *
     * @param left The Formula left of the U operator (i.e. what should happen <b>before</b>)
     * @param right The Formula right of the U operator (i.e. what should happen <b>thereafter</b>)
     */
    public U(final Formula left, final Formula right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return The left operand of the U operator, i.e. "what happens before". For <i>a U b</i>, this would be <i>a</i>.
     */
    public Formula getLeft() {
        return left;
    }

    /**
     * @return The operand right of the U operator. For <i>a U b</i>, this would be <i>b</i>.
     */
    public Formula getRight() {
        return right;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (left instanceof Variable || left instanceof Boolean) {
            builder.append(left.toString());
        }
        else {
            builder.append("(").append(left.toString()).append(")");
        }
        builder.append(" U ");
        if (right instanceof Variable || right instanceof Boolean) {
            builder.append(right.toString());
        }
        else {
            builder.append("(").append(left.toString()).append(")");
        }
        return builder.toString();
    }

    /**
     * Accept a visitor. See for example {@link LTLAfGVisitor}.
     * @param visitor    An object implementing the IVisitor interface.
     * @return           Whatever the visitor does.
     */
    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    /**
     * Tests for structural equivalence. Both operands have to be structurally equivalent for this method to return
     * true.
     *
     * @param obj    The object this is compared to.
     * @return True, if the objects are structurally equivalent. False otherwise.
     */
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
