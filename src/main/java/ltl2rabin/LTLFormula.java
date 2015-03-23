package ltl2rabin;

import java.util.Collection;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class LTLFormula {
    public abstract String toString();

    public abstract LTLFormula af(final Collection<String> letters);

    /*
     * This equality test tests for propositional equivalence. It is used within the reach() function of the
     * MojmirAutomaton class, to be precisely in the called states.add() method.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && EquivalenceOfLTLs.arePropositionallyEquivalent((LTLFormula) obj, this);
    }
}