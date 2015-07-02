package ltl2rabin.LTL;

import java.util.Objects;

/**
 * Represents a variable (string) with an optional negation ('!') in an LTL Formula.
 */
public class Variable extends Formula {
    private final String value;
    private final boolean negated;

    /**
     * This constructor creates a <code>Variable</code> that optionally can be negated.
     *
     * @param value The token that represents the variable
     * @param negated Indicates whether or not the char is preceded by a negation ('!'). Example usages: <pre>
     * {@code
     *                a --> <code>Variable('a', false)</code>
     *                !a --> <code>Variable('a', true)</code>
     * }
     * </pre>
     *
     */
    public Variable(String value, boolean negated) {
        this.value = value;
        this.negated = negated;
    }

    /**
     * This constructor initializes <code>negated</code> to <code>false</code>
     * @param value The token that represents the variable
     */
    public Variable(String value) {
        this.value = value;
        negated = false;
    }

    public String getValue() {
        return value;
    }

    public boolean isNegated() {
        return negated;
    }

    @Override
    public String toString() {
        if (negated) return "!" + value;
        else return value;
    }

    @Override
    public Formula accept(IVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.value.equals(((Variable)obj).value)
                && this.negated == ((Variable)obj).negated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), value, negated);
    }
}
