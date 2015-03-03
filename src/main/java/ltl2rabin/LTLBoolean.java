package ltl2rabin;

import java.util.Collection;

/**
 * Represents a boolean value in an LTL formula (true or false)
 */
public class LTLBoolean extends LTLFormula {
    private final boolean value;

    public LTLBoolean() throws IllegalArgumentException {
        throw new IllegalArgumentException("Empty constructor LTLBoolean() has been called!");
    }

    public LTLBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value) return "tt"; else return "ff";
    }

    @Override
    public LTLFormula after(Collection<String> tokens) {
        return this;
    }
}
