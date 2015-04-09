package ltl2rabin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class LTLAfGVisitor implements ILTLFormulaVisitor {
    private LTLFormula result;
    Set<String> letter;

    public void visit(LTLAnd formula) {
        ArrayList<LTLFormula> newConjuncts = new ArrayList<>();
        Iterator<LTLFormula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            LTLFormula temp = iterator.next();
            newConjuncts.add(afG(temp, letter));
        }
        result = new LTLAnd(newConjuncts);
    }

    public void visit(LTLBoolean formula) {
        result = new LTLBoolean(formula.getValue());
    }

    public void visit(LTLFOperator formula) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(formula);
        orParameter.add(afG(formula.getOperand(), letter));
        result = new LTLOr(orParameter);
    }

    public void visit(LTLGOperator formula) {
        result = formula;
    }

    public void visit(LTLOr formula) {
        ArrayList<LTLFormula> newDisjuncts = new ArrayList<>();
        Iterator<LTLFormula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            LTLFormula temp = iterator.next();
            newDisjuncts.add(afG(temp, letter));
        }
        result = new LTLOr(newDisjuncts);
    }

    public void visit(LTLUOperator formula) {
        LTLFormula afGLeftSide = afG(formula.getLeft(), letter);
        LTLFormula afGRightSide = afG(formula.getRight(), letter);
        result = new LTLOr(afGRightSide, new LTLAnd(afGLeftSide, formula));
    }

    public void visit(LTLVariable formula) {
        result = new LTLBoolean(formula.isNegated() != (letter.contains(formula.getValue())));
    }

    public void visit(LTLXOperator formula) {
        result = formula.getOperand();
    }

    public LTLFormula afG(IVisitable formula, Set<String> letter) {
        this.letter = letter;
        formula.accept(this);
        return result;
    }
}