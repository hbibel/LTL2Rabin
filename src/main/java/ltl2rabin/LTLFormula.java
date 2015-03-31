package ltl2rabin;

import net.sf.javabdd.BDD;

import java.util.Collection;

/**
 * Base class for all LTL Formulae; An LTL formula is built using the composite pattern.
 */
public abstract class LTLFormula {
    private BDD cachedBDD = null;
    public abstract String toString();

    public abstract LTLFormula af(final Collection<String> letters);

    public abstract LTLFormula afG(final Collection<String> letters);

    public BDD getCachedBDD() {
        if (cachedBDD == null) {
            Main.bddFactory.extVarNum(1);
            cachedBDD = Main.bddFactory.ithVar(Main.bddVarCount++);
        }
        return cachedBDD;
    }
}