package ltl2rabin.LTL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This visitor implements the afG function for a fixed letter. The afG function unfolds a Formula over a letter.
 * To unfold a Formula over a word, you have to chain several LTLAfGVisitors, one for each letter.
 * Example: You want to unfold the <code>Formula</code> f over the letters {"a"}, then {"b", "c"}, then again {"a"}:
 * <pre>
 * {@code
 * LTLAfGVisitor visitor1 = new LTLAfGVisitor(ImmutableSet.of("a"));
 * LTLAfGVisitor visitor2 = new LTLAfGVisitor(ImmutableSet.of("b", "c"));
 * Formula result = f.accept(visitor1).accept(visitor2).accept(visitor1);
 * }
 * </pre>
 */
public class LTLAfGVisitor implements IVisitor<Formula> {
    private final Set<String> letter;

    public LTLAfGVisitor(Set<String> letter) {
        this.letter = letter;
    }

    public Formula visit(And formula) {
        ArrayList<Formula> newConjunctList = new ArrayList<>();
        Iterator<Formula> iterator = formula.getIterator();
        while (iterator.hasNext()) {
            Formula temp = iterator.next();
            newConjunctList.add(afG(temp));
        }
        return new And(newConjunctList);
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
        return new Or(formula.getOperands().stream().map(this::afG).collect(Collectors.toList()));
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

    public Formula afG(IVisitable<Formula> formula) {
        return formula.accept(this);
    }
}