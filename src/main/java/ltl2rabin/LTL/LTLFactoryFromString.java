package ltl2rabin.LTL;

import com.google.common.collect.Sets;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * This is the factory that constructs an LTL formula from a String, using antlr4.
 */
public class LTLFactoryFromString extends LTLFactoryWithAntlr<String> {
    @Override
    public Result buildLTL(String input) {
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(input);
        LTLLexer lexer = new LTLLexer(antlrInputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LTLParser parser = new LTLParser(tokens);
        ParserRuleContext tree = parser.formula();
        ParseTreeWalker walker = new ParseTreeWalker();
        LTLListener extractor = new LTLListener(parser);
        walker.walk(extractor, tree);
        return new Result(extractor.getLtlTree(), Sets.powerSet(extractor.getTerminalSymbols()), extractor.getgFormulas());
    }
}
