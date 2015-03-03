package ltl2rabin;

import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

public class LTLListenerTest {

    @Test
    public void testEnterFormula() throws Exception {
        ANTLRInputStream input = new ANTLRInputStream("F a | ( (tt & b))");
        LTLLexer lexer = new LTLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();

        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);
    }

    @Test
    public void testExitFormula() throws Exception {

    }

    @Test
    public void testEnterFormulainparentheses() throws Exception {

    }

    @Test
    public void testExitFormulainparentheses() throws Exception {

    }

    @Test
    public void testEnterOrformula() throws Exception {

    }

    @Test
    public void testExitOrformula() throws Exception {

    }

    @Test
    public void testEnterAndformula() throws Exception {

    }

    @Test
    public void testExitAndformula() throws Exception {

    }

    @Test
    public void testEnterUformula() throws Exception {

    }

    @Test
    public void testExitUformula() throws Exception {

    }

    @Test
    public void testEnterAtom() throws Exception {

    }

    @Test
    public void testExitAtom() throws Exception {

    }

    @Test
    public void testEnterEveryRule() throws Exception {

    }

    @Test
    public void testExitEveryRule() throws Exception {

    }

    @Test
    public void testVisitTerminal() throws Exception {

    }

    @Test
    public void testVisitErrorNode() throws Exception {

    }
}