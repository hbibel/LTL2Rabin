package ltl2rabin;

import org.junit.Test;

public class MainTest {
    @Test
    public void parseLTLFormulaTest() {
        // These tests are obsolete for now. They'll be reactivated once I continue the work on the parser
        /* System.out.println("\n\n#### Test 1 ####");
        LTLVariable expectedTestResult1 = new LTLVariable("a");
        LTLFormula testCase1 = Main.parseLTLFormula("a", null);
        assertEquals(expectedTestResult1.toString(), testCase1.toString());
        Main.resetStringPosition();

        System.out.println("\n\n#### Test 2 ####");
        LTLOr expectedTestResult2 = new LTLOr(new LTLVariable("a"), new LTLVariable("b"));
        LTLFormula testCase2 = Main.parseLTLFormula("a | b", null);
        assertEquals(expectedTestResult2.toString(), testCase2.toString());
        Main.resetStringPosition();

        System.out.println("\n\n#### Test 3 ####");
        LTLOr expectedTestResult3 = new LTLOr(new LTLVariable("a"), new LTLVariable("b"));
        LTLFormula testCase3 = Main.parseLTLFormula("(a | b)", null);
        assertEquals(expectedTestResult3.toString(), testCase3.toString());
        Main.resetStringPosition();

        System.out.println("\n\n#### Test 4 ####");
        LTLOr boc4 = new LTLOr(new LTLVariable("b"), new LTLVariable("c"));
        LTLOr expectedTestResult4 = new LTLOr(new LTLVariable("a"), boc4);
        LTLFormula testCase4 = Main.parseLTLFormula("a | b | c", null);
        System.out.println("Test result 4: " + expectedTestResult4 + " = " + testCase4);
        assertEquals(expectedTestResult4.toString(), testCase4.toString());
        Main.resetStringPosition();

        System.out.println("\n\n#### Test 5 ####");
        LTLUOperator buc5 = new LTLUOperator(new LTLVariable("b"), new LTLVariable("c"));
        LTLOr expectedTestResult5 = new LTLOr(new LTLVariable("a"), buc5);
        LTLFormula testCase5 = Main.parseLTLFormula("(a U b | c)", null);
        System.out.println("Test result 5: " + expectedTestResult5 + " = " + testCase5);
        assertEquals(expectedTestResult5.toString(), testCase5.toString());
        Main.resetStringPosition();

        System.out.println("\n\n#### Test 999 ####");
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
        LTLFOperator fgaobucanaubuxc = new LTLFOperator(gaobucanaubuxc);
        LTLFOperator expectedTestResult999 = fgaobucanaubuxc;
        LTLFormula testCase999 = Main.parseLTLFormula("F G (a | b U c) & !a U (b U X c)", null);
        assertEquals(expectedTestResult999.toString(), testCase999.toString());
        Main.resetStringPosition();*/
    }
}