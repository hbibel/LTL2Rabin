package ltl2rabin;

import ltl2rabin.LTL.LTLListener;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


// Note to self: When parsing several LTLs, don't forget to call Variable.cachedBDDs.clear() in between to clean up
// the variable space.

public class Main {

    public static void main(String[] args) {
        System.out.println("This program runs, but does not do anything useful yet.");
        GDRAFactory gdraFactory = new GDRAFactory();
        gdraFactory.createFrom("(a U b) & (G c)");
    }

    public static LTLListener stringToLTLFormula (String s) {
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
}
