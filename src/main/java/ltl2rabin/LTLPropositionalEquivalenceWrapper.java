package ltl2rabin;

import net.sf.javabdd.BDD;

/**
 * Equivalence wrapper for LTLFormula objects; could become an abstract class to match the decorator pattern
 */
public class LTLPropositionalEquivalenceWrapper  {
    private LTLFormula formula;

    public LTLPropositionalEquivalenceWrapper(final LTLFormula formula) {
        this.formula = formula;
    }

    public Class<? extends LTLFormula> getFormulaClass() {
        return formula.getClass();
    }
/*
    @Override
    public int hashCode() {
        return this.getCachedBDD().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())
                && ((LTLPropositionalEquivalenceWrapper) obj).getFormulaClass().equals(formula.getClass())) {
            if (this.formula instanceof LTLVariable
                    || this.formula instanceof LTLBoolean
                    || this.formula instanceof LTLAnd
                    || this.formula instanceof LTLOr) {
                // for these types, check for propositional equivalence with the help of the BDDs.
                return ((LTLPropositionalEquivalenceWrapper) obj).getCachedBDD().equals(this.getCachedBDD());
            }
            // for G, F, X and U operators check for structural equivalence
            else return this.formula.equals(((LTLPropositionalEquivalenceWrapper) obj).formula);
        }
        return false;
    }
*/
}
