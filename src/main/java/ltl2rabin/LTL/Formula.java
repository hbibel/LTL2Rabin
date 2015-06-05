package ltl2rabin.LTL;

import ltl2rabin.IVisitableFormula;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class Formula implements IVisitableFormula<Formula> {
    public abstract String toString();
}