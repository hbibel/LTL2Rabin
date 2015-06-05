package ltl2rabin.LTL;

import ltl2rabin.ILTLFormulaVisitor;

import java.util.Objects;

/**
 * This class represents the X (next) operator in LTL.
 */
public class X extends Formula {
    private final Formula operand;

    public X(Formula operand) {
        this.operand = operand;
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "X (" + operand.toString() + ")";
    }

    @Override
    public Formula accept(ILTLFormulaVisitor<Formula> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass())
                && this.operand.equals(((X)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), operand);
    }
}
