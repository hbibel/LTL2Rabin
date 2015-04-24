package ltl2rabin;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class LTLFormula implements IVisitableFormula<LTLFormula> {
    public abstract String toString();
}