package ltl2rabin;

import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LTLListenerTest {

    private LTLFormula testEnterFormulaHelp(String s) {
        ANTLRInputStream input = new ANTLRInputStream(s);
        LTLLexer lexer = new LTLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();

        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);

        return extractor.getLtlTree();
    }

    @Test
    public void getLtlTree() throws Exception {
        LTLVariable expectedTestResult1 = new LTLVariable("a");
        LTLFormula testCase1 = testEnterFormulaHelp("a");
        assertEquals(expectedTestResult1.toString(), testCase1.toString());

        LTLVariable ol = new LTLVariable("a");
        LTLVariable or = new LTLVariable("b");
        ArrayList<LTLFormula> op2 = new ArrayList<>();
        op2.add(ol);
        op2.add(or);
        LTLOr expectedTestResult2 = new LTLOr(op2);
        LTLFormula testCase2 = testEnterFormulaHelp("a | b");
        assertEquals(expectedTestResult2.toString(), testCase2.toString());

        LTLVariable ol3 = new LTLVariable("a");
        LTLVariable or3 = new LTLVariable("b");
        ArrayList<LTLFormula> op3 = new ArrayList<>();
        op3.add(ol3);
        op3.add(or3);
        LTLOr expectedTestResult3 = new LTLOr(op3);
        LTLFormula testCase3 = testEnterFormulaHelp("(a | b)");
        assertEquals(expectedTestResult3.toString(), testCase3.toString());

        ArrayList<LTLFormula> op4 = new ArrayList<>();
        op4.add(new LTLVariable("a"));
        op4.add(new LTLVariable("b"));
        op4.add(new LTLVariable("c"));
        LTLOr expectedTestResult4 = new LTLOr(op4);
        LTLFormula testCase4 = testEnterFormulaHelp("a | b | c");
        assertEquals(expectedTestResult4.toString(), testCase4.toString());

        LTLUOperator aub5 = new LTLUOperator(new LTLVariable("a"), new LTLVariable("b"));
        LTLOr expectedTestResult5 = new LTLOr(aub5, new LTLVariable("c"));
        LTLFormula testCase5 = testEnterFormulaHelp("(a U b | c)");
        assertEquals(expectedTestResult5.toString(), testCase5.toString());

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
        LTLFormula testCase999 = testEnterFormulaHelp("F G (a | b U c) & !a U (b U (X c))");
        assertEquals(expectedTestResult999.toString(), testCase999.toString());
    }
}