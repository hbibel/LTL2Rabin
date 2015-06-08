package ltl2rabin.LTL;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class Formula implements IVisitableFormula<Formula> {
    public abstract String toString();
}