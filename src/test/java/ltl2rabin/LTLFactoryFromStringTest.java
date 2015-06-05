package ltl2rabin;

import ltl2rabin.LTL.*;
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
    }

    @Test
    public void testAgainstRandom() throws Exception {
        for (int i = 0; i < 5; i++) {
            Formula randomFormula = randomFactory.buildLTL(1+i).getLtlFormula();
            Formula generatedFormula = factory.buildLTL(randomFormula.toString()).getLtlFormula();
            assertEquals(randomFormula, generatedFormula);
        }
    }

    @Test
    public void testVariable() throws Exception {
        Variable expectedTestResult1 = new Variable("a");
        assertEquals(expectedTestResult1, factory.buildLTL("a").getLtlFormula());
    }

    @Test
    public void testOr() throws Exception {
        Variable ol = new Variable("a");
        Variable or = new Variable("b");
        ArrayList<Formula> op2 = new ArrayList<>();
        op2.add(ol);
        op2.add(or);
        Or expectedTestResult2 = new Or(op2);
        assertEquals(expectedTestResult2, factory.buildLTL("a | b").getLtlFormula());
    }

    @Test
    public void testParentheses() throws Exception {
        Variable ol3 = new Variable("a");
        Variable or3 = new Variable("b");
        ArrayList<Formula> op3 = new ArrayList<>();
        op3.add(ol3);
        op3.add(or3);
        Or expectedTestResult3 = new Or(op3);
        assertEquals(expectedTestResult3, factory.buildLTL("(a | b)").getLtlFormula());
    }

    @Test
    public void testMultiOr() throws Exception {
        ArrayList<Formula> op4 = new ArrayList<>();
        op4.add(new Variable("a"));
        op4.add(new Variable("b"));
        op4.add(new Variable("c"));
        Or expectedTestResult4 = new Or(op4);
        assertEquals(expectedTestResult4, factory.buildLTL("a | b | c").getLtlFormula());
    }

    @Test
        public void testUntilOr() throws Exception {
        U aub5 = new U(new Variable("a"), new Variable("b"));
        Or expectedTestResult5 = new Or(aub5, new Variable("c"));
        assertEquals(expectedTestResult5, factory.buildLTL("(a U b | c)").getLtlFormula());
    }

    @Test
    public void testBiggerExample() throws Exception {
        Variable c = new Variable("c");
        X xc = new X(c);
        Variable b = new Variable("b");
        U buxc = new U(b, xc);
        Variable na = new Variable("a", true);
        U naubuxc = new U(na, buxc);
        U buc = new U(b, c);
        Variable a = new Variable("a");
        Or aobuc = new Or(a, buc);
        And aobucanaubuxc = new And(aobuc, naubuxc);
        G gaobucanaubuxc = new G(aobucanaubuxc);
        F expectedTestResult6 = new F(gaobucanaubuxc);
        assertEquals(expectedTestResult6, factory.buildLTL("F G (a | b U c) & !a U (b U (X c))").getLtlFormula());
    }
}