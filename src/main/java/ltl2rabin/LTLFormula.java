package ltl2rabin;

import java.util.Collection;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class LTLFormula {
    public abstract String toString();

    public abstract LTLFormula after(Collection<String> letters);
}