package ltl2rabin.LTL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class LTLAfGVisitor implements ILTLFormulaVisitor<Formula> {
    private final Set<String> letter;

    public LTLAfGVisitor(Set<String> letter) {
        this.letter = letter;
    }

    public Formula visit(And formula) {
        ArrayList<Formula> newConjuncts = new ArrayList<>();
        Iterator<Formula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            Formula temp = iterator.next();
            newConjuncts.add(afG(temp));
        }
        return new And(newConjuncts);
    }

    public Formula visit(Boolean formula) {
        return new Boolean(formula.getValue());
    }

    public Formula visit(F formula) {
        ArrayList<Formula> orParameter = new ArrayList<>();
        orParameter.add(formula);
        orParameter.add(afG(formula.getOperand()));
        return new Or(orParameter);
    }

    public Formula visit(G formula) {
        return formula;
    }

    public Formula visit(Or formula) {
        return new Or(formula.getDisjuncts().stream().map(this::afG).collect(Collectors.toList()));
    }

    public Formula visit(U formula) {
        Formula afGLeftSide = afG(formula.getLeft());
        Formula afGRightSide = afG(formula.getRight());
        return new Or(afGRightSide, new And(afGLeftSide, formula));
    }

    public Formula visit(Variable formula) {
        return new Boolean(formula.isNegated() != (letter.contains(formula.getValue())));
    }

    public Formula visit(X formula) {
        return formula.getOperand();
    }

    public Formula afG(IVisitableFormula<Formula> formula) {
        return formula.accept(this);
    }
}