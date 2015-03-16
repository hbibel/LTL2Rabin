package ltl2rabin;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class BDDTest {
    static BDDFactory bddFactory = BDDFactory.init("j", 2, 2);

    public static void main(String[] args) {
        bddFactory.setVarNum(5);
        System.out.println("bddVarNum = " + bddFactory.varNum());
        BDD bla0 = bddFactory.ithVar(0);
        BDD bla1 = bddFactory.ithVar(1);
        BDD bla2 = bddFactory.ithVar(2);
        BDD bla3 = bddFactory.ithVar(3);
        BDD bla4 = bddFactory.ithVar(4);

        BDD and1 = bla0.and(bla1);
        BDD and2 = bla2.and(bla3);
        BDD or1 = and1.or(and2);
        BDD res = or1.low();
        BDD test = bla0.low();
        int aasfhgd = test.nodeCount();
        int qwkehrtgf = bla0.nodeCount();
        int sgsdsfjhdsglj = or1.nodeCount();


        System.out.println("and4: " + res.toString());
    }

    public static BDD ltlFormulaToBdd(LTLFormula formula) {
        if (formula instanceof LTLGOperator) {
            bddFactory.extVarNum(1);
            BDD gop = bddFactory.ithVar(0);
        }
        return null;
    }
}
