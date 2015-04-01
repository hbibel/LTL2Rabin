package ltl2rabin;

import net.sf.javabdd.BDD;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

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
    public LTLFormula af(final Collection<String> letters) {
        return this;
    }

    @Override
    public LTLFormula afG(Collection<String> letters) {
        return af(letters);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.value == ((LTLBoolean)obj).value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(911, 19).append(this.getClass()).append(value).toHashCode();
    }
}
