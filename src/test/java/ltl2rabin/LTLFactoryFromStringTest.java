package ltl2rabin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LTLFactoryFromStringTest {
    LTLFactoryFromString factory;
    LTLRandomFactory randomFactory;

    @Before
    public void setUp() throws Exception {
        factory = new LTLFactoryFromString();
        randomFactory = new LTLRandomFactory();
    }

    @After
    public void tearDown() throws Exception {
        //LTLVariable.resetVariableSpace();
    }

    @Test
    public void testAgainstRandom() throws Exception {
        for (int i = 0; i < 5; i++) {
            LTLFormula randomFormula = randomFactory.buildLTL(1+i);
            LTLFormula generatedFormula = factory.buildLTL(randomFormula.toString());

            assertEquals(randomFormula, generatedFormula);
        }
    }

    @Test
    public void testVariable() throws Exception {
        LTLVariable expectedTestResult1 = new LTLVariable("a");
        assertEquals(expectedTestResult1, factory.buildLTL("a"));
    }

    @Test
    public void testOr() throws Exception {
        LTLVariable ol = new LTLVariable("a");
        LTLVariable or = new LTLVariable("b");
        ArrayList<LTLFormula> op2 = new ArrayList<>();
        op2.add(ol);
        op2.add(or);
        LTLOr expectedTestResult2 = new LTLOr(op2);
        assertEquals(expectedTestResult2, factory.buildLTL("a | b"));
    }

    @Test
    public void testParentheses() throws Exception {
        LTLVariable ol3 = new LTLVariable("a");
        LTLVariable or3 = new LTLVariable("b");
        ArrayList<LTLFormula> op3 = new ArrayList<>();
        op3.add(ol3);
        op3.add(or3);
        LTLOr expectedTestResult3 = new LTLOr(op3);
        assertEquals(expectedTestResult3, factory.buildLTL("(a | b)"));
    }

    @Test
    public void testMultiOr() throws Exception {
        ArrayList<LTLFormula> op4 = new ArrayList<>();
        op4.add(new LTLVariable("a"));
        op4.add(new LTLVariable("b"));
        op4.add(new LTLVariable("c"));
        LTLOr expectedTestResult4 = new LTLOr(op4);
        assertEquals(expectedTestResult4, factory.buildLTL("a | b | c"));
    }

    @Test
        public void testUntilOr() throws Exception {
        LTLUOperator aub5 = new LTLUOperator(new LTLVariable("a"), new LTLVariable("b"));
        LTLOr expectedTestResult5 = new LTLOr(aub5, new LTLVariable("c"));
        assertEquals(expectedTestResult5, factory.buildLTL("(a U b | c)"));
    }

    @Test
    public void testBiggerExample() throws Exception {
        LTLVariable c = new LTLVariable("c");
        LTLXOperator xc = new LTLXOperator(c);
        LTLVariable b = new LTLVariable("b");
        LTLUOperator buxc = new LTLUOperator(b, xc);
        LTLVariable na = new LTLVariable("a", true);
        LTLUOperator naubuxc = new LTLUOperator(na, buxc);
        LTLUOperator buc = new LTLUOperator(b, c);
        LTLVariable a = new LTLVariable("a");
        LTLOr aobuc = new LTLOr(a, buc);
        LTLAnd aobucanaubuxc = new LTLAnd(aobuc, naubuxc);
        LTLGOperator gaobucanaubuxc = new LTLGOperator(aobucanaubuxc);
        LTLFOperator expectedTestResult6 = new LTLFOperator(gaobucanaubuxc);
        assertEquals(expectedTestResult6, factory.buildLTL("F G (a | b U c) & !a U (b U (X c))"));
    }
}