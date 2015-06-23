package ltl2rabin.LTL;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import java.util.*;

/**
 * An object of this class represents an equivalence class of LTL formulas.
 * This class solves several problems:
 *  - It separates the BDD library from the rest of the code
 *  - It eliminates formulas that are propositionally equivalent
 *  - It serves as a point where all variables and their BDD representations get stored
 */
public class PropEquivalenceClass {
    private static int bddVarCount = 0;
    private static final BDDFactory bddFactory = BDDFactory.init("java", 2, 2);

    private Formula representative;
    private BDD cachedBDD;
    private static final Map<Formula, BDD> formulaBDDMap = new HashMap<>();

    public PropEquivalenceClass(Formula representative) {
        this.representative = representative;
        this.cachedBDD = getOrCreateBDD(representative);
    }

    public Formula getRepresentative() {
        return representative;
    }

    public boolean implies(PropEquivalenceClass other) {
        return cachedBDD.imp(other.cachedBDD).isOne();
    }

    private static BDD getOrCreateBDD(final Formula formula) {
        BDD result = formulaBDDMap.get(formula);
        if (result != null) {
            return result;
        } else {
            if (formula instanceof And) {
                Iterator<Formula> it = ((And) formula).getIterator();
                Formula tempFormula = it.next();
                result = getOrCreateBDD(tempFormula);

                while (it.hasNext()) {
                    tempFormula = it.next();
                    result = result.and(getOrCreateBDD(tempFormula));
                }
            } else if (formula instanceof Or) {
                Iterator<Formula> it = ((Or) formula).getIterator();
                Formula tempFormula = it.next();
                result = getOrCreateBDD(tempFormula);

                while (it.hasNext()) {
                    tempFormula = it.next();
                    result = result.or(getOrCreateBDD(tempFormula));
                }
            } else if (formula instanceof Boolean) {
                result = ((Boolean) formula).getValue() ? bddFactory.one() : bddFactory.zero();
            } else if (formula instanceof Variable) {
                // The LTLListener class makes sure any Variable object is unique. Still, for any possible variable,
                // there might be two versions: A negated one and a non-negated one. For our BDD, we have to make sure
                // that those two are represented by the same BDDVariable.
                Variable notFormula = new Variable(((Variable) formula).getValue(),
                        !((Variable) formula).isNegated());
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

    public boolean isTautology() {
        return cachedBDD.isOne();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropEquivalenceClass that = (PropEquivalenceClass) o;

        return (that.representative.equals(this.representative)) || cachedBDD.equals(that.cachedBDD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cachedBDD);
    }
}