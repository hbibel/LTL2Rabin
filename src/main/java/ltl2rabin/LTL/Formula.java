package ltl2rabin.LTL;

/**
 * Base class for all LTL operands. Operands are And, Boolean, F, G, Or, U, Variable and X.
 */
public abstract class Formula implements IVisitable<Formula> {
    public abstract String toString(); // TODO: Make toString methods less horrible
}