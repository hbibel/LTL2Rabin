package ltl2rabin.LTL;

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

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
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
