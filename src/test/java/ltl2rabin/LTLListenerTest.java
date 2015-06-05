package ltl2rabin;

import ltl2rabin.LTL.*;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LTLListenerTest {

    private LTLListener testEnterFormulaHelp(String s) {
        ANTLRInputStream input = new ANTLRInputStream(s);
        LTLLexer lexer = new LTLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();

        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);

        return extractor;
    }

    @Test
    public void getLtlTree() throws Exception {
        Variable expectedTestResult1 = new Variable("a");
        Formula testCase1 = testEnterFormulaHelp("a").getLtlTree();
        assertEquals(expectedTestResult1.toString(), testCase1.toString());
        // TODO: Test getTerminalSymbols

        Variable ol = new Variable("a");
        Variable or = new Variable("b");
        ArrayList<Formula> op2 = new ArrayList<>();
        op2.add(ol);
        op2.add(or);
        Or expectedTestResult2 = new Or(op2);
        Formula testCase2 = testEnterFormulaHelp("a | b").getLtlTree();
        assertEquals(expectedTestResult2.toString(), testCase2.toString());

        Variable ol3 = new Variable("a");
        Variable or3 = new Variable("b");
        ArrayList<Formula> op3 = new ArrayList<>();
        op3.add(ol3);
        op3.add(or3);
        Or expectedTestResult3 = new Or(op3);
        Formula testCase3 = testEnterFormulaHelp("(a | b)").getLtlTree();
        assertEquals(expectedTestResult3.toString(), testCase3.toString());

        ArrayList<Formula> op4 = new ArrayList<>();
        op4.add(new Variable("a"));
        op4.add(new Variable("b"));
        op4.add(new Variable("c"));
        Or expectedTestResult4 = new Or(op4);
        Formula testCase4 = testEnterFormulaHelp("a | b | c").getLtlTree();
        assertEquals(expectedTestResult4.toString(), testCase4.toString());

        U aub5 = new U(new Variable("a"), new Variable("b"));
        Or expectedTestResult5 = new Or(aub5, new Variable("c"));
        Formula testCase5 = testEnterFormulaHelp("(a U b | c)").getLtlTree();
        assertEquals(expectedTestResult5.toString(), testCase5.toString());

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
        F expectedTestResult999 = new F(gaobucanaubuxc);
        Formula testCase999 = testEnterFormulaHelp("F G (a | b U c) & !a U (b U (X c))").getLtlTree();
        assertEquals(expectedTestResult999.toString(), testCase999.toString());
    }
}