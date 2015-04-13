package ltl2rabin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class LTLAfGVisitor implements ILTLFormulaVisitor<LTLFormula> {
    private LTLFormula result;
    Set<String> letter;

    public LTLFormula visit(LTLAnd formula) {
        ArrayList<LTLFormula> newConjuncts = new ArrayList<>();
        Iterator<LTLFormula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            LTLFormula temp = iterator.next();
            newConjuncts.add(afG(temp, letter));
        }
        return new LTLAnd(newConjuncts);
    }

    public LTLFormula visit(LTLBoolean formula) {
        return new LTLBoolean(formula.getValue());
    }

    public LTLFormula visit(LTLFOperator formula) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(formula);
        orParameter.add(afG(formula.getOperand(), letter));
        return new LTLOr(orParameter);
    }

    public LTLFormula visit(LTLGOperator formula) {
        return formula;
    }

    public LTLFormula visit(LTLOr formula) {
        ArrayList<LTLFormula> newDisjuncts = new ArrayList<>();
        Iterator<LTLFormula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            LTLFormula temp = iterator.next();
            newDisjuncts.add(afG(temp, letter));
        }
        return new LTLOr(newDisjuncts);
    }

    public LTLFormula visit(LTLUOperator formula) {
        LTLFormula afGLeftSide = afG(formula.getLeft(), letter);
        LTLFormula afGRightSide = afG(formula.getRight(), letter);
        return new LTLOr(afGRightSide, new LTLAnd(afGLeftSide, formula));
    }

    public LTLFormula visit(LTLVariable formula) {
        return new LTLBoolean(formula.isNegated() != (letter.contains(formula.getValue())));
    }

    public LTLFormula visit(LTLXOperator formula) {
        return formula.getOperand();
    }

    public LTLFormula afG(IVisitable<LTLFormula> formula, Set<String> letter) {
        this.letter = letter;
        return formula.accept(this);
    }
}