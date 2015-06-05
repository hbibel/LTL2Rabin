package ltl2rabin.LTL;

import ltl2rabin.ILTLFormulaVisitor;

import java.util.Objects;

/**
 * Represents a boolean value in an LTL formula (true or false)
 */
public class Boolean extends Formula {
    private final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value ? "tt" : "ff";
    }

    @Override
    public Formula accept(ILTLFormulaVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.value == ((Boolean)obj).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), value);
    }
}
