package ltl2rabin;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import java.util.HashMap;
import java.util.Iterator;

public class EquivalenceOfLTLs {
    public static BDDFactory bddFactory = BDDFactory.init("java", 2, 2);
    private static HashMap<LTLFormula, BDD> ltlToBDDMap = new HashMap<>();
    private static int varCount = 0;

    public static boolean arePropositionallyEquivalent(LTLFormula formula1, LTLFormula formula2) {
        // question: Should (F a) = (F a) always hold?
        if (formula1.equals(formula2)) return true;

        BDD bdd1 = lookUpBDD(formula1);
        BDD bdd2 = lookUpBDD(formula2);

        return bdd1.equals(bdd2);
    }

    private static BDD lookUpBDD(LTLFormula formula) {
        BDD result = ltlToBDDMap.get(formula);
        if (result != null) {
            return result;
        }

        if (formula instanceof LTLBoolean) {
            return ((LTLBoolean) formula).getValue() ? bddFactory.one() : bddFactory.zero();
        }
        else if (formula instanceof LTLOr) {
            Iterator<LTLFormula> it = ((LTLOr) formula).getIterator();
            LTLFormula tempFormula = it.next();
            result = lookUpBDD(tempFormula);
            while (it.hasNext()) {
                tempFormula = it.next();
                result = result.or(lookUpBDD(tempFormula));
            }
            return result;
        }
        else if (formula instanceof LTLAnd) {
            Iterator<LTLFormula> it = ((LTLAnd) formula).getIterator();
            LTLFormula tempFormula = it.next();
            result = lookUpBDD(tempFormula);
            while (it.hasNext()) {
                tempFormula = it.next();
                result = result.and(lookUpBDD(tempFormula));
            }
            return result;
        }
        else {
            // question: Does this condition always evaluate to true after the initial variable space is used up?
            if (bddFactory.varNum() <= varCount) bddFactory.extVarNum(1);
            BDD newBDDVar = bddFactory.ithVar(varCount);
            ltlToBDDMap.put(formula, newBDDVar);
            varCount++;
            return newBDDVar;
        }
    }
}
