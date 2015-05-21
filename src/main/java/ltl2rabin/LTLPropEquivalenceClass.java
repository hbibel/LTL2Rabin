package ltl2rabin;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * An object of this class represents an equivalence class of LTL formulas.
 * This class solves several problems:
 *  - It separates the BDD library from the rest of the code
 *  - It eliminates formulas that are propositionally equivalent
 *  - It serves as a point where all variables and their BDD representations get stored
 */
public class LTLPropEquivalenceClass {
    private static int bddVarCount = 0;
    private static final BDDFactory bddFactory = BDDFactory.init("java", 2, 2);

    private LTLFormula representative;
    private BDD cachedBDD;
    private static final Map<LTLFormula, BDD> formulaBDDMap = new HashMap<>();

    public LTLPropEquivalenceClass(LTLFormula representative) {
        this.representative = representative;
        this.cachedBDD = getOrCreateBDD(representative);
    }

    public LTLFormula getRepresentative() {
        return representative;
    }

    public boolean implies(LTLPropEquivalenceClass other) {
        return cachedBDD.imp(other.cachedBDD).isOne();
    }

    private static BDD getOrCreateBDD(final LTLFormula formula) {
        BDD result = formulaBDDMap.get(formula);
        if (result != null) {
            return result;
        } else {
            if (formula instanceof LTLAnd) {
                Iterator<LTLFormula> it = ((LTLAnd) formula).getIterator();
                LTLFormula tempFormula = it.next();
                result = getOrCreateBDD(tempFormula);

                while (it.hasNext()) {
                    tempFormula = it.next();
                    result = result.and(getOrCreateBDD(tempFormula));
                }
            } else if (formula instanceof LTLOr) {
                Iterator<LTLFormula> it = ((LTLOr) formula).getIterator();
                LTLFormula tempFormula = it.next();
                result = getOrCreateBDD(tempFormula);

                while (it.hasNext()) {
                    tempFormula = it.next();
                    result = result.or(getOrCreateBDD(tempFormula));
                }
            } else if (formula instanceof LTLBoolean) {
                result = ((LTLBoolean) formula).getValue() ? bddFactory.one() : bddFactory.zero();
            } else if (formula instanceof LTLVariable) {
                // The LTLListener class makes sure any LTLVariable object is unique. Still, for any possible variable,
                // there might be two versions: A negated one and a non-negated one. For our BDD, we have to make sure
                // that those two are represented by the same BDDVariable.
                LTLVariable notFormula = new LTLVariable(((LTLVariable) formula).getValue(),
                        !((LTLVariable) formula).isNegated());
                BDD notFormulaBDD = formulaBDDMap.get(notFormula);
                // If a negated version of our variable already exists, negate it and return it. Otherwise just proceed
                // in creating a new variable (down below)
                if (notFormulaBDD != null) {
                    result = notFormulaBDD.not();
                } else {
                    bddFactory.extVarNum(1);
                    result = bddFactory.ithVar(bddVarCount++);
                }
            } else {
                bddFactory.extVarNum(1);
                result = bddFactory.ithVar(bddVarCount++);
            }
            formulaBDDMap.put(formula, result);

            return result;
        }
    }

    boolean isTautology() {
        return cachedBDD.isOne();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LTLPropEquivalenceClass that = (LTLPropEquivalenceClass) o;

        return (that.representative.equals(this.representative)) || cachedBDD.equals(that.cachedBDD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cachedBDD);
    }
}
