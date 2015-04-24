package ltl2rabin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class LTLAfGVisitor implements ILTLFormulaVisitor<LTLFormula> {
    private final Set<String> letter;

    public LTLAfGVisitor(Set<String> letter) {
        this.letter = letter;
    }
// TODO: See LTLOr
    public LTLFormula visit(LTLAnd formula) {
        ArrayList<LTLFormula> newConjuncts = new ArrayList<>();
        Iterator<LTLFormula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            LTLFormula temp = iterator.next();
            newConjuncts.add(afG(temp));
        }
        return new LTLAnd(newConjuncts);
    }

    public LTLFormula visit(LTLBoolean formula) {
        return new LTLBoolean(formula.getValue());
    }

    public LTLFormula visit(LTLFOperator formula) {
        ArrayList<LTLFormula> orParameter = new ArrayList<>();
        orParameter.add(formula);
        orParameter.add(afG(formula.getOperand()));
        return new LTLOr(orParameter);
    }

    public LTLFormula visit(LTLGOperator formula) {
        return formula;
    }

    public LTLFormula visit(LTLOr formula) {
        return new LTLOr(formula.getDisjuncts().stream().map(this::afG).collect(Collectors.toList()));
    }

    public LTLFormula visit(LTLUOperator formula) {
        LTLFormula afGLeftSide = afG(formula.getLeft());
        LTLFormula afGRightSide = afG(formula.getRight());
        return new LTLOr(afGRightSide, new LTLAnd(afGLeftSide, formula));
    }

    public LTLFormula visit(LTLVariable formula) {
        return new LTLBoolean(formula.isNegated() != (letter.contains(formula.getValue())));
    }

    public LTLFormula visit(LTLXOperator formula) {
        return formula.getOperand();
    }

    public LTLFormula afG(IVisitableFormula<LTLFormula> formula) {
        return formula.accept(this);
    }
}