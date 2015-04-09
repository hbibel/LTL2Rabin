package ltl2rabin;

import java.util.Objects;

/**
 * Represents a boolean value in an LTL formula (true or false)
 */
public class LTLBoolean extends LTLFormula {
    private final boolean value;

    public LTLBoolean(boolean value) {
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
    public void accept(ILTLFormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.value == ((LTLBoolean)obj).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), value);
    }
}
