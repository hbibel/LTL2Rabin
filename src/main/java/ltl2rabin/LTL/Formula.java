package ltl2rabin.LTL;

import java.util.Iterator;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class Formula implements IVisitable<Formula> {
    public abstract String toString(); // TODO: Make toString methods less horrible by introducing boolean isInfixOperator etc
}