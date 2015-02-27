package ltl2rabin;

/**
 * Represents a variable (string) with an optional negation ('!') in an LTL Formula.
 */
public class LTLVariable extends LTLFormula {
    private final String value;
    private final boolean negated;

    /**
     * Default constructor for LTLVariable
     *
     * @param value The char literal
     * @param negated Indicates whether or not the char is preceded by a negation ('!'). Example usages:
     *                a --> <code>LTLVariable('a', false)</code>
     *                !a --> <code>LTLVariable('a', true)</code>
     */
    public LTLVariable(String value, boolean negated) {
        this.value = value;
        this.negated = negated;
    }

    /**
     * This constructor initializes <code>negated</code> to <code>false</code>
     * @param value The char literal
     */
    public LTLVariable(String value) {
        this.value = value;
        negated = false;
    }

    public LTLVariable() {
        throw new IllegalArgumentException("Empty constructor LTLVariable() has been called!");
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
}
